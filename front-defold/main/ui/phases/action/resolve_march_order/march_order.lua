local army_logic = require "main/ui/army_logic"
local travel_logic = require "main/ui/travel_logic"
local supply_logic = require "main/ui/supply_logic"
local game_data = require "main/ui/game_data"
local utils = require "main/utils"


---@param source_tile_num string Source tile number
---@param my_armies table<string, GUIArmyToSend> Army in GUI format
local function new_march_order(source_tile_num, my_armies)
	local o = {
		init_army = my_armies[source_tile_num],
		my_armies = my_armies,
		to_send = {
			actionType = 'resolveMarchOrder',
			sourceTileNumber = tonumber(source_tile_num),
			targets = {}
		},
		sent = {},
		possible_targets = nil,
		to_send_targets_count_changed = false,
		enemy_at_tile_num = false,
	}

	local function get_message_to_server()
		return {
			player_action = o.to_send
		}
	end

	---@param to_tile_num string
	---@param to_send GUIArmyToSend
	---@param do_add boolean true to add, false to delete
	local function update_my_armies(to_tile_num, to_send, do_add)
		do_add = do_add and 1 or -1
		if not o.my_armies[to_tile_num] then
			o.my_armies[to_tile_num] = {}
		end
		for unit, count in pairs(to_send) do
			o.my_armies[to_tile_num][unit] = (o.my_armies[to_tile_num][unit] or 0) + (do_add * count)
			o.my_armies[tostring(o.to_send.sourceTileNumber)][unit] = (o.my_armies[tostring(o.to_send.sourceTileNumber)][unit] or 0) - (do_add * count)
		end
	end

	---@param to_send GUIArmyToSend
	local function get_possible_targets(to_send)
		if not o.possible_targets or o.to_send_targets_count_changed then
			o.to_send_targets_count_changed = false
			o.possible_targets = travel_logic:calculate_possible_destinations(o.to_send.sourceTileNumber)
			local how_many = 0
			for _, v in pairs(to_send) do
				how_many = how_many + v
			end
			if o.enemy_at_tile_num then
				local updated_possible_targets = {}
				for _, v in ipairs(o.possible_targets) do
					if not army_logic:has_tile_enemy_army(tostring(v), game_data.me) then
						updated_possible_targets[#updated_possible_targets + 1] = v
					end
				end
				o.possible_targets = updated_possible_targets
			end
			o.possible_targets = supply_logic.filter_target_candidates(
					tostring(o.to_send.sourceTileNumber),
					how_many,
					o.possible_targets,
					o.my_armies
			)
		end
		return o.possible_targets
	end

	---@return number
	local function get_source_tile_num()
		return o.to_send.sourceTileNumber
	end

	---@return table Army in GUI format
	local function get_remaining_army()
		local result = {}
		for i_type, i_count in pairs(o.init_army) do
			result[i_type] = i_count
			for _, v in pairs(o.sent) do
				result[i_type] = result[i_type] - (v[i_type] or 0)
			end
			if result[i_type] <= 0 then
				result[i_type] = nil
			end
		end
		return result
	end

	---@param to_tile_num string Tile number as string
	---@param counts GUIArmyToSend Map of types and counts
	local function add_partial_order(to_tile_num, counts)
		if army_logic:has_tile_enemy_army(to_tile_num, game_data.me) then
			o.enemy_at_tile_num = to_tile_num
		end
		o.to_send.targets[to_tile_num] = army_logic:to_server_format(counts)
		o.to_send_targets_count_changed = true
		update_my_armies(to_tile_num, counts, true)
		o.sent[to_tile_num] = counts
		if o.possible_targets then
			table.remove(o.possible_targets, utils.index_of(o.possible_targets, tonumber(to_tile_num)))
		end
	end

	---@param to_tile_num string Tile number as string
	---@return boolean Partial orders are empty
	local function delete_partial_order(to_tile_num)
		if o.enemy_at_tile_num == to_tile_num then
			o.enemy_at_tile_num = false
		end
		update_my_armies(to_tile_num, army_logic:to_gui_format(o.to_send.targets[to_tile_num]), false)
		o.to_send.targets[to_tile_num] = nil
		o.to_send_targets_count_changed = true
		o.sent[to_tile_num] = nil
		o.possible_targets[#o.possible_targets + 1] = tonumber(to_tile_num)
		return not next(o.to_send.targets)
	end

	return {
		add_partial_order = add_partial_order,
		delete_partial_order = delete_partial_order,
		get_message_to_server = get_message_to_server,
		get_remaining_army = get_remaining_army,
		get_source_tile_num = get_source_tile_num,
		get_possible_targets = get_possible_targets,
	}
end

return new_march_order