local game_data = require "main/ui/game_data"

local addOrder = require "main/ui/phases/planning/addOrder"
local ravenChoose = require "main/ui/phases/planning/ravenChooseChangeOrderOrLookAtWildlingCard"
local ravenChangeOrder = require "main/ui/phases/planning/ravenChangeOrder"

local resolveMarchOrder = require "main/ui/phases/action/resolveMarchOrder"

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
	resolveMarchOrder = function(reply)
		if reply.player_action.targets and next(reply.player_action.targets) then
			msg.post('/map', 'move_units', {
				from_tile = reply.player_action.sourceTileNumber,
				targets = reply.player_action.targets
			})
		end
		if reply.player_action.houseType == game_data.me then
			msg.post('/map', 'unselect_label')
			msg.post('/map', 'set_order_source_tile', {})
		end
		msg.post('/map', 'remove_order', {tile_num = reply.player_action.sourceTileNumber})
		resolveMarchOrder:clean_up()
		if reply.current_phase.subPhase == 'resolveMarchOrder' then
			resolveMarchOrder:init()
		elseif reply.current_phase.subPhase == 'resolveSupportOrder'then
			-- todo
		elseif reply.current_phase.subPhase == 'chooseHouseCard'then
			-- todo
		end
	end,
}

switch.ravenChangeOrder = function(reply)
	switch.addOrder(reply)
	ravenChangeOrder:clean_up()
	if reply.current_phase.subPhase == 'resolveRaidOrder' then
		-- todo
	elseif reply.current_phase.subPhase == 'resolveMarchOrder' then
		resolveMarchOrder:init()
	else
		-- todo
	end
end

setmetatable(switch, {
	__index = function(_, k)
		error("Unknown or unimplemented action type '" .. k .. "'")
	end
})

function _M:process(reply)
	if not reply.player_action.actionType then
		error("No action type in the reply")
	else
		game_data.subPhase = reply.current_phase
		msg.post("/map", "set_phase", {phase = reply.current_phase.subPhase})
		--if reply.current_phase.houseType then
		--	--todo move to phases?
		--	self.player_panel__set_player_turn(reply.current_phase.houseType)
		--end
		switch[reply.player_action.actionType](reply)
	end
end

return _M