--note: a mock for tests to work
if not hash then
	function hash(v) return v end
end

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
	HOUSE_REAL_NAMES = {
		lion = 'Lannister',
		kraken = 'Greyjoy',
		moose = 'Baratheon',
		rose = 'Tyrell',
		pufferfish = 'Martell',
		wolf = 'Stark',
	},
	ANIMATION_TIME = .15,
	---Returns index of item in a table, returns nil if not found
	---@generic T
	---@param tab T[]
	---@param value_or_comp T | fun(v:T):boolean
	---@return number | nil
	index_of = function (tab, value_or_comp)
		if type(value_or_comp) == 'function' then
			for i, v in ipairs(tab) do
				if value_or_comp(v) then
					return i
				end
			end
		else
			for i, v in ipairs(tab) do
				if v == value_or_comp then
					return i
				end
			end
		end
		return nil
	end,
}

function _M.is_unit_commandable(type)
	return type ~= "garrison" and type ~= "powerToken"
end

function _M.is_port(id)
	return string.find(id, "%dport_")
end

function _M.noop()
end

function _M.unimplemented()
	error('This function is unimplemented.')
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