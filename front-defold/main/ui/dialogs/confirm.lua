local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {
	panel = nil,
	ok_button = nil,
	cancel_button = nil,
	question = nil,
}

function _M:init()
	self.panel = gui.get_node('confirm/panel')
	self.ok_button = gui.get_node('confirm/ok')
	self.cancel_button = gui.get_node('confirm/cancel')
	self.question = gui.get_node('confirm/question')
end

function _M:check_pressed(x, y)
	return gui.is_enabled(self.panel) and gui.pick_node(self.panel, x, y)
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.panel) then
		if gui.pick_node(self.ok_button, x, y) then
			event_dispatcher.trigger('confirm_ok_click')
			return true
		elseif gui.pick_node(self.cancel_button, x, y) then
			event_dispatcher.trigger('confirm_cancel_click')
			return true
		end
	end
	return false
end

return _M