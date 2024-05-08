local game_data = require "main/ui/game_data"

local addOrder = require "main/ui/phases/planning/addOrder"
local ravenChoose = require "main/ui/phases/planning/ravenChooseChangeOrderOrLookAtWildlingCard"
local ravenChangeOrder = require "main/ui/phases/planning/ravenChangeOrder"

local resolveMarchOrder = require "main/ui/phases/action/resolveMarchOrder"
local leavePowerTokenAtTile = require "main/ui/phases/action/leavePowerTokenAtTile"
local chooseHouseCard = require "main/ui/phases/action/chooseHouseCard"

local event_dispatcher = require "main/ui/event_dispatcher"
local army_logic = require "main/ui/army_logic"

local power_tokens_logic = require "main/ui/power_tokens_logic"

local _M = {
	player_panel__set_player_turn = nil,
}

local current_phase_switch, action_type_switch, do_current_phase_switching

action_type_switch = {
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
			do_current_phase_switching(reply)
		end
	end,
	ravenChooseChangeOrderOrLookAtWildlingCard = function(reply)
		event_dispatcher.trigger('ws_raven_card_or_order', reply)
		ravenChoose:clean_up()
		do_current_phase_switching(reply)
	end,
	resolveMarchOrder = function(reply)
		if reply.player_action.targets and next(reply.player_action.targets) then
			local no_en, enemy =
				army_logic:separate_targets_with_no_enemies(reply.player_action.targets, reply.player_action.houseType)
			if next(no_en) then
				msg.post('/map', 'move_units', {
					from_tile = reply.player_action.sourceTileNumber,
					targets = no_en
				})
				army_logic:move_units_no_conflicts(
						reply.player_action.sourceTileNumber,
						no_en
				)
			end
			if next(enemy) then
				msg.post('/map', 'move_units_for_attack', {
					from_tile = reply.player_action.sourceTileNumber,
					targets = enemy
				})
			end
		end
		if reply.player_action.houseType == game_data.me then
			msg.post('/map', 'unselect_label')
			msg.post('/map', 'set_order_source_tile', {})
		end
		msg.post('/map', 'remove_order', {tile_num = reply.player_action.sourceTileNumber})
		resolveMarchOrder:clean_up()
		do_current_phase_switching(reply)
	end,
	leavePowerTokenAtTile = function(reply)
		if reply.player_action.doLeave then
			msg.post('/map', 'set_units', {
				tile_num = tostring(reply.player_action.tileNumber),
				units = {
					{
						house = reply.player_action.houseType,
						type = 'powerToken'
					}
				}
			})
			msg.post('/map', 'reassign_labels')
			power_tokens_logic.leave_power_token_on_map(reply.player_action.houseType)
		end
		leavePowerTokenAtTile:clean_up()
		do_current_phase_switching(reply)
	end,
	chooseHouseCard = function(reply)
		local a =0
	end
}

action_type_switch.ravenChangeOrder = function(reply)
	action_type_switch.addOrder(reply)
	ravenChangeOrder:clean_up()
	do_current_phase_switching(reply)
end

current_phase_switch = {
	resolveMarchOrder = function()
		resolveMarchOrder:init()
	end,
	resolveRaidOrder = function(reply)

	end,
	leavePowerTokenAtTile = function(reply)
		leavePowerTokenAtTile:init(reply.current_phase.houseType, reply.current_phase.tileNumber)
	end,
	resolveSupportOrder = function(reply)

	end,
	chooseHouseCard = function(reply)
		local _, enemy =
		army_logic:separate_targets_with_no_enemies(
				reply.player_action.targets,
				reply.player_action.houseType
		)
		local key = pairs(enemy)(enemy)
		chooseHouseCard:init(
				reply.current_phase.houseTypes[1],
				reply.current_phase.houseTypes[2],
				reply.player_action.sourceTileNumber,
				key
		)
	end,
	ravenChooseChangeOrderOrLookAtWildlingCard = function()
		ravenChoose:init()
	end,
	ravenChangeOrder = function()
		ravenChangeOrder:init()
	end,
	ravenGetWildlingsCard = function(reply)

	end,
}

function do_current_phase_switching(reply)
	current_phase_switch[reply.current_phase.subPhase](reply)
end

setmetatable(action_type_switch, {
	__index = function(_, k)
		error("Unknown or unimplemented action type '" .. k .. "'")
	end
})

setmetatable(current_phase_switch, {
	__index = function(_, k)
		error("Unknown or unimplemented current sub-phase '" .. k .. "'")
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
		action_type_switch[reply.player_action.actionType](reply)
	end
end

return _M