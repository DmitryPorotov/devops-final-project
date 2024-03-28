---@class MilitaryUnit
---@field house "'wolf'" | "'moose'" | "'pufferfish'" | "'kraken'" | "'rose'" | "'lion'" | "'neutral'"
---@field type "'footmen'" | "'knights'" | "'ships'" | "'siegeEngines'" | "'garrison'" | "'powerToken'"
---@field isDefeated boolean
---@field defPoints number

---@class BoardTile
---@field number number
---@field tileType "'sea'" | "'land'" | "'port'"
---@field name string
---@field neighbourTiles number[]
---@field musteringPoints number
---@field supplyPoints number
---@field powerPoints number
---@field homeOf "'lion'" | "'kraken'" | "'moose'" | "'rose'" | "'pufferfish'" | "'wolf'"


---@class GameRules
---@field board BoardTile[]
---@field supplyUsage number[][]
---@field kingsCourtStars number[]
---@field maxArmies table<string, number>

---@module game_data
---@field me string
---@field armies table<string, MilitaryUnit[]>
---@field gameRules GameRules
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
