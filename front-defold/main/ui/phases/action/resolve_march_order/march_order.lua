local army_logic = require "main/ui/army_logic"
local travel_logic = require "main/ui/travel_logic"
local utils = require "main/utils"


---@param source_tile_num number Source tile number
---@param init_army table Army in GUI format
local function newMarchOrder(source_tile_num, init_army)
	local o = {
		init_army = init_army,
		to_send = {
			sourceTileNumber = tonumber(source_tile_num),
			targets = {}
		},
		sent = {},
		possible_targets = nil,
	}

	local function get_message_to_server()
		return {
			actionType = 'resolveMarchOrder',
			player_action = o.to_send
		}
	end

	local function get_possible_targets()
		if not o.possible_targets then
			o.possible_targets = travel_logic:calculate_possible_destinations(o.to_send.sourceTileNumber)
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
	---@param counts table Map of types and counts
	local function add_partial_order(to_tile_num, counts)
		o.to_send.targets[to_tile_num] = army_logic.to_server_format(counts)
		o.sent[to_tile_num] = counts
		if o.possible_targets then
			table.remove(o.possible_targets, utils.index_of(o.possible_targets, tonumber(to_tile_num)))
		end
	end

	---@param to_tile_num string Tile number as string
	local function delete_partial_order(to_tile_num)
		o.to_send.targets[to_tile_num] = nil
		o.sent[to_tile_num] = nil
		o.possible_targets[#o.possible_targets + 1] = tonumber(to_tile_num)
		return next(o.to_send.targets)
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

return newMarchOrder