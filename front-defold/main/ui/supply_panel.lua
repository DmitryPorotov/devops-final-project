local supply_logic = require 'main/ui/supply_logic'
local army_logic = require 'main/ui/army_logic'
local game_data = require 'main/ui/game_data'

local _M = {
	current_count = -1,
	available_count = 0,
	x = -105,
	y = 112,
	step = 50,
	usage_rules = nil,
	cur_max_armies = nil,
}

function _M:init()
	self.current_sup_node = gui.get_node("sup_current")
	self.available_sup_node = gui.get_node("sup_available")
	self.sup_panel = gui.get_node("supply_panel")
end

function _M:set_supply_usage_rules(usage)
	self.usage_rules = usage
end

function _M:check_pressed(x, y)
	return gui.pick_node(self.sup_panel, x, y)
end

_M.check_button_pressed = _M.check_pressed

local function update_panel(self)
	if not self.usage_rules or self.current_count < 0 then return end
	local cur_count
	if self.current_count > 6 then
		cur_count = 6
	else
		cur_count = self.current_count
	end
	self.cur_max_armies = self.usage_rules[cur_count + 1]
	local avail_helm_count = #self.cur_max_armies
	gui.set_position(self.sup_panel, vmath.vector3(self.x + (self.step * avail_helm_count), self.y ,0))
	for i = 1, 5 do
		local node = gui.get_node("sup" .. i)
		local do_enable = avail_helm_count >= i
		gui.set_enabled(node, do_enable)
	end
	gui.set_enabled(gui.get_node("sup_dot13"), cur_count > 0)
	gui.set_enabled(gui.get_node("sup_dot14"), cur_count > 4)
	gui.set_enabled(gui.get_node("sup_dot23"), cur_count > 3)
end 

local function update_counter_color(self)
	if self.current_count == self.available_count then
		gui.set_color(self.available_sup_node, vmath.vector4(1,1,1,1))
	elseif self.current_count < self.available_count then
		gui.set_color(self.available_sup_node, vmath.vector4(0,1,0,1))
	else
		gui.set_color(self.available_sup_node, vmath.vector4(1,0,0,1))
	end
end

function _M:set_current(count)
	self.current_count = count
	gui.set_text(self.current_sup_node, tostring(count))
	update_counter_color(self)
	update_panel(self)
end

function _M:set_available(count)
	self.available_count = count
	gui.set_text(self.available_sup_node, tostring(count))
	update_counter_color(self)
end

local function get_dot_color(flag)
	return flag and vmath.vector4(1,1,0,1) or vmath.vector4(1,1,1,1)
end

function _M:update_usage(armies)
	local counts = supply_logic.order_by_units_per_tile(army_logic:house_armies_to_gui_format(game_data.me, armies))
	for i = 1, math.min(#self.cur_max_armies, #counts) do
		for j = 1,  self.cur_max_armies[i] do
			gui.set_color(gui.get_node("sup_dot" .. i .. j),
					get_dot_color(j <= counts[i].c)
			)
		end
	end
end

return _M
