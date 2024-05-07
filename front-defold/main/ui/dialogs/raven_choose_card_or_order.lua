local base = require "main/ui/dialogs/base_dialog"
local event_dispatcher = require "main/ui/event_dispatcher"

---@module RavenChooseCardOrOrder : BaseDialog
local _M = {}

setmetatable(_M, base)

function _M:init()
	self.panel = gui.get_node("raven_card_or_order/panel")
	self.order_button = gui.get_node("raven_card_or_order/order")
	self.card_button = gui.get_node("raven_card_or_order/card")
end

local selected = false

function _M:on_closed()
	selected = false
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.panel) and not selected then
		if gui.pick_node(self.order_button, x, y) then
			selected = "changeOrder"
			event_dispatcher.trigger('raven_card_or_order_click')
			return true
		elseif gui.pick_node(self.card_button, x, y) then
			selected = "lookAtWildlingsCard"
			event_dispatcher.trigger('raven_card_or_order_click')
			return true
		end
		return gui.pick_node(self.panel, x, y)
	end
	return false
end

function _M:build_message()
	local m = {
		player_action = {
			actionType = 'ravenChooseChangeOrderOrLookAtWildlingCard',
			ravenChoice = selected
		}
	}
	return m
end

return _M