local event_dispatcher = require "main/ui/event_dispatcher"
local events = require "main/ui/events"
local army_logic = require "main/ui/army_logic"
local base = require "main/ui/dialogs/base_dialog"

---@class GUIArmyToSend
---@field footmen number
---@field knights number
---@field siegeEngines number
---@field ships number

---@module MarchSelectArmy : BaseDialog
local _M = {
	panel_name = 'march_select_army',
	controls = {},
	avail_counts = {},
	to_send = {},
	for_label = false,
	army_selected = false,
	target_selected = false,
	from = nil,
	to = nil,
	who = nil,
	closed_position = 1335,
	opened_position = 1065,
}

setmetatable(_M, base)

function _M:to_send_changed()
	local text = ''
	local to_send = self:get_to_send()
	if next(to_send) then
		for k, v in pairs(to_send) do
			text = text .. army_logic:build_unit_and_count_phrase(k ,v) .. ', '
		end
		text = text:sub(1, -3)
		gui.set_color(self.ok_button, vmath.vector4(1,1,1,1))
	elseif self.is_multi_march then
		gui.set_color(self.ok_button, vmath.vector4(1,1,1,.5))
	end
	self:set_who(text)
end

local function reset_counts(self)
	for _, v in ipairs(army_logic.unit_names) do
		self.avail_counts[v] = 0
		self.to_send[v] = 0
		gui.set_text(self.controls[v].label, '0/0')
	end
end

function _M:init()
	self.panel = gui.get_node(self.panel_name .. '/panel')
	for i, v in ipairs(army_logic.unit_names) do
		self.controls[v] = {}
		self.controls[v].en = false
		self.controls[v].p = gui.get_node(self.panel_name .. '/' .. army_logic.unit_names[i] .. '/container')
		self.controls[v].plus = gui.get_node(self.panel_name
				.. '/' .. army_logic.unit_names[i] .. '/plus_button')
		self.controls[v].minus = gui.get_node(self.panel_name
				.. '/' .. army_logic.unit_names[i] .. '/minus_button')
				self.controls[v].label = gui.get_node(self.panel_name 
				.. '/' .. army_logic.unit_names[i] .. '/count')
	end
	self.ok_button = gui.get_node(self.panel_name .. '/ok_button')
	self.from = gui.get_node('march_select_army/from')
	self.to = gui.get_node('march_select_army/to')
	self.who = gui.get_node('march_select_army/march')
	reset_counts(self)
end

function _M:set_from(territory)
	gui.set_text(self.from, 'From ' .. territory)
end

function _M:set_to(territory)
	gui.set_text(self.to, 'To ' .. territory)
	self:set_target_selected()
end

function _M:set_who(to_send)
	if to_send == '' then
		to_send = '...'
	end
	gui.set_text(self.who, 'March ' .. to_send)
end

function _M:get_order_text()
	return gui.get_text(self.from) .. ' ' .. gui.get_text(self.who) .. ' ' .. gui.get_text(self.to)
end

local function reset_panel(self)
	for _, v in pairs(self.controls) do
		v.en = false
		gui.set_color(v.p, vmath.vector4(1,1,1,0.5))
	end
	gui.set_color(self.ok_button, vmath.vector4(1,1,1,1))
	self.for_label = false
	self.army_selected = false
	self.target_selected = false
	reset_counts(self)
end

function _M:open_or_toggle(army, label)
	if label and label ~= self.for_label then
		reset_panel(self)
		self.for_label = label
	elseif label == self.for_label then
		self:close()
		return false
	end
	self:set_available_units(army, not label)
	if not gui.is_enabled(self.panel) then
		self:open()
	end
	return true
end

function _M:on_closed()
	reset_panel(self)
end

function _M:set_available_units(army, is_multi_march)
	self.is_multi_march = is_multi_march
	self.army_selected = false
	self.target_selected = false
	self:set_who('')
	gui.set_text(self.to, 'To ...')
	if is_multi_march then
		gui.set_color(self.ok_button, vmath.vector4(1,1,1,.5))
	else
		gui.set_color(self.ok_button, vmath.vector4(1,1,1,1))
	end
	reset_counts(self)
	self.avail_counts = army
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
	if self.army_selected then return end
	if self.to_send[type] < self.avail_counts[type] then
		self.to_send[type] = self.to_send[type] + 1
		self:to_send_changed()
		--event_dispatcher.trigger('march_select_army_to_send_changed', self:get_to_send())
	end
end

function _M:dec_to_send(type)
	if self.army_selected then return end
	if self.to_send[type] > 0 then
		self.to_send[type] = self.to_send[type] - 1
		self:to_send_changed()
		--event_dispatcher.trigger('march_select_army_to_send_changed', self:get_to_send())
	end
end

function _M:set_target_selected()
	self.target_selected = true
	gui.set_color(self.ok_button, vmath.vector4(1,1,1,1))
end

---@return GUIArmyToSend
function _M:get_to_send()
	local to_send = {}
	for k, v in pairs(self.to_send) do
		if v > 0 then
			to_send[k] = v
		end
	end
	return to_send
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.panel) then
		for k, v in pairs(self.controls) do
			if v.en then
				if gui.pick_node(v.plus, x, y) then
					self:enc_to_send(k)
					gui.set_text(v.label, self.to_send[k] .. '/' .. self.avail_counts[k])
					return true
				elseif gui.pick_node(v.minus, x, y) then
					self:dec_to_send(k)
					gui.set_text(v.label, self.to_send[k] .. '/' .. self.avail_counts[k])
					return true
				end
			end
		end
		if gui.is_enabled(self.ok_button) and gui.pick_node(self.ok_button, x, y) then
			if self.is_multi_march and not next(self:get_to_send()) then
				return
			end
			if not self.army_selected then
				self.army_selected = true
				for _, v in pairs(self.controls) do
					gui.set_color(v.p, vmath.vector4(1,1,1,0.5))
					v.en = false
				end
				gui.set_color(self.ok_button, vmath.vector4(1,1,1,0.5))
				event_dispatcher.trigger(events.march_select_army_ok_button_click, self:get_to_send())
			elseif self.target_selected then
				gui.set_color(self.ok_button, vmath.vector4(1,1,1,0.5))
				self.target_selected = false
				event_dispatcher.trigger(events.march_select_army_ok_button_click, false)
			end
			return true
		end
		return gui.pick_node(self.panel, x, y)
	end
	return false
end

return _M