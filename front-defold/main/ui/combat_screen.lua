local player_panels = require "main/ui/player_panel"
local utils = require "main/utils"
local game_data = require "main/ui/game_data"
local house_card = require "main/ui/house_card"
local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {
	a_card_position = vmath.vector3(-14, -102, 0),
	a_card_rotation = vmath.vector3(0, 0, -5),
	d_card_position = vmath.vector3(14, -102, 0),
	d_card_rotation = vmath.vector3(0, 0, 5),
	---@private
	---@type HouseCardWrapper[]
	cards = {},
	---@private
	confirmed = false,
}

function _M:init()
	self.panel = gui.get_node('combat_screen/bg')
	self.place_text = gui.get_node('combat_screen/place_text')
	
	self.attacker_shield = gui.get_node('combat_screen/a_shield')
	self.attacker_house_text = gui.get_node('combat_screen/a_house_name_text')
	self.attacker_total_strength_text = gui.get_node('combat_screen/a_strength_shield_text')
	self.attacker_tide_of_battle_text = gui.get_node('combat_screen/a_tob_strength_text')
	self.attacker_tide_of_battle_icon = gui.get_node('combat_screen/a_tob_icon')

	self.defender_shield = gui.get_node('combat_screen/d_shield')
	self.defender_house_text = gui.get_node('combat_screen/d_house_name_text')
	self.defender_total_strength_text = gui.get_node('combat_screen/d_strength_shield_text')
	self.defender_tide_of_battle_text = gui.get_node('combat_screen/d_tob_strength_text')
	self.defender_tide_of_battle_icon = gui.get_node('combat_screen/d_tob_icon')
end

function _M:open(attacker, defender, tile_num)
	gui.play_flipbook(self.attacker_shield, attacker)
	gui.play_flipbook(self.defender_shield, defender)
	gui.set_text(self.attacker_house_text, "House" .. utils.HOUSE_REAL_NAMES[attacker])
	gui.set_text(self.defender_house_text, "House" .. utils.HOUSE_REAL_NAMES[defender])
	gui.set_text(self.place_text, game_data.gameRules.board[tile_num + 1].name)

	gui.set_enabled(self.panel, true)
end

function _M:set_attacker_strength(str)
	gui.set_text(self.attacker_total_strength_text, str)
end

function _M:set_defender_strength(str)
	gui.set_text(self.defender_total_strength_text, str)
end

function _M:close()
	gui.set_enabled(self.panel, false)
	self:delete_cards()
	self.confirmed = false
end

function _M:delete_cards()
	for _, card in ipairs(self.cards) do
		card:delete()
	end
	self.cards = {}
end

local card_pos_y = -100
local card_position_step = 110
---@param cards HouseCard[]
function _M:show_cards(cards)
	local card_start_pos_x = -card_position_step * ((#cards - 1) / 2)
	for _, v in ipairs(cards) do
		local c = house_card:new(v)
		c:set_parent(self.panel)
		c:set_position(vmath.vector3(
				card_start_pos_x + (card_position_step * #self.cards),
				card_pos_y,
				0
		))
		self.cards[#self.cards + 1] = c
	end
end

function _M:check_pressed(x, y)
	if gui.is_enabled(self.panel) and gui.pick_node(self.panel, x, y) then
		return true
	end
	return false
end

local last_clicked_card_idx

function _M:check_button_pressed(x, y)
	if not gui.is_enabled(self.panel) or self.confirmed then
		return false
	end
	if last_clicked_card_idx then
		if last_clicked_card_idx < #self.cards then
			gui.move_below(
					self.cards[last_clicked_card_idx].bg,
					self.cards[last_clicked_card_idx + 1].bg
			)
		end
		gui.set(self.cards[last_clicked_card_idx].bg, 'position.y', card_pos_y)
		last_clicked_card_idx = nil
		event_dispatcher.trigger('house_card_selected', false)
	end
	for i = #self.cards, 1, -1 do
		if self.cards[i]:check_press(x, y) then
			if i <= #self.cards then
				if i < #self.cards then
					gui.move_above(self.cards[i].bg, self.cards[i + 1].bg)
				end
				gui.set(self.cards[i].bg, 'position.y', card_pos_y + 50)
				last_clicked_card_idx = i
				event_dispatcher.trigger('house_card_selected', true)
			end
			return true
		end
	end
	player_panels:check_button_pressed(x, y)
	return true
end

function _M:get_selected_card()
	if last_clicked_card_idx then
		return self.cards[last_clicked_card_idx].card
	end
end

---@param card HouseCard
---@param is_attacker boolean
function _M:confirm_card(card, is_attacker)
	if self.confirmed then
		return
	end
	self.confirmed = true
	for _, c in ipairs(self.cards) do
		if c.card ~= card then
			c:delete()
		else
			if is_attacker then
				gui.set_parent(c.bg, gui.get_node('combat_screen/attacker_box'), true)
				gui.animate(c.bg, gui.PROP_POSITION, self.a_card_position, gui.EASING_LINEAR, utils.ANIMATION_TIME)
				gui.animate(c.bg, gui.PROP_EULER, self.a_card_rotation, gui.EASING_LINEAR, utils.ANIMATION_TIME)
			else
				gui.set_parent(c.bg, gui.get_node('combat_screen/defender_box'), true)
				gui.animate(c.bg, gui.PROP_POSITION, self.d_card_position, gui.EASING_LINEAR, utils.ANIMATION_TIME)
				gui.animate(c.bg, gui.PROP_EULER, self.d_card_rotation, gui.EASING_LINEAR, utils.ANIMATION_TIME)
			end
		end
	end
	self.cards = {}
end

---@param card HouseCard
---@param is_attacker boolean
function _M:set_card(card, is_attacker)
	local c = house_card:new(card)
	if is_attacker then
		gui.set_parent(c.bg, gui.get_node('combat_screen/attacker_box'))
		gui.set_euler(c.bg, self.a_card_rotation)
		gui.set_position(c.bg, self.a_card_position)
	else
		gui.set_parent(c.bg, gui.get_node('combat_screen/defender_box'))
		gui.set_euler(c.bg, self.d_card_rotation)
		gui.set_position(c.bg, self.d_card_position)
	end
end

return _M
