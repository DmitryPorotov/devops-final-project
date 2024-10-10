local event_dispatcher = require "main/ui/event_dispatcher"
local utils = require "main/utils"
local events = require "main/ui/events"

local POSITION_Y_STEP = 57

local _M = {
	panels = {},
	buttons = {},
	order_texts = {},
	is_enabled = false,
	starting_position = vmath.vector3(),
	enabled_panels = {},
}

function _M:init()
	for i = 1, 4 do
		self.panels[i] = {
			p = gui.get_node('hints/march' .. i .. '/panel'),
			id = false
		}
		self.buttons[i] = gui.get_node('hints/march'..i..'/remove')
		self.order_texts[i] = gui.get_node('hints/march'..i..'/order')
	end
	self.starting_position = gui.get_position(self.panels[1].p)
end

local function calc_y_offset(panels)
	local count = -1
	for _, v in pairs(panels) do
		if gui.is_enabled(v.p) then
			count = count + 1
		end
	end
	return count * POSITION_Y_STEP
end

---
---@param enabled boolean
function _M:set_enabled(enabled)
	self.is_enabled = enabled
	if not enabled then
		for _, v in ipairs(self.panels) do
			gui.set_enabled(v.p, false)
		end
		self.enabled_panels = {}
	end
end

function _M:add_order(id, text)
	self.is_enabled = true
	for i, v in ipairs(self.panels) do
		if not gui.is_enabled(v.p) then
			gui.set_enabled(v.p, true)
			v.id = id
			gui.set_text(self.order_texts[i], text)
			local pos_y = self.starting_position.y
			pos_y = pos_y + calc_y_offset(self.panels)
			gui.set_position(v.p, vmath.vector3(
					self.starting_position.x,
					pos_y,
					0
			))
			self.enabled_panels[#self.enabled_panels + 1] = v
			break
		end
	end
	if self.enabled_panels[#self.enabled_panels] and self.enabled_panels[#self.enabled_panels -1] then
		gui.move_below(self.enabled_panels[#self.enabled_panels].p, self.enabled_panels[#self.enabled_panels - 1].p)
	end
end

local function find_order_panels_above_me(panels, me)
	local result = {}
	for _, v in pairs(panels) do
		if gui.is_enabled(v.p) and gui.get_position(v.p).y > gui.get_position(me.p).y then
			result[#result + 1] = v
		end
	end
	return result
end

local function remove_order_panel(self, to_remove)
	table.remove(self.enabled_panels, utils.index_of(self.enabled_panels, to_remove))
	gui.set_enabled(to_remove.p, false)
	for i, v in ipairs(find_order_panels_above_me(self.panels, to_remove)) do
		local pos = gui.get_position(v.p)
		pos.y = pos.y - POSITION_Y_STEP
		gui.set_position(v.p, pos)
	end
end

function _M:check_button_pressed(x, y)
	if not self.is_enabled then return false end
	for i, v in ipairs(self.panels) do
		if gui.is_enabled(v.p) then
			if gui.pick_node(self.buttons[i], x, y) then
				event_dispatcher.trigger(events.partial_march_remove, v.id)
				remove_order_panel(self, v)
				return true
			end
		end
	end
	return false
end


function _M:check_pressed()
	return false
end


return _M