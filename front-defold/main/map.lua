local labels = require "main/labels"

local _M = {
	UNIT_OFFSETS = {
		vmath.vector3(-50, -30, 0.5),
		vmath.vector3(-25, -30, 0.5),
		vmath.vector3(0, -30, 0.5),
		vmath.vector3(25, -30, 0.5),
	},
	PORT_SHIPS_OFFSET = {
		vmath.vector3(-0, 0, 0.5),
		vmath.vector3(-25, 0, 0.5),
		vmath.vector3(25, 0, 0.5),
	},
	units
}

function _M.set_units(self, tile_num, units)
	local label_id = labels.LABEL_IDS[tonumber(tile_num)]
	local is_port = labels.is_port(label_id)
	local label_location
	if is_port then
		label_location = go.get_position("/" .. label_id)
	else
		label_location = go.get_position("/" .. label_id .. "#shield")
	end
	for i, v in ipairs(units) do
		if v.type ~= "garrison" and v.type ~= "powerToken" then
			local position
			if is_port then
				position = label_location + self.PORT_SHIPS_OFFSET[i]
			else
				position = label_location + self.UNIT_OFFSETS[i]
			end
			factory.create("/map#millitary_unit_facrory", position, nil, {house = hash(v.house), type = hash(v.type)})
		end
	end
	-- factory.create("/map#millitary_unit_facrory", vmath.vector3(350, 250, 0.125), nil, {house = hash("lion"), type = hash("siegeEngines")})
end

return _M