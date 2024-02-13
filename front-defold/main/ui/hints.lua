local _M = {
	on_goto_button_pressed = nil,
	on_next_button_pressed = nil,
	set_has_order = nil
}

function _M:init()
	self.hints = gui.get_node("hints/hints")
	self.goto_button = gui.get_node("hints/goto")
	self.goto_count = gui.get_node("hints/count")
	self.hint_text = gui.get_node("hints/hint")
	self.next_button = gui.get_node("hints/next")
end

function _M:set_hints_enabled(is_enabled)
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

function _M:set_hint_text(text)
	gui.set_text(self.hint_text, text)
end

local function orders_confirmed(self)
	msg.post("/map", "set_phase", {phase = "openOrders"})
	gui.set_enabled(self.hints, false)
end

function _M:check_button_pressed(x, y)
	if gui.pick_node(self.goto_button, x, y) then
		self.on_goto_button_pressed()
		return true
	elseif gui.pick_node(self.next_button, x, y) then
		self.on_next_button_pressed()
		return true
	end
	return false
end

return _M
