local game_data = require "main/ui/game_data"

local _M = {
	send = nil,
}

local function update_players(old, new)
	for _, v in ipairs(new) do
		old[v.house] = {id =v.id , name = v.name}
	end
end

function _M.process_message(self, message)
	if message.type == 'action' then
		if message.action == "game_action" then

		elseif message.action == "join_game" then
			update_players(game_data.players, message.gameSettings.players)
			game_data.gameRules = message.gameRules
			self.tracks_gui:set_players(game_data.players)
			self.set_tracks(self.tracks_gui, message.gameState.tracks)
			self.set_game_state(self.top_panel, message.gameState)
			for i, v in pairs(message.gameState.armies) do
				self.set_tile_units(i, v)
			end
		elseif message.action == "create_game" and game_data.me == "kraken" then
			self.send({
				type = "action",
				userId = game_data.user_data.id,
				lobbyId = game_data.game_id,
				action = "join_game",
				joinAs = game_data.me,
				name = game_data.user_data.name
			})
		end
	elseif message.type == 'chat' then
		if message.body.type == 'create' then
			self.send({
				type = "chat",
				userId = game_data.user_data.id,
				lobbyId = game_data.game_id,
				body = {
					type = "join"
				}
			})
		elseif message.body.type == 'join' then
			if game_data.me == "kraken" then
				self.send({
					type = "action",
					userId = game_data.user_data.id,
					lobbyId = game_data.game_id,
					action = "create_game",
					isRandomHouses = false,
				})
			else
				self.send({
					type = "action",
					userId = game_data.user_data.id,
					lobbyId = game_data.game_id,
					action = "join_game",
					joinAs = game_data.me,
					name = game_data.user_data.name
				})
			end

		end
	else
		print(message.message)
		local s = gui.get_node("debug")
		gui.set_text(s, message.message)
	end
end

function _M.set_tile_units(tile_num, units)
	msg.post("/map", "set_units", {
		tile_num = tile_num,
		units = units
	})
end

function _M.set_set_tracks_cb(ctx, callback)
	_M.tracks_gui = ctx
	_M.set_tracks = callback
end

function _M.set_set_game_state_cb(ctx, callback)
	_M.top_panel = ctx
	_M.set_game_state = callback
end


return _M
