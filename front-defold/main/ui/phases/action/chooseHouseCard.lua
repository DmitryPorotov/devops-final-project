local combat_screen = require "main/ui/combat_screen"
local army_logic = require "main/ui/army_logic"
local game_data = require "main/ui/game_data"
local utils = require "main/utils"
local house_cards_logic = require "main/ui/house_cards_logic"
local hints = require "main/ui/hints"
local event_dispatcher = require "main/ui/event_dispatcher"
local events = require "main/ui/events"

local _M = {
	att_str = 0,
	def_str = 0,
}

local function on_card_selection_confirmed()
	local card = combat_screen:get_selected_card()
	combat_screen:confirm_card(card, army_logic.combat.attackerHouse == game_data.me)
	event_dispatcher.trigger(events.ws_send, {
		player_action = {
			actionType = "chooseHouseCard",
			cardCode = card.code,
		}
	})
	hints:set_enabled(false)
end

local function on_house_card_selected(flag)
	hints:set_next_button_enabled(flag)
end

function _M:init(attacker, defender, a_tile_num, d_tile_num)
	self.att_str = 0
	self.def_str = 0
	msg.post('/map', 'move_camera_to_label', {tile_num = d_tile_num})
	combat_screen:open(attacker, defender, d_tile_num)
	self.def_str = army_logic:calc_army_strength(army_logic.combat.defenderArmy)
	local add_siege_engines = game_data.gameRules.board[d_tile_num + 1].musteringPoints > 0
	self.att_str = army_logic:calc_army_strength(army_logic.combat.attackerArmy, add_siege_engines)
	combat_screen:set_defender_strength(self.def_str)
	combat_screen:set_attacker_strength(self.att_str)
	local function get_active_cards(house)
		local cards = {}
		local house_cards = house_cards_logic.get_house_cards(house)
		local discarded_cards = house_cards_logic.get_discarded_cards(house)
		for _, v in ipairs(house_cards) do
			if not utils.index_of(discarded_cards, v.code) then
				cards[#cards+1] = v
			end
		end
		return cards
	end
	local active_cards
	if attacker == game_data.me and not army_logic.combat.attackerCard then
		active_cards = get_active_cards(attacker)
	elseif defender == game_data.me and not army_logic.combat.defenderCard then
		active_cards = get_active_cards(defender)
	end
	self:update_house_cards()
	self:update_TOB_cards()
	if active_cards then
		combat_screen:show_cards(active_cards)
	end
	if attacker == game_data.me or defender == game_data.me then
		hints:set_enabled(true)
		hints:set_goto_button_enabled(false)
		hints:set_hint_text('Select a card') -- todo : check the actual text
		event_dispatcher.on(events.hints_next_button_click, on_card_selection_confirmed)
		event_dispatcher.on(events.house_card_selected, on_house_card_selected)
	end
end

function _M:update_house_cards()
	if army_logic.combat.defenderCard then
		local card = house_cards_logic.get_house_card(
				army_logic.combat.defenderHouse,
				army_logic.combat.defenderCard
		)
		self.def_str = self.def_str + card.strength
		combat_screen:set_card(card,false)
		combat_screen:set_defender_strength(self.def_str)
	end
	if army_logic.combat.attackerCard then
		local card = house_cards_logic.get_house_card(
				army_logic.combat.attackerHouse,
				army_logic.combat.attackerCard
		)
		self.att_str = self.def_str + card.strength
		combat_screen:set_card(card, true)
		combat_screen:set_attacker_strength(self.att_str)
	end
end

function _M:update_TOB_cards()
	timer.delay(.2, false, function()
		local tob_card = army_logic:get_attacker_tob_card()
		if tob_card then
			combat_screen:set_TOB_card(tob_card, true)
		end
		tob_card = army_logic:get_defender_tob_card()
		if tob_card then
			combat_screen:set_TOB_card(tob_card, false)
		end
	end)
end

function _M:update_outcome()
	local oc = army_logic.combat.combatOutcome
	combat_screen:set_attacker_strength(oc.attackerStrength)
	combat_screen:set_defender_strength(oc.defenderStrength)
	combat_screen:set_winner(army_logic.combat.attackerHouse == oc.winner)
end

function _M:clean_up()
	event_dispatcher.off(events.hints_next_button_click, on_card_selection_confirmed)
	event_dispatcher.off(events.house_card_selected, on_house_card_selected)
	hints:set_enabled(false)
	combat_screen:close()
	self.att_str = 0
	self.def_str = 0
end


return _M
