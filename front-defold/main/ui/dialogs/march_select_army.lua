local utils = require 'main/utils'
local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {
	panel_name = 'march_select_army',
	unit_names = {
		'footmen',
		'knights',
		'siegeEngines',
		'ships'
	},
	controls = {},
	avail_counts = {},
	to_send = {},
	for_label = false,
	is_sent = false,
}

local function reset_counts(self)
	for _, v in ipairs(self.unit_names) do
		self.avail_counts[v] = 0
		self.to_send[v] = 0
		gui.set_text(self.controls[v].label, '0/0')
	end
end

function _M:init()
	self.panel = gui.get_node(self.panel_name .. '/panel')
	for i, v in ipairs(self.unit_names) do
		self.controls[v] = {}
		self.controls[v].en = false
		self.controls[v].p = gui.get_node(self.panel_name .. '/' .. self.unit_names[i] .. '/container')
		self.controls[v].plus = gui.get_node(self.panel_name
				.. '/' .. self.unit_names[i] .. '/plus_button')
		self.controls[v].minus = gui.get_node(self.panel_name
				.. '/' .. self.unit_names[i] .. '/minus_button')
				self.controls[v].label = gui.get_node(self.panel_name 
				.. '/' .. self.unit_names[i] .. '/count')
	end
	self.ok_button = gui.get_node(self.panel_name .. '/ok_button')
	reset_counts(self)
end

local function reset_panel(self)
	for _, v in pairs(self.controls) do
		v.en = false
		gui.set_color(v.p, vmath.vector4(1,1,1,0.5))
	end
	self.for_label = false
	self.is_sent = false
	reset_counts(self)
end

function _M:open(army, label)
	if label ~= self.for_label then
		reset_panel(self)
		self.for_label = label
	else
		self:close()
		return
	end
	self:set_available_units(army)
	gui.set_enabled(self.panel, true)
	gui.animate(self.panel, "position.x", 1065, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
end

function _M:close()
	gui.animate(self.panel, "position.x", 1335, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME, 0,
			function()
				gui.set_enabled(self.panel, false)
				reset_panel(self)
			end
	)
end

function _M:set_available_units(army)
	for _, v in ipairs(army) do
		if utils.index_of(self.unit_names, v.type) and not v.isDefeated then
			self.avail_counts[v.type] = self.avail_counts[v.type] + 1
		end
	end
	for k, v in pairs(self.avail_counts) do
		if v > 0 then
			local panel = self.controls[k]
			gui.set_color(panel.p, vmath.vector4(1,1,1,1))
			panel.en = true
			gui.set_text(panel.label, '0/' .. v)
		end
	end
end

function _M:enc_to_send(type)
	if self.to_send[type] < self.avail_counts[type] then
		self.to_send[type] = self.to_send[type] + 1
	end
end

function _M:dec_to_send(type)
	if self.to_send[type] > 0 then
		self.to_send[type] = self.to_send[type] - 1
	end
end

function _M:check_pressed(x, y)
	return gui.is_enabled(self.panel) and gui.pick_node(self.panel, x, y)
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.panel) then
		for k, v in pairs(self.controls) do
			if not v.en then
				goto continue
			end
			if gui.pick_node(v.plus, x, y) then
				self:enc_to_send(k)
				gui.set_text(v.label, self.to_send[k] .. '/' .. self.avail_counts[k])
				return true
			elseif gui.pick_node(v.minus, x, y) then
				self:dec_to_send(k)
				gui.set_text(v.label, self.to_send[k] .. '/' .. self.avail_counts[k])
				return true
			end
			::continue::
		end
		if gui.pick_node(self.ok_button, x, y) then
			if not self.is_sent then
				self.is_sent = true
				local to_send = {}
				for i, v in pairs(self.to_send) do
					to_send[i] = v
				end
				event_dispatcher.trigger('march_select_army_ok_button_click', to_send)

			end
			return true
		end
	end
	return false
end

return _M