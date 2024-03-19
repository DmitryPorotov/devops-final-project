local _M = {
	HOUSES = {
		[hash("lion")] = "lion",
		[hash("kraken")] = "kraken",
		[hash("moose")] = "moose",
		[hash("rose")] = "rose",
		[hash("pufferfish")] = "pufferfish",
		[hash("wolf")] = "wolf",
		[hash("neutral")] = "neutral"
	},
	MIL_UNITS = {
		[hash("knights")] = "knights",
		[hash("footmen")] = "footmen",
		[hash("ships")] = "ships",
		[hash("siegeEngines")] = "siegeEngines",
	},
	ORDERS = {
		[hash("consolidate")] = "consolidate",
		[hash("raid")] = "raid",
		[hash("march")] = "march",
		[hash("defend")] = "defend",
		[hash("support")] = "support",
	},
	ANIMATION_TIME = .15,
	---Returns index of item in a table, returns nil if not found
	---@param table table
	---@param value any
	index_of = function (table, value)
		for i, v in ipairs(table) do
			if v == value then
				return i
			end
		end
		return nil
	end,
}

function _M.build_unit_and_count_phrase(type, count)
	if type == "footmen" then
		if count == 1 then
			return '1 footman'
		else
			return count .. ' footmen'
		end
	elseif type == "knights" then
		if count == 1 then
			return '1 knight'
		else
			return count .. ' knights'
		end
	elseif type == "ships" then
		if count == 1 then
			return '1 ship'
		else
			return count .. ' ships'
		end
	elseif type == "siegeEngines" then
		if count == 1 then
			return '1 siege engine'
		else
			return count .. ' siege engines'
		end
	end
end

function _M.is_unit_commandable(type)
	return type ~= "garrison" and type ~= "powerToken"
end

function _M.is_port(id)
	return string.find(id, "%dport_")
end

function _M.noop()
end

function _M.filter_my_armies(armies, me)
	local my = {}
	for i, v in pairs(armies) do
		if v[1].house == me then
			my[i] = v
		end
	end
	return my
end

return _M