local ws = require "main/messaging/websocket"

local _M = {}

function _M:init()
	self.save_load_menu = gui.get_node("save_load/back_drop")
	self.save_button = gui.get_node("save_load/save_game")
	self.load_button = gui.get_node("save_load/load_game")
	self.close_button = gui.get_node("save_load/close")
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.save_load_menu) then
		if gui.pick_node(self.close_button, x, y) then
			gui.set_enabled(self.save_load_menu, false)
			msg.post('/camera', 'take_focus')
			return true
		elseif gui.pick_node(self.save_button, x, y) then
			ws.send({
				type = "action",
				action = "save",
				saveName = "name"
			})
			gui.set_enabled(self.save_load_menu, false)
			return true
		elseif gui.pick_node(self.load_button, x, y) then --note: should be another button
			ws.send({
				type = "action",
				action = "list_saves",
			})
			return true
		elseif gui.pick_node(self.save_load_menu, x, y) then
			return true
		end
	end
	return false
end

return _M