local utils = require "main/utils"
local _M = {
	ORDER_TYPES = {
		"consolidate",
		"raid",
		"march",
		"defend",
		"support",
	},
	buttons = {},
	for_label = nil,
	button_selected = nil,
	placed_orders = {},
}

function _M.init(self)
	self.panel = gui.get_node("orders/orders_panel")
	for _, v in ipairs(self.ORDER_TYPES) do
		for i = 1, 3 do
			self.buttons[v .. i] = gui.get_node("orders/" .. v .. i)
		end
	end
end

function _M.open(self, for_label)
	if self.for_label and self.for_label == for_label then
		self:close()
		self.for_label = nil
		return
	end
	self.for_label = for_label
	gui.set_enabled(self.panel, true)
	gui.animate(self.panel, "position.x", 1050, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
end

local function on_closed(self, message_id, message, sender)
	gui.set_enabled(_M.panel, false)
end

function _M.close(self)
	gui.animate(self.panel, "position.x", 1350, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME, 0, on_closed)
end

function _M.get_button_pressed(self, x, y)
	for k, v in pairs(self.buttons) do
		if gui.pick_node(v, x, y) then
			self.button_selected = k
			self:close()
			return true
		end
	end
	return false
end

function _M.add_order_to_map(self)
	msg.post("/map", "add_order", {order = self.button_selected, label = self.for_label})
	self.for_label = nil
	self.button_selected = nil
	-- TODO send addOrder to the server
end

return _M