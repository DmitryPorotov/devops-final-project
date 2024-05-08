local game_data = require "main/ui/game_data"
local utils = require "main/utils"
local army_logic = require "main/ui/army_logic"

local _M = {}

---A recursive function to find all tiles reachable by ship
---@param from_tile number
---@param candidates table
---@param visited_seas table
local function traveling_on_land_and_by_ship(from_tile, candidates, visited_seas)
	local f_t = game_data.gameRules.board[from_tile + 1]
	for _, v in ipairs(f_t.neighbourTiles) do
		local c = game_data.gameRules.board[v + 1]
		if c.tileType == 'land' then
			if not utils.index_of(candidates, c.number) then
				candidates[#candidates + 1] = c.number
			end
		elseif c.tileType == 'sea'
			and army_logic.armies[tostring(c.number)]
			and army_logic.armies[tostring(c.number)][1]
			and army_logic.armies[tostring(c.number)][1].house == game_data.me
			and not utils.index_of(visited_seas, c.number)
		then
			visited_seas[#visited_seas + 1] = c.number
			traveling_on_land_and_by_ship(c.number, candidates, visited_seas)
		end
	end
	return candidates
end

function _M:is_my_port(port_num)
	local army_on_land = army_logic.armies[tostring(port_num - 1)]
	if army_on_land and army_on_land[1] and army_on_land[1].house == game_data.me then
		return true
	end
	return false
end

function _M:calculate_possible_destinations(from_tile)
	local f_t = game_data.gameRules.board[from_tile + 1]
	if f_t.number ~= from_tile then
		error('Tile number ' .. from_tile .. ' does not correspond to ' .. f_t.number)
	end
	local candidates = {}
	if f_t.tileType == 'sea' then
		for _, v in ipairs(f_t.neighbourTiles) do
			local c = game_data.gameRules.board[v + 1]
			if c.tileType == 'sea' or (c.tileType == 'port' and self:is_my_port(c.number)) then
				candidates[#candidates + 1] = c.number
			end
		end
	elseif f_t.tileType == 'port' then
		for _, v in ipairs(f_t.neighbourTiles) do
			local c = game_data.gameRules.board[v + 1]
			if c.tileType == 'sea' then
				candidates[#candidates + 1] = c.number
			end
		end
	else
		--note: adding starting tile to the 1st place to make it easy to delete it if
		--it's added by recursion
		candidates[#candidates + 1] = from_tile
		traveling_on_land_and_by_ship(from_tile, candidates, {})
		table.remove(candidates, 1)
	end
	return candidates
end

return _M