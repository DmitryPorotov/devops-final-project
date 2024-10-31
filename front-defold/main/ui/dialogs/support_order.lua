local event_dispatcher = require "main/ui/event_dispatcher"
local events = require "main/ui/events"

local TO_SHIFT_PIXELS = 1

---@module SupportOrderPanel
local _M = {
	attacker_btn_pressed = false,
	defender_btn_pressed = false,
	no_one_btn_pressed = false,
}

function _M:init()
	self.panel = gui.get_node('support_order/container')
	self.attacker_btn = gui.get_node('support_order/attacker_btn')
	self.attacker_btn_bg = gui.get_node('support_order/attacker_bg/button')
	self.defender_btn = gui.get_node('support_order/defender_btn')
	self.defender_btn_bg = gui.get_node('support_order/defender_bg/button')
	self.no_one_btn = gui.get_node('support_order/no_one_button/button')
	self.send_btn = gui.get_node('support_order/send_btn')
end

function _M:check_pressed(x, y)
	return gui.is_enabled(self.panel) and gui.pick_node(self.panel, x, y)
end

function _M:press_button(btn, btn_bg)
	local pos = gui.get_position(btn)
	gui.play_flipbook(btn_bg, hash('button2-pressed'))
	gui.animate(btn, gui.PROP_POSITION, vmath.vector3(pos.x + TO_SHIFT_PIXELS, pos.y - TO_SHIFT_PIXELS, pos.z), gui.EASING_LINEAR, 0.1, 0)
end

function _M:unpress_button(btn, btn_bg)
	local pos = gui.get_position(btn)
	gui.play_flipbook(btn_bg, hash('button2'))
	gui.animate(btn, gui.PROP_POSITION, vmath.vector3(pos.x - TO_SHIFT_PIXELS, pos.y + TO_SHIFT_PIXELS, pos.z), gui.EASING_LINEAR, 0.1, 0)
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.panel) then
		if gui.pick_node(self.attacker_btn, x, y) then
			if not self.attacker_btn_pressed then
				self.attacker_btn_pressed = true
				self:press_button(self.attacker_btn, self.attacker_btn_bg)
			end
			if self.defender_btn_pressed then
				self.defender_btn_pressed = false
				self:unpress_button(self.defender_btn, self.defender_btn_bg)
			elseif self.no_one_btn_pressed then
				self.no_one_btn_pressed = false
				self:unpress_button(self.no_one_btn, self.no_one_btn)
			end
		elseif gui.pick_node(self.defender_btn, x, y) then
			if not self.defender_btn_pressed then
				self.defender_btn_pressed = true
				self:press_button(self.defender_btn, self.defender_btn_bg)
			end
			if self.attacker_btn_pressed then
				self.attacker_btn_pressed = false
				self:unpress_button(self.attacker_btn, self.attacker_btn_bg)
			elseif self.no_one_btn_pressed then
				self.no_one_btn_pressed = false
				self:unpress_button(self.no_one_btn, self.no_one_btn)
			end
		elseif gui.pick_node(self.no_one_btn, x, y) then
			if not self.no_one_btn_pressed then
				self.no_one_btn_pressed = true
				self:press_button(self.no_one_btn, self.no_one_btn)
			end
			if self.attacker_btn_pressed then
				self.attacker_btn_pressed = false
				self:unpress_button(self.attacker_btn, self.attacker_btn_bg)
			elseif self.defender_btn_pressed then
				self.defender_btn_pressed = false
				self:unpress_button(self.defender_btn, self.defender_btn_bg)
			end
		elseif gui.pick_node(self.send_btn, x, y) then
			local support_who
			if self.attacker_btn_pressed then
				support_who = 'attacker'
			elseif self.defender_btn_pressed then
				support_who = 'defender'
			elseif self.no_one_btn_pressed then
				support_who = 'no_one'
			end
			if support_who then
				event_dispatcher.trigger(events.support_order_send_click, support_who)
			end
		end
	end
	return false
end

return _M