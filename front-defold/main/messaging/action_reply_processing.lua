local game_data = require "main/ui/game_data"

local addOrder = require "main/ui/phases/planning/addOrder"
local ravenChoose = require "main/ui/phases/planning/ravenChooseChangeOrderOrLookAtWildlingCard"
local ravenChangeOrder = require "main/ui/phases/planning/ravenChangeOrder"

local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {
	player_panel__set_player_turn = nil,
}

local switch = {
	addOrder = function(reply)
		if reply.player_action.houseType == game_data.me then
			return
		end
		event_dispatcher.trigger('ws_add_order', reply)
	end,
	removeOrder = function(reply)
		if reply.player_action.houseType == game_data.me then
			return
		end
		msg.post("/map", "remove_order", {tile_num = reply.player_action.tileNumber})
	end,
	openOrders = function(reply)
		event_dispatcher.trigger('ws_open_orders', reply)
		if reply.player_action.orders then
			addOrder:clean_up()
			ravenChoose:init()
		end
	end,
	ravenChooseChangeOrderOrLookAtWildlingCard = function(reply)
		event_dispatcher.trigger('ws_raven_card_or_order', reply)
		ravenChoose:clean_up()
		if reply.current_phase.subPhase == 'ravenChangeOrder' then
			ravenChangeOrder:init()
		else
			-- todo
		end
	end,
}

switch.ravenChangeOrder = function(reply)
	switch.addOrder(reply)
	ravenChangeOrder:clean_up()
end

function _M:process(reply)
	if not reply.player_action.actionType then
		print("no action type in reply")
	elseif not switch[reply.player_action.actionType] then
		print("unknown action type " .. reply.player_action.actionType)
	else
		switch[reply.player_action.actionType](reply)
		-- note: add is_me field to know if any action is to be done?
		msg.post("/map", "set_phase", {phase = reply.current_phase.subPhase})
		if reply.current_phase.houseType then
			self.player_panel__set_player_turn(reply.current_phase.houseType)
		end
	end
end

return _M