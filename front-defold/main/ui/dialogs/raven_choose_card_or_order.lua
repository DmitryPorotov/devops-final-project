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
		0,
		function()
			gui.set_enabled(self.panel, false)
			selected = false
		end
	)
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.panel) and not selected then
		if gui.pick_node(self.order_button, x, y) then
			selected = "changeOrder"
			return true
		elseif gui.pick_node(self.card_button, x, y) then
			selected = "lookAtWildlingsCard"
			return true
		end
	end
	return false
end

function _M:build_message()
	local m = {
		type = 'action',
		action = 'game_action',
		player_action = {
			actionType = 'ravenChooseChangeOrderOrLookAtWildlingCard',
			ravenChoice = selected
		}
	}
	return m
end

return _M