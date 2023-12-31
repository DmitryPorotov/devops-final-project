-- Put functions in this file to use them in several other scripts.
-- To get access to the functions, you need to put:
-- require "my_directory.my_file"
-- in any script using the functions.
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
	selected = nil
}

function _M.to_id(hash)
	return string.sub(tostring(hash), 8, #tostring(hash) - 1)
end

local function is_port(id)
	return string.find(id, "%dport_")
end

function _M.select(self, label)
	local id = _M.to_id(label)
	if is_port(id) then
		go.set(id .. '#bg_selected', 'tint', self.LABEL_SELECT_COLOR)
	else
		go.set(id .. '#label_bg_sel', 'tint', self.LABEL_SELECT_COLOR)
	end

	if self.selected then
		self:unselect(self.selected)
		if self.selected == label then
			self.selected = nil
			return
		end
	end

	self.selected = label
end

function _M.unselect(self, label)
	local id = _M.to_id(label)
	if is_port(id) then
		go.set(id .. '#bg_selected', 'tint', vmath.vector4(1,1,1,0))
	else
		go.set(id .. '#label_bg_sel', 'tint', vmath.vector4(1,1,1,1))
	end
end

function _M.init(self)
	go.set("/2the_shivering_sea#label_bg", "tint", self.HOUSE_COLORS.wolf)
	go.set("/3winterfell#label_bg", "tint", self.HOUSE_COLORS.wolf)
	go.set("/7white_harbor#label_bg", "tint", self.HOUSE_COLORS.wolf)

	go.set("/12greywater_watch#label_bg", "tint", self.HOUSE_COLORS.kraken)
	go.set("/15ironmans_bay#label_bg", "tint", self.HOUSE_COLORS.kraken)
	go.set("/16pyke#label_bg", "tint", self.HOUSE_COLORS.kraken)

	go.set("/21the_golden_sound#label_bg", "tint", self.HOUSE_COLORS.lion)
	go.set("/22lannisport#label_bg", "tint", self.HOUSE_COLORS.lion)
	go.set("/27stoney_sept#label_bg", "tint", self.HOUSE_COLORS.lion)

	go.set("/30shipbreaker_bay#label_bg", "tint", self.HOUSE_COLORS.moose)
	go.set("/31dragonstone#label_bg", "tint", self.HOUSE_COLORS.moose)
	go.set("/40kingswood#label_bg", "tint", self.HOUSE_COLORS.moose)

	go.set("/38highgarden#label_bg", "tint", self.HOUSE_COLORS.rose)
	go.set("/41redwyne_straights#label_bg", "tint", self.HOUSE_COLORS.rose)
	go.set("/44dornish_marches#label_bg", "tint", self.HOUSE_COLORS.rose)

	go.set("/50sea_of_dorn#label_bg", "tint", self.HOUSE_COLORS.pufferfish)
	go.set("/54salt_shore#label_bg", "tint", self.HOUSE_COLORS.pufferfish)
	go.set("/55sunspear#label_bg", "tint", self.HOUSE_COLORS.pufferfish)

	go.set("/4port_winterfell#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set("/8port_white_harbor#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set("/17port_pyke#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set("/23port_lannisport#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set("/32port_dragonstone#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set("/43port_oldtown#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set("/47port_storms_end#bg_selected", "tint", vmath.vector4(1,1,1,0))
	go.set("/56port_sunspear#bg_selected", "tint", vmath.vector4(1,1,1,0))

	go.set("/0bay_of_ice#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/1castle_black#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/5karhold#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/6the_stony_shore#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/9widows_watch#label_bg", "tint", self.HOUSE_COLORS.neutral)

	go.set("/10sunset_sea#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/11flints_finger#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/13moat_calin#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/14the_narrow_sea#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/18seaguard#label_bg", "tint", self.HOUSE_COLORS.neutral)
	
	go.set("/19the_twins#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/20the_fingers#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/24riverrun#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/25the_mountains_of_the_moon#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/26the_eyrie#label_bg", "tint", self.HOUSE_COLORS.neutral)

	go.set("/28herrenhal#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/29crackclaw_point#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/24riverrun#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/33searoad_marches#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/34blackwater#label_bg", "tint", self.HOUSE_COLORS.neutral)
	
	go.set("/35kings_landing#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/36blackwater_bay#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/37west_summer_sea#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/39the_reach#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/42oldtown#label_bg", "tint", self.HOUSE_COLORS.neutral)

	go.set("/45the_boneway#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/46storms_end#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/48three_towers#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/49princes_pass#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/51the_arbor#label_bg", "tint", self.HOUSE_COLORS.neutral)

	go.set("/52starfall#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/53yronwood#label_bg", "tint", self.HOUSE_COLORS.neutral)
	go.set("/57east_summer_sea#label_bg", "tint", self.HOUSE_COLORS.neutral)
end

return _M
