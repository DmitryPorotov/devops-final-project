local labels = require "main/labels"

local utils = require "main/utils"
local game_data = require "main/ui/game_data"

local ws = require "main/messaging/websocket"

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
	tile_num = nil,
	button_selected = nil,
	placed_orders = {},

	stars_available = 3,
	stars_locks = nil,
	label_text = "Select an Order"
}

local count_stars_used, lock_stars_disable_special_buttons

function _M:init()
	self.panel = gui.get_node("orders/orders_panel")
	self.label = gui.get_node("orders/label")
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
end

function _M:calc_stars_available(court_track)
	local myIndex = utils.index_of(court_track, game_data.me)
	if game_data.gameRules.kingsCourtStars[myIndex] then
		self.stars_available = game_data.gameRules.kingsCourtStars[myIndex]
	else
		self.stars_available = 0
	end
	for i = 1, 3 do
		gui.set_enabled(gui.get_node("orders/star" .. i), i <= self.stars_available)
	end
	lock_stars_disable_special_buttons(self, count_stars_used(self))
end

function lock_stars_disable_special_buttons(self, num_stars_used) --local
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

function count_stars_used(self) --local
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
	self.tile_num = nil
	lock_stars_disable_special_buttons(self, count_stars_used(self))
end

function _M:open(for_label, tile_num, name, deleted)
	if self.for_label and self.for_label == for_label then
		self:close()
		self.for_label = nil
		self.tile_num = nil
		return
	end
	if deleted then
		enable_button(self, deleted)
		ws.send({
			type = "action",
			action = "game_action",
			player_action = {
				actionType = "removeOrder",
				houseType = game_data.me,
				tileNumber = tonumber(tile_num),
			}
		})
	end
	self.for_label = for_label
	self.tile_num = tile_num
	gui.set_enabled(self.panel, true)
	gui.animate(self.panel, "position.x", 1050, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
	gui.set_text(self.label, self.label_text .. "\nfor " .. name)
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

local function build_order_obj(button)
	local order = {
		type = string.match(button, "^(%a+)")
	}
	if order.type == "consolidate" then
		order.type = "consolidatePower"
	end
	local num = string.match(button, "(%d)")
	
	if num == "3" then
		order.isStar = true
		if order.type == "march" or order.type == "support" then
			order.modifier = 1
		elseif order.type == "defend" then
			order.modifier = 2
		end
	elseif num == "2" then
		if order.type == "defend" then
			order.modifier = 1
		end
	elseif num == "1" then
		if order.type == "defend" then
			order.modifier = 1
		elseif order.type == "march" then
			order.modifier = -1
		end
	end
	return order
end

function _M:get_order_to_send()
	return {
		type = "action",
		action = "game_action",
		player_action = {
			actionType = "addOrder",
			houseType = game_data.me,
			tileNumber = tonumber(self.tile_num),
			order = build_order_obj(self.button_selected)
		}
	}
end

function _M:add_order_to_map()
	msg.post("/map", "add_order", {
		order = self.button_selected,
		label = self.for_label,
		house = game_data.me,
		is_opened = true
	})
	disable_button(self)
end

function _M:show_orders_on_map(orders, do_open)
	for h, os in pairs(orders) do
		for t, o in pairs(os) do
			local order = o.type == "consolidatePower" and "consolidate" or o.type
			if o.isStar then
				order = order .. 3
			elseif o.type == "march" then
				if o.modifier == -1 then
					order = order .. 1
				else
					order = order .. 2
				end
			else
				if self.buttons[order .. 1].label then
					order = order .. 2
				else
					order = order .. 1
				end
			end
			if h == game_data.me then
				self.for_label = hash(labels.LABEL_IDS[tonumber(t)])
				self.button_selected = order
				self.tile_num = t
				disable_button(self)
			end
			msg.post("/map", "add_order", {
				order = order,
				label = tonumber(t),
				house = h,
				is_opened = (do_open and true or (h == game_data.me))
			})
		end
	end
end

return _M