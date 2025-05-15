local _M = {}

function _M:init(enable_save_load)
	self.save_load_menu = gui.get_node("save_load/back_drop")
	self.log_window = gui.get_node("log/back_drop")
	self.save_load_button = gui.get_node("save_load_button")
	self.log_button = gui.get_node("log_button")
	gui.set_enabled(self.save_load_button, enable_save_load)
end

local node_clicked = false

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.save_load_button) and gui.pick_node(self.save_load_button, x, y) then
		node_clicked = self.save_load_menu
		self:do_action()
		return true
	end
	if gui.pick_node(self.log_button, x, y) then
		node_clicked = self.log_window
		self:do_action()
		return true
	end
	return false
end

function _M:check_pressed(x, y)
	if gui.is_enabled(self.save_load_button) and gui.pick_node(self.save_load_button, x, y) then
		return true
	elseif gui.pick_node(self.log_button, x, y) then
		return true
	end
	return false
end

function _M:do_action()
	gui.set_enabled(node_clicked, true)
	msg.post("/camera", "release_focus")
end

return _M