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
	ANIMATION_TIME = .15,
}

return _M