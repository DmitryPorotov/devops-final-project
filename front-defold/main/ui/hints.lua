local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {}

function _M:init()
	self.hints = gui.get_node("hints/hints")
	self.bar = gui.get_node("hints/bar")
	self.goto_button = gui.get_node("hints/goto")
	self.goto_count = gui.get_node("hints/count")
	self.hint_text = gui.get_node("hints/hint")
	self.next_button = gui.get_node("hints/next")
end

function _M:clean_up()
	self:set_enabled(false)
end

function _M:set_enabled(is_enabled)
	gui.set_enabled(self.hints, is_enabled)
end

function _M:set_goto_button_enabled(is_enabled)
	gui.set_enabled(self.goto_button, is_enabled)
end

function _M:set_next_button_enabled(is_enabled)
	gui.set_enabled(self.next_button, is_enabled)
end

function _M:set_goto_count_text(text)
	gui.set_text(self.goto_count, text)
end

local function adjust_bar_size(self, text)
	local _, num_line_breaks = text:gsub('\n', '\n')
	local size = gui.get_size(self.bar)
	size.y = (num_line_breaks + 1) * 52
	gui.set_size(self.bar, size)
	gui.set_position(self.bar, vmath.vector3(0, (26 * num_line_breaks), 0))
end

function _M:set_hint_text(text)
	adjust_bar_size(self, text)
	gui.set_text(self.hint_text, text)
end

function _M:none_actionable_hint(text)
	self:set_enabled(true)
	self:set_goto_button_enabled(false)
	self:set_next_button_enabled(false)
	adjust_bar_size(self, text)
	self:set_hint_text(text)
end

function _M:check_pressed(x, y)
	if not gui.is_enabled(self.hints) then
		return false
	elseif gui.is_enabled(self.goto_button) and gui.pick_node(self.goto_button, x, y) then
		return true
	elseif gui.is_enabled(self.next_button) and gui.pick_node(self.next_button, x, y) then
		return true
	elseif gui.pick_node(self.bar, x, y) then
		return true
	end
	return false
end

function _M:check_button_pressed(x, y)
	if not gui.is_enabled(self.hints) then return false end

	if gui.is_enabled(self.goto_button) and gui.pick_node(self.goto_button, x, y) then
		event_dispatcher.trigger('hints_goto_button_click')
		return true
	elseif gui.is_enabled(self.next_button) and gui.pick_node(self.next_button, x, y) then
		event_dispatcher.trigger('hints_next_button_click')
		return true
	elseif gui.is_enabled(self.bar) and gui.pick_node(self.bar, x, y)  then
		event_dispatcher.trigger('hints_goto_button_click')
		return true
	end
	return false
end

return _M
