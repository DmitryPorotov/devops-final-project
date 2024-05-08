local event_dispatcher = require "main/ui/event_dispatcher"
local utils = require "main/utils"
local power_tokens_logic = require "main/ui/power_tokens_logic"
local house_card = require "main/ui/house_card"
local house_cards_logic = require "main/ui/house_cards_logic"

local card_pos_y = -90
local card_position_step = 140
local card_start_pos_x = -(200 - card_position_step) * 7

local _M = {
	---@type HouseCardWrapper[]
	cards = {}
}

function _M:make_cards(house)
	---@type HouseCard[]
	local my_cards = house_cards_logic.get_house_cards(house)
	local discarded_cards = house_cards_logic.get_discarded_cards(house)
	for _, v in ipairs(my_cards) do
		local c = house_card:new(v, utils.index_of(discarded_cards, v.code))
		c:set_parent(self.panel)
		c:set_position(vmath.vector3(
				card_start_pos_x + (card_position_step * #self.cards),
				card_pos_y,
				0
		))
		self.cards[#self.cards + 1] = c
	end
end

function _M:delete_cards()
	for _, card in ipairs(self.cards) do
		card:delete()
	end
	self.cards = {}
end

function _M:on_player_panel_click(house)
	gui.set_enabled(self.panel, true)
	gui.play_flipbook(self.shield, hash(house))
	gui.set_text(self.house_name, "House " .. utils.HOUSE_REAL_NAMES[house])
	gui.set_text(self.power_token_count, power_tokens_logic.get(house))
	self:make_cards(house)
end

function _M:init()
	self.panel = gui.get_node("player_screen/backdrop")
	self.shield = gui.get_node("player_screen/shield")
	self.castles_count = gui.get_node("player_screen/castle_count")
	self.power_token_count = gui.get_node("player_screen/token_count")
	self.house_name = gui.get_node('player_screen/house_name')
	self.close_button = gui.get_node('player_screen/close_button')
	event_dispatcher.on('player_panel_click', self.on_player_panel_click, self)
end

function _M:check_pressed(x, y)
	if gui.is_enabled(self.panel) and gui.pick_node(self.panel, x, y) then
 		return true
	end
	return false
end

local last_clicked_card_idx

function _M:check_button_pressed(x, y)
	if not gui.is_enabled(self.panel) then
		return false
	end
	if gui.pick_node(self.close_button, x, y) then
		gui.set_enabled(self.panel, false)
		self:delete_cards()
		return true
	end
	if last_clicked_card_idx then
		gui.move_below(
				self.cards[last_clicked_card_idx].bg,
				self.cards[last_clicked_card_idx + 1].bg
		)
		last_clicked_card_idx = nil
	end
	for i = #self.cards, 1, -1 do
		if self.cards[i]:check_press(x, y) then
			if i < 7 then
				gui.move_above(self.cards[i].bg, self.cards[i + 1].bg)
				last_clicked_card_idx = i
			end
			return true
		end
	end
	return true
end

return _M
