local utils = require "main/utils"

local _M = {}

function _M:init()
	self.panel = gui.get_node("raven_card_or_order/panel")
	self.order_button = gui.get_node("raven_card_or_order/order")
	self.card_button = gui.get_node("raven_card_or_order/card")
end

function _M:open()
	gui.set_enabled(self.panel, true)
	gui.animate(self.panel, "position.x", 1075, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
end

local selected = false

function _M:close()
	gui.animate(
		self.panel, 
		"position.x",
		1325,
		gui.PLAYBACK_ONCE_FORWARD,
		utils.ANIMATION_TIME,
		function()
			gui.set_enabled(self.panel, true)
		end
	)
	selected = false
end

function _M:check_button_pressed(x, y)
	if gui.pick_node(self.order_button, x, y) then
		selected = "order"
		return true
	elseif gui.pick_node(self.card_button, x, y) then
		selected = "card"
		return true
	end
	return false
end

function _M:get_selected()
	return selected
end

return _M