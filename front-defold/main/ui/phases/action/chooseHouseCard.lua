local combat_screen = require "main/ui/combat_screen"
local army_logic = require "main/ui/army_logic"
local game_data = require "main/ui/game_data"
local utils = require "main/utils"
local house_cards_logic = require "main/ui/house_cards_logic"
local hints = require "main/ui/hints"
local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {}

local function on_card_selection_confirmed()
	pprint(combat_screen:get_selected_card())
end

local function on_house_card_selected(flag)
	hints:set_next_button_enabled(flag)
end

function _M:init(attacker, defender, a_tile_num, d_tile_num)
	msg.post('/map', 'move_camera_to_label', {tile_num = d_tile_num})
	combat_screen:open(attacker, defender, d_tile_num)
	local def_str = army_logic:calc_army_strength(army_logic.combat.defenderArmy)
	local add_siege_engines = game_data.gameRules.board[d_tile_num + 1].musteringPoints > 0
	local att_str = army_logic:calc_army_strength(army_logic.combat.defenderArmy, add_siege_engines)
	combat_screen:set_defender_strength(def_str)
	combat_screen:set_attacker_strength(att_str)
	local function get_active_cards(house)
		local cards = {}
		local discarded_cards = house_cards_logic.get_discarded_cards(house)
		for _, v in ipairs(house_cards_logic.get_house_cards(house)) do
			if not utils.index_of(discarded_cards, v.code) then
				cards[#cards+1] = v
			end
		end
		return cards
	end
	if attacker == game_data.me then
		combat_screen:show_cards(get_active_cards(attacker))
	elseif defender == game_data.me then
		combat_screen:show_cards(get_active_cards(defender))
	end
	if attacker == game_data.me or defender == game_data.me then
		hints:set_enabled(true)
		hints:set_goto_button_enabled(false)
		hints:set_hint_text('Select a card') -- todo : check the actual text
		event_dispatcher.on('hints_next_button_click', on_card_selection_confirmed)
		event_dispatcher.on('house_card_selected', on_house_card_selected)
	end
end


return _M
