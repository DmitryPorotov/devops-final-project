local utils = require "main/utils"
local game_data = require "main/ui/game_data"

local ws = require "main/messaging/websocket"

local _M = {
	tiles_with_hints = {},
	current_area = 1,
	addOrder_text = "Place an Order",
	ready_text = "Confirm orders"
}

local function count_tiles_without_orders()
	local count = 0
	for _, v in ipairs(_M.tiles_with_hints) do
		if not v.has_order then
			count = count + 1
		end
	end
	return count
end

local function get_next_hint()
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
	return next_hint(_M)
end

function _M:init()
	self.hints = gui.get_node("hints/hints")
	self.goto_button = gui.get_node("hints/goto")
	self.goto_count = gui.get_node("hints/count")
	self.hint_text = gui.get_node("hints/hint")
	self.next_button = gui.get_node("hints/next")
end

local function orders_confirmed(self)
	msg.post("/map", "set_phase", {phase = "openOrders"})
	gui.set_enabled(self.hints, false)
end

local function update_hint_text(self)
	local count = count_tiles_without_orders()
	gui.set_text(self.goto_count, count)
	gui.set_enabled(self.goto_button, count ~= 0)
	gui.set_enabled(self.next_button, count == 0)
	gui.set_text(self.hint_text, count == 0 and self.ready_text or self.addOrder_text)
end

function _M:set_has_order(tile_num, has_order)
	tile_num = tostring(tile_num)
	for i, v in ipairs(self.tiles_with_hints) do
		if v.tile_num == tile_num then
			v.has_order = has_order
			goto end_loop
		end
	end
	::end_loop::
	update_hint_text(self)
end

function _M:set_addOrder_hints(my_armies, my_orders, phase)
	for i, v in pairs(my_armies) do
		if utils.is_unit_commandable(v[1].type) or #v > 1 then
			table.insert(self.tiles_with_hints, {tile_num = i, has_order = my_orders[i] ~= nil})
		end
	end
	update_hint_text(self)
	gui.set_enabled(self.hints, true)
	-- todo move this from here
	if phase.subPhase == "addOrder" then
		for i, v in ipairs(phase.houseTypes) do
			if v == game_data.me then
				orders_confirmed(self)
			else
				-- todo update player panels
			end
		end
	end
end

function _M:get_button_pressed(x, y)
	if gui.pick_node(self.goto_button, x, y) then
		if count_tiles_without_orders() == 0 then
			return true
		end
		local n = get_next_hint()
		msg.post("/map", "move_camera_to_label", {tile_num = n})
		return true
	end
	if gui.pick_node(self.next_button, x, y) then
		orders_confirmed(self)
		ws.send({
			type = "action",
			action = "game_action",
			player_action = {
				actionType = "openOrders",
				houseType = game_data.me,	
			}
		})
		return true
	end
	return false
end

return _M
