local utils = require 'main/utils'
local game_data = require 'main/ui/game_data'

local _M = {
	unit_names = {
		'footmen',
		'knights',
		'siegeEngines',
		'ships'
	},
	---@type table<string, MilitaryUnit[]>
	armies = {}
}

---@param tile_num string
---@param me string HouseType
---@return boolean
function _M:has_tile_enemy_army(tile_num, me)
	local army = self.armies[tile_num]
	if army and army[1] and army[1].house ~= me
		and not (army[1].type == 'powerToken' and #army == 1)
	then
		return true
	end
	return false
end

---@return table<number, MilitaryUnit[]>, table<number, MilitaryUnit>
function _M:separate_targets_with_no_enemies(targets, attacker)
	local enemy = {}
	local no_enemies = {}
	for tile_num, army in pairs(targets) do
		if self:has_tile_enemy_army(tile_num, attacker) then
			enemy[tile_num] = army
		else
			no_enemies[tile_num] = army
		end
	end
	return no_enemies, enemy
end

function _M:build_unit_and_count_phrase(type, count)
	if type == self.unit_names[1] then
		if count == 1 then
			return '1 footman'
		else
			return count .. ' footmen'
		end
	elseif type == self.unit_names[2] then
		if count == 1 then
			return '1 knight'
		else
			return count .. ' knights'
		end
	elseif type == self.unit_names[3] then
		if count == 1 then
			return '1 siege engine'
		else
			return count .. ' siege engines'
		end
	elseif type == self.unit_names[4] then
		if count == 1 then
			return '1 ship'
		else
			return count .. ' ships'
		end
	end
end

---@param army table Server format army
---@return GUIArmyToSend GUI format army
function _M:to_gui_format(army)
	local avail_counts = {}
	for _, v in ipairs(army) do
		if utils.index_of(self.unit_names, v.type) and not v.isDefeated then
			avail_counts[v.type] = (avail_counts[v.type] or 0) + 1
		end
	end
	return avail_counts
end

function _M:house_armies_to_gui_format(house, armies)
	local result = {}
	for tile_num, army in pairs(armies) do
		if army and army[1].house == house then
			result[tile_num] = self:to_gui_format(army)
		end
	end
	return result
end

function _M:to_server_format(counts)
	local army = {}
	for k, v in pairs(counts) do
		if not utils.index_of(self.unit_names, k) then
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

---@class Combat
---@field attackerTileNum number
---@field attackerHouse string
---@field attackerArmy MilitaryUnit[]
---@field attackerOrder userdata
---@field attackerCard HouseCard | nil
---@field attackerCardResolved boolean
---@field attackerTidesOfBattle userdata | nil
---@field attackerSupport number[]
---@field defenderTileNum number
---@field defenderHouse string
---@field defenderArmy MilitaryUnit[]
---@field defenderOrder userdata | nil
---@field defenderCard HouseCard | nil
---@field defenderCardResolved boolean
---@field defenderTidesOfBattle userdata | nil
---@field defenderSupport number[]

---@param armies table<string, MilitaryUnit[]>
---@param combat Combat
function _M:init(armies, combat)
	self.combat = combat
	self.armies = armies
end

---@param from_tile number
---@param targets table<number, MilitaryUnit[]>
function _M:move_units_no_conflicts(from_tile, targets)
	for target_tile_num, armies in pairs(targets) do
		local target_tile_num_str = tostring(target_tile_num)
		if not self.armies[target_tile_num_str] then
			self.armies[target_tile_num_str] = armies
		elseif self.armies[target_tile_num_str]
				and self.armies[target_tile_num_str][1]
				and self.armies[target_tile_num_str][1].type == 'powerToken'
				and self.armies[target_tile_num_str][1].house ~= armies[1].house
				and #self.armies[target_tile_num_str] == 1 then
			self.armies[target_tile_num_str] = armies
		elseif self.armies[target_tile_num_str]
				and self.armies[target_tile_num_str][1]
				and self.armies[target_tile_num_str][1].house == armies[1].house then
			for _, v in ipairs(armies) do
				self.armies[target_tile_num_str][#self.armies[target_tile_num_str]+1] = v
			end
		else
			error('There are enemy armies at the tile ' .. target_tile_num_str)
		end
		self:remove_armies_from_tile(tostring(from_tile), armies)
	end
end

---@param tile_num string
---@param armies MilitaryUnit[]
function _M:remove_armies_from_tile(tile_num, armies)
	for _, unit in ipairs(armies) do
		for i, s_unit in ipairs(self.armies[tile_num]) do
			if unit.type == s_unit.type
					and unit.house == s_unit.house
					and unit.defPoints == s_unit.defPoints
					and unit.isDefeated == s_unit.isDefeated
			then
				table.remove(self.armies[tile_num], i)
				break
			end
		end
	end
end

---@param army MilitaryUnit[]
---@param add_siege_engines boolean
---@return number
function _M:calc_army_strength(army, add_siege_engines)
	local strength = 0
	for _, v in ipairs(army) do
		if not v.isDefeated then
			if v.type == 'footmen' then
				strength = strength + 1
			elseif v.type == 'knights' then
				strength = strength + 2
			elseif v.type == 'ships' then
				strength = strength + 1
			elseif v.type == 'garrison' then
				strength = strength + v.defPoints
			elseif v.type == 'siegeEngines' and add_siege_engines then
				strength = strength + 4
			end
		end
	end
	return strength
end

return _M
