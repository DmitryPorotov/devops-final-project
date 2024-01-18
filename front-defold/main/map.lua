local labels = require "main/labels"
local utils = require "main/utils"

local _M = {
	UNIT_OFFSETS = {
		vmath.vector3(10, -30, 0.5),
		vmath.vector3(35, -30, 0.5),
		vmath.vector3(60, -30, 0.5),
		vmath.vector3(85, -30, 0.5),
	},
	PORT_SHIPS_OFFSET = {
		vmath.vector3(-0, 0, 0.5),
		vmath.vector3(-25, 0, 0.5),
		vmath.vector3(25, 0, 0.5),
	},
	armies = {},
	me = "kraken",
	phase = "addOrder",
	orders = {},
}

function _M.is_unit_commandable(type)
	return type ~= "garrison" and type ~= "powerToken"
end

function _M.select_label(self, label_hash)
	local id_str = labels.to_id(label_hash)
	local tile_num = string.match(id_str, "^/(%d+)")
	if self.phase == "addOrder" then
		self:tile_selectable_for_order(tonumber(tile_num), label_hash)
	end
end

function _M.tile_selectable_for_order(self, tile_num, label_hash)
	if self.armies[tile_num] and #self.armies[tile_num] > 0 and self.armies[tile_num][1].house == self.me 
	and (self.is_unit_commandable(self.armies[tile_num][1].type) or #self.armies[tile_num] > 1)
	then
		labels:select(label_hash)
		local deleted = nil
		if self.orders[label_hash] then
			go.delete(self.orders[label_hash])
			local script_url = tostring(self.orders[label_hash][hash('/order')]):match("%[(.+)%]") .. "#order"
			deleted = utils.ORDERS[go.get(script_url, "type")] .. go.get(script_url, "number")
			self.orders[label_hash] = nil
		end
		msg.post("/gui", "show_orders_menu", { label = label_hash, deleted = deleted })
	end
end

function _M.add_order(self, message)
	labels:unselect(message.label)
	local order_type = hash(message.order:sub(1, -2))
	local order_num = tonumber(message.order:sub(-1))
	local position = vmath.vector3(0, 25, 0)
	local order = collectionfactory.create("/map#orderfactory", position, nil, 
	{
		[hash("/order")] = {
			type = order_type,
			number = order_num
		}
	},
	.75)
	go.set_parent(order[hash("/order")], message.label)
	self.orders[message.label] = order
end

function _M.set_units(self, tile_num, units)
	table.insert(self.armies, tile_num, units)
	-- table.insert(self.armies_by_house[units[1].house], tile_num, units)
	local label_id = labels.LABEL_IDS[tonumber(tile_num)]
	local is_port = labels.is_port(label_id)
	local label_location = go.get_position("/" .. label_id)
	if not is_port then
		label_location = label_location + go.get_position("/" .. tile_num .. "shield")
	end
	for i, v in ipairs(units) do
		if self.is_unit_commandable(v.type) then
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