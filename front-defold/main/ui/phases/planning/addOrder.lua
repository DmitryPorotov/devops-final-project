local utils = require "main/utils"
local game_data = require "main/ui/game_data"
local hints = require "main/ui/hints"
local player_panels = require "main/ui/player_panel"
local orders = require "main/ui/orders"

local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {
	tiles_with_hints = {},
	current_area = 1,
	goto_count = -1,
	addOrder_text = "Place an Order",
	ready_text = "Confirm orders",
}

local function count_tiles_without_orders(self)
	local count = 0
	for _, v in ipairs(self.tiles_with_hints) do
		if not v.has_order then
			count = count + 1
		end
	end
	return count
end

local function get_next_hint(self)
	local function next_hint(self)
		if self.current_area > #self.tiles_with_hints then
			self.current_area = 1
		end
		if self.tiles_with_hints[self.current_area].has_order then
			self.current_area = self.current_area + 1
			return next_hint(self)
		else
			local next_ = self.tiles_with_hints[self.current_area].tile_num
			self.current_area = self.current_area + 1
			return next_
		end
	end
	return next_hint(self)
end

local function update_hint_text(self)
	local count = count_tiles_without_orders(self)
	hints:set_goto_count_text(count)
	hints:set_goto_button_enabled(count ~= 0)
	hints:set_next_button_enabled(count == 0)
	hints:set_hint_text(count == 0 and self.ready_text or self.addOrder_text)
end

local function orders_confirmed(self)
	msg.post("/map", "set_phase", {phase = "openOrders"})
	hints:set_hints_enabled(false)
end

local function on_map_show_orders_menu(self, message)
	orders:open(
		message.label,
		message.tile_num,
		message.name,
		message.deleted,
		false
	)
	if message.deleted then
		self:set_has_order(message.tile_num, false)
		event_dispatcher.trigger('ws_send',{
			type = "action",
			action = "game_action",
			player_action = {
				actionType = "removeOrder",
				tileNumber = tonumber(message.tile_num),
			}
		})
	end
end

local function on_order_button_click(self)
	local order = orders:get_order_to_send()
	self:set_has_order(order.player_action.tileNumber, true)
	event_dispatcher.trigger('ws_send', order)
	orders:add_order_to_map()
end

local function on_ws_add_order(reply)
	local order = reply.player_action.order or {
		type = "consolidatePower"
	}
	local to_send = {
		[reply.player_action.houseType] = {
			[reply.player_action.tileNumber] = order
		}
	}
	orders:show_orders_on_map(to_send, reply.player_action.order and true or false)
end

local function on_ws_open_orders(reply)
	player_panels:set_player_ready(reply.player_action.houseType)
	if reply.player_action.orders then
		orders:show_orders_on_map(reply.player_action.orders, true)
		game_data.placedOrders = reply.player_action.orders
	end
end

function _M:init(armies, my_orders, phase)
	event_dispatcher.on('hints_goto_button_click', function()
		self:on_goto_button_pressed()
	end)
	event_dispatcher.on('hints_next_button_click', function()
		self:on_next_button_pressed()
	end)
	local my_armies = utils.filter_my_armies(armies, game_data.me)
	for tile_num, v in pairs(my_armies) do
		if utils.is_unit_commandable(v[1].type) or #v > 1 then
			table.insert(self.tiles_with_hints, { tile_num = tile_num, has_order = my_orders[tile_num] ~= nil})
		end
	end
	update_hint_text(self)
	hints:set_hints_enabled(true)
	for _, v in ipairs(phase.houseTypes) do
		if v == game_data.me then
			orders_confirmed(self)
		end
		player_panels:set_player_ready(v)
	end
	event_dispatcher.on('map_show_orders_menu', function(message)
		on_map_show_orders_menu(self, message)
	end)
	event_dispatcher.on('order_button_click', function()
		on_order_button_click(self)
	end)
	event_dispatcher.on('ws_add_order', on_ws_add_order)
	event_dispatcher.on('ws_open_orders', on_ws_open_orders)
end

function _M:clean_up()
	event_dispatcher.off('map_show_orders_menu')
	event_dispatcher.off('order_button_click')
	event_dispatcher.off('ws_add_order')
	event_dispatcher.off('ws_open_orders')
	event_dispatcher.off('hints_goto_button_click')
	event_dispatcher.off('hints_next_button_click')
	hints:clean_up()
end

function _M:set_has_order(tile_num, has_order)
	tile_num = tostring(tile_num)
	for _, v in ipairs(self.tiles_with_hints) do
		if v.tile_num == tile_num then
			v.has_order = has_order
			goto end_loop
		end
	end
	::end_loop::
	update_hint_text(self)
end

function _M:on_goto_button_pressed()
	if count_tiles_without_orders(self) == 0 then
		return
	end
	local n = get_next_hint(self)
	msg.post("/map", "move_camera_to_label", {tile_num = n})
end

function _M:on_next_button_pressed()
	orders_confirmed(self)
	event_dispatcher.trigger('ws_send', {
		type = "action",
		action = "game_action",
		player_action = {
			actionType = "openOrders",
		}
	})
end

return _M