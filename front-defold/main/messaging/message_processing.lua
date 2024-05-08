local game_data = require "main/ui/game_data"
local action_proc = require "main/messaging/action_reply_processing"

local _get_game_state = require "main/ui/phases/_get_game_state"

local _M = {
	send = nil,

	list_of_saves__show_saves = nil
}

local function update_players(old, new)
	for _, v in ipairs(new) do
		old[v.house] = {id = v.id , name = v.name}
	end
end

local phase

function _M:process_message(message)
	if message.type == 'action' then
		if message.action == "game_action" then
			for _, reply in ipairs(message.reply) do
				action_proc:process(reply)
			end
		elseif message.action == "join_game" then
			update_players(game_data.players, message.gameSettings.players)
			if not game_data.i_joined then
				game_data.i_joined = true
				self.send({
					type = "action",
					action = "get_game_state",
				})
			end
		elseif message.action == "list_saves" then
			self.list_of_saves__show_saves(message.saves)
		elseif message.action == "load" then
			msg.post("/camera", "take_focus")
			self.send({
				type = "action",
				action = "get_game_state",
			})
		elseif message.action == "get_game_state" then
			print('got game state')
			_get_game_state:init(message)

			if phase then
				phase:clean_up()
			end

			if message.gameState.subPhase.subPhase == "addOrder" then
				phase = require "main/ui/phases/planning/addOrder"
				phase:init(
					message.gameState.armies,
					message.gameState.placedOrders[game_data.me] or {},
					message.gameState.subPhase
				)
			elseif message.gameState.subPhase.subPhase == "ravenChooseChangeOrderOrLookAtWildlingCard" then
				phase = require "main/ui/phases/planning/ravenChooseChangeOrderOrLookAtWildlingCard"
				phase:init()
			elseif message.gameState.subPhase.subPhase == "ravenChangeOrder" then
				phase = require "main/ui/phases/planning/ravenChangeOrder"
				phase:init()
			elseif message.gameState.subPhase.subPhase == "resolveMarchOrder" then
				phase = require "main/ui/phases/action/resolveMarchOrder"
				phase:init()
			elseif message.gameState.subPhase.subPhase == 'leavePowerTokenAtTile' then
				phase = require "main/ui/phases/action/leavePowerTokenAtTile"
				phase:init(message.gameState.subPhase.houseType, message.gameState.subPhase.tileNumber)
			elseif message.gameState.subPhase.subPhase == 'chooseHouseCard' then
				phase = require "main/ui/phases/action/chooseHouseCard"
				phase:init(
						message.gameState.subPhase.houseTypes[1],
						message.gameState.subPhase.houseTypes[2],
						message.gameState.combat.attackerTileNum,
						message.gameState.combat.defenderTileNum
				)
			else
				error("Unknown or unimplemented phase " .. message.gameState.subPhase.subPhase)
				--action_proc.player_panel__set_player_turn(message.gameState.subPhase.houseType)
			end
		elseif message.action == "create_game" and game_data.me == "kraken" then
			self.send({
				type = "action",
				action = "join_game",
				joinAs = game_data.me,
				name = game_data.user_data.name
			})
		elseif message.action == "error" then
			print(message.message)
			local s = gui.get_node("debug")
			gui.set_text(s, message.message)
		end
	elseif message.type == 'chat' then
		if message.body.type == 'create' then
			self.send({
				type = "chat",
				body = {
					type = "join"
				}
			})
		elseif message.body.type == 'join' and message.userId == game_data.user_data.id then
			if game_data.creating_new_game then
				self.send({
					type = "action",
					action = "create_game",
					isRandomHouses = false,
				})
			else
				self.send({
					type = "action",
					action = "join_game",
					joinAs = game_data.me,
					name = game_data.user_data.name
				})
			end

		end
	elseif message.type == 'system'	then
		-- todo
	else
		print(message.message)
		local s = gui.get_node("debug")
		gui.set_text(s, message.message)
	end
end

return _M
