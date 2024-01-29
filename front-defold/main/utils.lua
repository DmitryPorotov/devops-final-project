-- Put functions in this file to use them in several other scripts.
-- To get access to the functions, you need to put:
-- require "my_directory.my_file"
-- in any script using the functions.
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
	index_of = function (table, value)
		for i, v in ipairs(table) do
			if v == value then
				return i
			end
		end
		return nil
	end,
	hash_to_id = function(hash)
		return string.sub(tostring(hash), 8, #tostring(hash) - 1)
	end
}

return _M