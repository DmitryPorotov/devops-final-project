local utils = require "main/utils"

local _M = {
	HOUSE_COLORS = {
		lion = vmath.vector4(.925, .302, .302, 1),
		kraken = vmath.vector4(.188, .188, .188, 1),
		moose = vmath.vector4(.992, .894, .255, 1),
		rose = vmath.vector4(.391, .671, .290, 1),
		pufferfish = vmath.vector4(.988, .718, .333, 1),
		wolf = vmath.vector4(.91, .91, .91, 1),
		neutral = vmath.vector4(1, .96, .7, 1)
		
	},
	LABEL_TEXT_COLOR = {
		kraken = vmath.vector3(.95, .95, .95),
		others = vmath.vector3(.2, .2, .2)
	},
	LABEL_SELECT_COLOR = vmath.vector4(0,0,0,1),
	selected = nil,
	LABEL_IDS = {
		[0] = "/0bay_of_ice",
		"/1castle_black",
		"/2the_shivering_sea",
		"/3winterfell",
		"/4port_winterfell",
		"/5karhold",
		"/6the_stony_shore",
		"/7white_harbor",
		"/8port_white_harbor",
		"/9widows_watch",
		"/10sunset_sea",
		"/11flints_finger",
		"/12greywater_watch",
		"/13moat_calin",
		"/14the_narrow_sea",
		"/15ironmans_bay",
		"/16pyke",
		"/17port_pyke",
		"/18seaguard",
		"/19the_twins",
		"/20the_fingers",
		"/21the_golden_sound",
		"/22lannisport",
		"/23port_lannisport",
		"/24riverrun",
		"/25the_mountains_of_the_moon",
		"/26the_eyrie",
		"/27stoney_sept",
		"/28herrenhal",
		"/29crackclaw_point",
		"/30shipbreaker_bay",
		"/31dragonstone",
		"/32port_dragonstone",
		"/33searoad_marches",
		"/34blackwater",
		"/35kings_landing",
		"/36blackwater_bay",
		"/37west_summer_sea",
		"/38highgarden",
		"/39the_reach",
		"/40kingswood",
		"/41redwyne_straights",
		"/42oldtown",
		"/43port_oldtown",
		"/44dornish_marches",
		"/45the_boneway",
		"/46storms_end",
		"/47port_storms_end",
		"/48three_towers",
		"/49princes_pass",
		"/50sea_of_dorn",
		"/51the_arbor",
		"/52starfall",
		"/53yronwood",
		"/54salt_shore",
		"/55sunspear",
		"/56port_sunspear",
		"/57east_summer_sea",
	},
	LABEL_HASHES = {},
	highlighted = {},
	selected_target = nil,
}

function _M:select(label_hash)
	local id = self.LABEL_HASHES[label_hash]
	if utils.is_port(id) then
		go.set(id .. '#bg_selected', 'tint', self.LABEL_SELECT_COLOR)
	else
		go.set(id .. '#label_bg_sel', 'tint', self.LABEL_SELECT_COLOR)
	end

	if self.selected then
		local s = self.selected
		self:unselect()
		if s == label_hash then
			return false
		end
	end

	self.selected = label_hash
	return true
end

function _M:unselect()
	if not self.selected then return end
	local id = self.LABEL_HASHES[self.selected]
	self.selected = nil
	if utils.is_port(id) then
		go.set(id .. '#bg_selected', 'tint', vmath.vector4(1,1,1,0))
	else
		go.set(id .. '#label_bg_sel', 'tint', vmath.vector4(1,1,1,1))
	end
end

local function build_bg_id(id)
	if utils.is_port(id) then
		return id .. '#bg_selected'
	else
		return id .. '#label_bg_sel'
	end
end

function _M:highlight(tile_numbers)
	for _, v in ipairs(tile_numbers) do
		local id = build_bg_id(self.LABEL_IDS[v])

		self.highlighted[tostring(v)] = id
		go.set(id, 'tint', vmath.vector4(1,1,0,0))
		go.animate(id, 'tint', go.PLAYBACK_LOOP_PINGPONG, vmath.vector4(1,1,0,1), go.EASING_LINEAR, 0.7)
	end
end

function _M:is_highlighted(tile_num)
	if self.highlighted[tile_num] then
		return true
	end
	return false
end

function _M:select_target(label_hash)
	if label_hash == self.selected_target then
		return
	elseif self.selected_target then
		self:unselect_target()
	end
	local id = build_bg_id(self.LABEL_HASHES[label_hash])
	go.cancel_animations(id, 'tint')
	go.set(id, 'tint', vmath.vector4(0,1,0,0))
	go.animate(id, 'tint', go.PLAYBACK_LOOP_PINGPONG, vmath.vector4(0,1,0,1), go.EASING_LINEAR, 0.7)
	self.selected_target = label_hash
end

function _M:unselect_target()
	local id = build_bg_id(self.LABEL_HASHES[self.selected_target])
	go.cancel_animations(id, 'tint')
	go.set(id, 'tint', vmath.vector4(1,1,0,0))
	go.animate(id, 'tint', go.PLAYBACK_LOOP_PINGPONG, vmath.vector4(1,1,0,1), go.EASING_LINEAR, 0.7)
	self.selected_target = nil
end

function _M:init()
	for i = 0, 57 do
		self.LABEL_HASHES[hash(self.LABEL_IDS[i])] = self.LABEL_IDS[i]
	end
	go.set(self.LABEL_IDS[2] .. "#label_bg", "tint", self.HOUSE_COLORS.wolf)
	go.set(self.LABEL_IDS[3] .. "#label_bg", "tint", self.HOUSE_COLORS.wolf)
	go.set(self.LABEL_IDS[7] .. "#label_bg", "tint", self.HOUSE_COLORS.wolf)

	go.set(self.LABEL_IDS[12] .. "#label_bg", "tint", self.HOUSE_COLORS.kraken)
	go.set(self.LABEL_IDS[15] .. "#label_bg", "tint", self.HOUSE_COLORS.kraken)
	go.set(self.LABEL_IDS[16] .. "#label_bg", "tint", self.HOUSE_COLORS.kraken)

	go.set(self.LABEL_IDS[21] .. "#label_bg", "tint", self.HOUSE_COLORS.lion)
	go.set(self.LABEL_IDS[22] .. "#label_bg", "tint", self.HOUSE_COLORS.lion)
	go.set(self.LABEL_IDS[27] .. "#label_bg", "tint", self.HOUSE_COLORS.lion)

	go.set(self.LABEL_IDS[30] .. "#label_bg", "tint", self.HOUSE_COLORS.moose)
	go.set(self.LABEL_IDS[31] .. "#label_bg", "tint", self.HOUSE_COLORS.moose)
	go.set(self.LABEL_IDS[40] .. "#label_bg", "tint", self.HOUSE_COLORS.moose)

	go.set(self.LABEL_IDS[38] .. "#label_bg", "tint", self.HOUSE_COLORS.rose)
	go.set(self.LABEL_IDS[41] .. "#label_bg", "tint", self.HOUSE_COLORS.rose)
	go.set(self.LABEL_IDS[44] .. "#label_bg", "tint", self.HOUSE_COLORS.rose)

	go.set(self.LABEL_IDS[50] .. "#label_bg", "tint", self.HOUSE_COLORS.pufferfish)
	go.set(self.LABEL_IDS[54] .. "#label_bg", "tint", self.HOUSE_COLORS.pufferfish)
	go.set(self.LABEL_IDS[55] .. "#label_bg", "tint", self.HOUSE_COLORS.pufferfish)

	go.set(self.LABEL_IDS[4] .. "#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set(self.LABEL_IDS[8] .. "#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set(self.LABEL_IDS[17] .. "#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set(self.LABEL_IDS[23] .. "#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set(self.LABEL_IDS[32] .. "#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set(self.LABEL_IDS[43] .. "#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set(self.LABEL_IDS[47] .. "#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set(self.LABEL_IDS[56] .. "#bg_selected", "tint", vmath.vector4(1,1,1,0))

	go.set(self.LABEL_IDS[0] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[1] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[5] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[6] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[9] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)

	go.set(self.LABEL_IDS[10] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[11] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[13] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[14] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[18] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	
	go.set(self.LABEL_IDS[19] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[20] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[24] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[25] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[26] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)

	go.set(self.LABEL_IDS[28] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[29] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[24] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[33] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[34] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	
	go.set(self.LABEL_IDS[35] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[36] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[37] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[39] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[42] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)

	go.set(self.LABEL_IDS[45] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[46] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[48] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[49] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[51] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)

	go.set(self.LABEL_IDS[52] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[53] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set(self.LABEL_IDS[57] .. "#label_bg", "tint", self.HOUSE_COLORS.neutral)
end

return _M
