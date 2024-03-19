local game_data = require "main/ui/game_data"
local hints = require "main/ui/hints"
local raven_card_or_order = require "main/ui/dialogs/raven_choose_card_or_order"
local player_panels = require "main/ui/player_panel"

local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {
	raven_choice_prefix_text = 'The owner of the Messenger Raven chose to\n',
	raven_choice_change_order_text = 'change an order for one of the territories.',
	raven_choice_look_at_card_text = 'look at the top card of the Wildlings deck.',
}

local function on_ws_raven_card_or_order(self, reply)
	local text = self.raven_choice_prefix_text
			.. (reply.current_phase.subPhase == 'ravenChangeOrder' and self.raven_choice_change_order_text or self.raven_choice_look_at_card_text)
	hints:none_actionable_hint(text)
end

local function on_raven_card_or_order_click()
	event_dispatcher.trigger('ws_send', raven_card_or_order:build_message())
	raven_card_or_order:close()
end

function _M:init()
	player_panels:set_player_turn(game_data.subPhase.houseType)
	if game_data.me == game_data.tracks["court"][1] then
		raven_card_or_order:open()
		event_dispatcher.on('raven_card_or_order_click', on_raven_card_or_order_click)
	end
	event_dispatcher.on('ws_raven_card_or_order', on_ws_raven_card_or_order, self)
end

function _M:clean_up()
	event_dispatcher.off('ws_raven_card_or_order', on_ws_raven_card_or_order)
	event_dispatcher.off('raven_card_or_order_click', on_raven_card_or_order_click)
	raven_card_or_order:close()
	hints:clean_up()
end

return _M
