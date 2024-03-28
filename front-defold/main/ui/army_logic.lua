local utils = require 'main/utils'
local game_data = require 'main/ui/game_data'

local _M = {
	unit_names = {
		'footmen',
		'knights',
		'siegeEngines',
		'ships'
	},
}

---@param tile_num string
function _M.has_tile_enemy_army(tile_num)
	local army = game_data.armies[tile_num]
	if army and army[1] and army[1].house ~= game_data.me
		and not (army[1].type == 'powerToken' and #army == 1)
	then
		return true
	end
	return false
end

function _M.build_unit_and_count_phrase(type, count)
	if type == _M.unit_names[1] then
		if count == 1 then
			return '1 footman'
		else
			return count .. ' footmen'
		end
	elseif type == _M.unit_names[2] then
		if count == 1 then
			return '1 knight'
		else
			return count .. ' knights'
		end
	elseif type == _M.unit_names[3] then
		if count == 1 then
			return '1 siege engine'
		else
			return count .. ' siege engines'
		end
	elseif type == _M.unit_names[4] then
		if count == 1 then
			return '1 ship'
		else
			return count .. ' ships'
		end
	end
end

---@param army table Server format army
---@return GUIArmyToSend GUI format army
function _M.to_gui_format(army)
	local avail_counts = {}
	for _, v in ipairs(army) do
		if utils.index_of(_M.unit_names, v.type) and not v.isDefeated then
			avail_counts[v.type] = (avail_counts[v.type] or 0) + 1
		end
	end
	return avail_counts
end

function _M.house_armies_to_gui_format(house, armies)
	local result = {}
	for tile_num, army in pairs(armies) do
		if army and army[1].house == house then
			result[tile_num] = _M.to_gui_format(army)
		end
	end
	return result
end

function _M.to_server_format(counts)
	local army = {}
	for k, v in pairs(counts) do
		if not utils.index_of(_M.unit_names, k) then
			error("Unknown military unit type" .. k)
		end
		for _ = 1, v do
			army[#army + 1] = {
				house = game_data.me,
				type = k
			}
		end
	end
	return army
end

return _M
