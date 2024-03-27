---@class MilitaryUnit
---@field house "'wolf'" | "'moose'" | "'pufferfish'" | "'kraken'" | "'rose'" | "'lion'" | "'neutral'"
---@field type "'footmen'" | "'knights'" | "'ships'" | "'siegeEngines'" | "'garrison'" | "'powerToken'"
---@field isDefeated boolean
---@field defPoints number


---@module game_data
---@field me string
---@field armies table<string, MilitaryUnit[]>
local _M = {
	players = {
		moose = { id = -1, name = "Waiting for player..." },
		lion = { id = -1, name = "Waiting for player..." },
		wolf = { id = -1, name = "Waiting for player..." },
		pufferfish = { id = -1, name = "Waiting for player..." },
		kraken = { id = -1, name = "Waiting for player..." },
		rose = { id = -1, name = "Waiting for player..." }
	},
	i_joined = false,
	me = nil,
	user_data = nil,

	gameRules = nil,

	tracks = nil,
	---@type table<string, MilitaryUnit[]>
	armies = {},
	subPhase = nil,
	supplies = nil,
	wildlingCounter = 0,
	discardedHouseCards = nil,
	powerTokens = nil,
	roundCounter = 1,

	game_id = 2,
	creating_new_game = false
}

return _M
