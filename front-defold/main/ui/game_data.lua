local _M = {
	players = {
		moose = { id = -1, name = "Waiting for player..." },
		lion = { id = -1, name = "Waiting for player..." },
		wolf = { id = -1, name = "Waiting for player..." },
		pufferfish = { id = -1, name = "Waiting for player..." },
		kraken = { id = -1, name = "Waiting for player..." },
		rose = { id = -1, name = "Waiting for player..." }
	},
	me = nil,
	user_data = nil,
	gameRules = nil,
	game_id = 2,
	creating_new_game = false
}

return _M