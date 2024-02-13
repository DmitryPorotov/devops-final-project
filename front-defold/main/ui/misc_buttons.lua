local _M = {}

function _M:init()
	self.save_load_menu = gui.get_node("save_load/back_drop")
	self.save_load_button = gui.get_node("save_load_button")
end

function _M:check_button_pressed(x, y)
	if gui.pick_node(self.save_load_button, x, y) then
		gui.set_enabled(self.save_load_menu, true)
		return true
	end
	return false
end

return _M