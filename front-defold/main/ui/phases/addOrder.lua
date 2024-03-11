local utils = require "main/utils"
local game_data = require "main/ui/game_data"

local _M = {
	tiles_with_hints = {},
	current_area = 1,
	goto_count = -1,
	addOrder_text = "Place an Order",
	ready_text = "Confirm orders",
	hints_gui = nil,
	ws_send = nil
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
	self.hints_gui:set_goto_count_text(count)
	self.hints_gui:set_goto_button_enabled(count ~= 0)
	self.hints_gui:set_next_button_enabled(count == 0)
	self.hints_gui:set_hint_text(count == 0 and self.ready_text or self.addOrder_text)
end

local function orders_confirmed(self)
	msg.post("/map", "set_phase", {phase = "openOrders"})
	self.hints_gui:set_hints_enabled(false)
end

function _M:init(ws_send, hints_gui, player_panels_gui, my_armies, my_orders, phase)
	self.ws_send = ws_send
	self.hints_gui = hints_gui
	self.player_panels_gui = player_panels_gui
	hints_gui.on_goto_button_pressed = function()
		self:on_goto_button_pressed()
	end
	hints_gui.on_next_button_pressed = function()
		self:on_next_button_pressed()
	end
	hints_gui.set_has_order = function(tile_num, has_order)
		self:set_has_order(tile_num, has_order)
	end
	for tile_num, v in pairs(my_armies) do
		if utils.is_unit_commandable(v[1].type) or #v > 1 then
			table.insert(self.tiles_with_hints, { tile_num = tile_num, has_order = my_orders[tile_num] ~= nil})
		end
	end
	update_hint_text(self)
	self.hints_gui:set_hints_enabled(true)
	for _, v in ipairs(phase.houseTypes) do
		if v == game_data.me then
			orders_confirmed(self)
		end
		self.player_panels_gui:set_player_ready(v)
	end
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
	self.ws_send({
		type = "action",
		action = "game_action",
		player_action = {
			actionType = "openOrders",
		}
	})
end

return _M