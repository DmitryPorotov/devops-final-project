local ws = require "main/messaging/websocket"

local _M = {}

function _M:init() 
	self.menu = gui.get_node("list_of_saves/back_drop")
	self.load_button = gui.get_node("list_of_saves/load_game")
	self.close_button = gui.get_node("list_of_saves/close")
	self.saves_list = gui.get_node("list_of_saves/saves")
	self.save_list_item = gui.get_node("list_of_saves/save_tmp")
	self.save_list_items = {}
	self.save_selected = nil
end

local function check_save_pressed(self, x, y)
	for i, v in ipairs(self.save_list_items) do
		if gui.pick_node(v[1], x, y) then
			if self.save_selected then
				gui.set_color(self.save_selected[2], vmath.vector4(0,0,0,1))
				gui.set_color(self.save_selected[1], vmath.vector4(1,1,1,1))
				self.save_selected = nil
			end
			gui.set_color(v[1], vmath.vector4(0,0,0,1))
			gui.set_color(v[2], vmath.vector4(1,1,1,1))
			self.save_selected = v
			return true
		end
	end
	return false
end

local function load_game(self)
	if not self.save_selected then
		return
	else
		local saveName = gui.get_text(self.save_selected[2])
		ws.send({
			type = 'action',
			action = 'load',
			saveName = saveName
		})
		gui.set_enabled(self.menu, false)
		gui.set_enabled(gui.get_node("save_load/back_drop"), false)
	end
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.menu) then
		if gui.pick_node(self.close_button, x, y) then
			gui.set_enabled(self.menu, false)
			return true
		elseif gui.pick_node(self.load_button, x, y) then
			load_game(self)
			return true
		elseif check_save_pressed(self, x, y) then
			return true
		end
	end
	return false
end


function _M:show_saves(saves)
	gui.set_enabled(self.menu, true)
	self.saves = saves
	local start_y = gui.get_position(self.save_list_item).y
	for i, v in ipairs(self.saves) do
		local s = gui.clone_tree(self.save_list_item)
		local box = s[hash('list_of_saves/save_tmp')]
		local label = s[hash('list_of_saves/save_label_tmp')]

		gui.set_text(label, v)
		gui.set_parent(box, self.saves_list)
		gui.set_id(box, 'list_of_saves/save_' .. i)
		gui.set_id(label, 'list_of_saves/save_label_' .. i)
		gui.set_enabled(box, true)
		gui.set_position(box, vmath.vector3(0,(start_y - (50 * (i - 1))),0))

		self.save_list_items[i] = {box,label}
		local a = 0
	end
end

return _M