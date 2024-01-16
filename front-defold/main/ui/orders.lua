-- Put functions in this file to use them in several other scripts.
-- To get access to the functions, you need to put:
-- require "my_directory.my_file"
-- in any script using the functions.
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

return _M