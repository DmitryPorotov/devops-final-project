local utils = require "main/utils"

local _M = {
	ORDER_TYPES = {
		"consolidate",
		"raid",
		"march",
		"defend",
		"support",
	},
	buttons = {},
	for_label = nil,
	button_selected = nil,
	placed_orders = {},

	stars_available = 3,
	stars_locks = nil
}

local count_stars_used, lock_stars_disable_special_buttons

function _M:init()
	self.panel = gui.get_node("orders/orders_panel")
	for _, v in ipairs(self.ORDER_TYPES) do
		for i = 1, 3 do
			self.buttons[v .. i] = {
				node = gui.get_node("orders/" .. v .. i),
				label = nil
			}
		end
	end
	self.stars_locks = {
		gui.get_node("orders/star-lock2"),
		gui.get_node("orders/star-lock1"),
		gui.get_node("orders/star-lock3")
	}
	self:set_stars_available(1)
end

function _M:set_stars_available(stars_available)
	self.stars_available = stars_available
	for i = 1, 3 do
		gui.set_enabled(gui.get_node("orders/star" .. i), i <= stars_available)
	end
	lock_stars_disable_special_buttons(self, count_stars_used(self))
end

function lock_stars_disable_special_buttons(self, num_stars_used)
	if self.stars_available > 1 then
		for i = 1, self.stars_available do
			gui.set_enabled(self.stars_locks[i], i <= num_stars_used)
		end
	else
		gui.set_enabled(self.stars_locks[2], num_stars_used > 0)
	end

	for _, v in ipairs(self.ORDER_TYPES) do
		local button_name = v .. 3
		if num_stars_used == self.stars_available then
			if self.buttons[button_name].label ~= "over_lim" and not self.buttons[button_name].label then
				gui.set_enabled(gui.get_node("orders/" .. button_name .. "-lock"), true)
				self.buttons[button_name].label = "over_lim"
			end
		else
			if self.buttons[button_name].label == "over_lim" then
				gui.set_enabled(gui.get_node("orders/" .. button_name .. "-lock"), false)
				self.buttons[button_name].label = nil
			end
		end
	end
end

function count_stars_used(self)
	local count = 0
	for _, v in ipairs(self.ORDER_TYPES) do
		if self.buttons[v .. 3].label and self.buttons[v .. 3].label ~= "over_lim" then 
			count = count + 1
		end
	end
	return count
end

local function enable_button(self, button_name)
	gui.set_color(self.buttons[button_name].node, vmath.vector4(1,1,1,1))
	self.buttons[button_name].label = nil
	lock_stars_disable_special_buttons(self, count_stars_used(self))
end

local function disable_button(self)
	gui.set_color(self.buttons[self.button_selected].node, vmath.vector4(1,1,1,.3))
	self.buttons[self.button_selected].label = self.for_label
	self.for_label = nil
	self.button_selected = nil
	lock_stars_disable_special_buttons(self, count_stars_used(self))
end

function _M:open(for_label, deleted)
	if self.for_label and self.for_label == for_label then
		self:close()
		self.for_label = nil
		return
	end
	if deleted then
		enable_button(self, deleted)
	end
	self.for_label = for_label
	gui.set_enabled(self.panel, true)
	gui.animate(self.panel, "position.x", 1050, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
end

local function on_closed(self, message_id, message, sender)
	gui.set_enabled(_M.panel, false)
end

function _M:close()
	gui.animate(self.panel, "position.x", 1350, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME, 0, on_closed)
end

function _M:get_button_pressed(x, y)
	for k, v in pairs(self.buttons) do
		if v.label then
			goto continue
		end
		if gui.pick_node(v.node, x, y) then
			self.button_selected = k
			self:close()
			return true
		end
		::continue::
	end
	return false
end

function _M:add_order_to_map()
	msg.post("/map", "add_order", {order = self.button_selected, label = self.for_label})
	disable_button(self)
	-- gui.set_color(self.buttons[self.button_selected].node, vmath.vector4(1,1,1,.3))
	-- self.buttons[self.button_selected].label = self.for_label
	-- self.for_label = nil
	-- self.button_selected = nil
	-- TODO send addOrder to the server
end

return _M