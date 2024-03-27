local utils = require 'main/utils'

local supply_logic = {
	usage_rules = nil,
	avail_supplies = 0,
}

local function set_usage_rules(rules)
	supply_logic.usage_rules = rules
end

local function set_available_supplies(count)
	supply_logic.avail_supplies = count
end

local function get_max_armies()
	if supply_logic.avail_supplies > 6 then
		return supply_logic.usage_rules[7]
	end
	return supply_logic.usage_rules[supply_logic.avail_supplies + 1]
end

---@param army MilitaryUnit[]
---@return number
local function count_units(army)
	local cnt = 0
	for _, v in ipairs(army) do
		cnt = cnt + (utils.is_unit_commandable(v.type) and 1 or 0)
	end
	return cnt
end

local function comparator(a, b) return b.c < a.c end

---@class TileNumberAndCount
---@field c number Count
---@field tn string Tile number

---@return table<number, TileNumberAndCount> | TileNumberAndCount[]
---@return table<string, TileNumberAndCount>
local function order_by_units_per_tile(army)
	local r1 = {}
	for k, v in pairs(army) do
		local tuple = {
			tn = k,
			c = count_units(v)
		}
		r1[#r1 + 1] = tuple
	end
	table.sort(r1, comparator)
	return r1
end

---@param from_tile string
---@param to_tile string
---@param sorted_armies TileNumberAndCount[]
local function simulate_move(from_tile, to_tile, how_many ,sorted_armies)
	for _, v in ipairs(sorted_armies) do
		if v.tn == from_tile then
			v.c = v.c - how_many
		elseif v.tn == to_tile then
			v.c = v.c + how_many
		end
	end
	table.sort(sorted_armies, comparator)
	return sorted_armies
end

---@param sorted_armies TileNumberAndCount[]
local function check_supply_is_ok(sorted_armies)
	local limits = get_max_armies()
	for i, v in ipairs(sorted_armies) do
		if (not limits[i] and v.c > 1) or v.c > (limits[i] or 0) then
			return false
		elseif v.c == 1 then
			return true
		end
	end
	return true
end

---@param from_tile string Source tile number
---@param how_many number How many units to move
---@param targets number[] Array of reachable tiles
---@param my_armies table<string, MilitaryUnit[]> A map of tile numbers to armies
local function filter_target_candidates(from_tile, how_many , targets, my_armies)
	local sorted_units_per_tile = order_by_units_per_tile(my_armies)
	if sorted_units_per_tile[1].c + how_many <= get_max_armies()[1] then
		return targets
	end
	local result = {}
	local idx = 1
	repeat
		local resorted = simulate_move(from_tile, tostring(targets[idx]), sorted_units_per_tile)
		if check_supply_is_ok(resorted) then
			result[#result + 1] = targets[idx]
		end
		idx = idx + 1
		sorted_units_per_tile = order_by_units_per_tile(my_armies)
	until idx <= #targets

	return result
end

return {
	set_usage_rules = set_usage_rules,
	set_available_supplies = set_available_supplies,
	get_max_armies = get_max_armies,
	order_by_units_per_tile = order_by_units_per_tile,
}
