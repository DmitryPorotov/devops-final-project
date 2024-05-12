---@type table<string, HouseCard[]>
local cards_by_house = {}
---@type table<string, number[]>
local discarded_cards = {}
local house_cards_is_init = false
local utils = require "main/utils"

---@param house_cards HouseCard[]
---@param disc_cards table<string, table<number>>
local function init(house_cards, disc_cards)
	discarded_cards = disc_cards
	if house_cards_is_init then
		return
	end
	for _, card in ipairs(house_cards) do
		if not cards_by_house[card.house] then
			cards_by_house[card.house] = {}
		end
		cards_by_house[card.house][#cards_by_house[card.house] + 1] = card
	end
	for _, v in pairs(cards_by_house) do
		table.sort(v, function(a, b)
			return a.strength < b.strength
		end)
	end
	house_cards_is_init = true
end

---@param house string
---@return HouseCard[]
local function get_house_cards(house)
	return cards_by_house[house]
end

---@param house string
---@param card HouseCard
local function discard_card(house, card)
	if not discarded_cards[house] then
		discarded_cards[house] = {}
	end
	discarded_cards[house][#discarded_cards[house] + 1] = card.code
end

---@param house string
---@param code number
---@return HouseCard
local function get_house_card(house, code)
	local idx = utils.index_of(cards_by_house[house], function(c)
		return c.code == code
	end)
	return cards_by_house[house][idx]
end

---@param house string
---@return number[] Codes of discarded cards
local function get_discarded_cards(house)
	return discarded_cards[house] or {}
end

return {
	init = init,
	get_house_cards = get_house_cards,
	discard_card = discard_card,
	get_discarded_cards = get_discarded_cards,
	get_house_card = get_house_card,
}
