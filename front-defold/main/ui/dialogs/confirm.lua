local utils = require "main/utils"

local _M = {
	panel = nil,
	ok_button = nil,
	cancel_button = nil,
	question = nil,
	on_result = utils.noop
}

function _M:init()
	self.panel = gui.get_node('confirm/panel')
	self.ok_button = gui.get_node('confirm/ok')
	self.cancel_button = gui.get_node('confirm/cancel')
	self.question = gui.get_node('confirm/question')
end

--- Open confirm dialog
---@param text string
---@param on_result function A callback for result. Receives a boolean.
function _M:open(text, on_result)
	if on_result then
		self.on_result = on_result
	end
	gui.set_text(self.question, text)
	gui.set_enabled(self.panel, true)
end

function _M:check_pressed(x, y)
	return gui.is_enabled(self.panel) and gui.pick_node(self.panel, x, y)
end

function _M:clean_up()
	gui.set_enabled(self.panel, false)
	self.on_result = utils.noop
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.panel) then
		if gui.pick_node(self.ok_button, x, y) then
			self.on_result(true)
			self:clean_up()
			return true
		elseif gui.pick_node(self.cancel_button, x, y) then
			self.on_result(false)
			self:clean_up()
			return true
		end
		return gui.pick_node(self.panel, x, y)
	end
	return false
end

return _M
