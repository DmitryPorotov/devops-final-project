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

---@class HouseCard : userdata
---@field house "'lion'" | "'kraken'" | "'moose'" | "'rose'" | "'pufferfish'" | "'wolf'"
---@field code number
---@field name string
---@field strength number
---@field text string
---@field attack number
---@field defense number

---@class TidesOfBattleCard: userdata
---@field code number
---@field power number
---@field death boolean
---@field attack boolean
---@field defense boolean

---@class RoundEventCard: userdata
---@field code number
---@field title string
---@field text string
---@field wildlings number

---@class WildlingCard: userdata
---@field code number
---@field title string
---@field wildlingVictoryLowestBidderText string
---@field wildlingVictoryEveryoneElseText string
---@field playerVictoryText string

---@class BoardCards: userdata
---@field tidesOfBattle TidesOfBattleCard[]
---@field deck1 RoundEventCard[]
---@field deck2 RoundEventCard[]
---@field deck3 RoundEventCard[]
---@field wildlings WildlingCard[]

---@class GameRules
---@field board BoardTile[]
---@field supplyUsage number[][]
---@field kingsCourtStars number[]
---@field maxArmies table<string, number>
---@field houseCards HouseCard[]
---@field boardCards BoardCards

---@module game_data
---@field me string
---@field is_html5 boolean
---@field armies table<string, MilitaryUnit[]>
---@field gameRules GameRules
---@field discardedHouseCards table<string, table<number>>
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
	is_html5 = false,

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
