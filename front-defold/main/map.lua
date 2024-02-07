local labels = require "main/labels"
local utils = require "main/utils"
local game_data = require "main/ui/game_data"

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
	armies_by_house = {},
	phase = "addOrder",
	orders = {},
}

function _M:get_center_of_territories(house)
	local positions = {}
	for i, v in pairs(self.armies_by_house[house]) do
		table.insert(positions, go.get_position(labels.LABEL_IDS[tonumber(i)]))
	end
	local sum = vmath.vector3()
	for i = 1, #positions do
		sum = sum + positions[i]
	end
	local avg = sum / #positions
	avg.z = go.get_position("/camera").z
	return avg
end

function _M:select_label(label_hash)
	local id_str = labels.LABEL_HASHES[label_hash]
	local tile_num = string.match(id_str, "^/(%d+)")
	if self.phase == "addOrder" then
		self:tile_selectable_for_order(tile_num, label_hash)
	elseif self.phase == "openOrders" then
	end
end

function _M:set_phase(phase)
	self.phase = phase
end

function _M:tile_selectable_for_order(tile_num, label_hash)
	if self.armies_by_house[game_data.me][tile_num] and #self.armies_by_house[game_data.me][tile_num] > 0
	and (utils.is_unit_commandable(self.armies[tile_num][1].type) or #self.armies[tile_num] > 1)
	then
		labels:select(label_hash)
		local deleted = nil
		if self.orders[label_hash] then
			go.delete(self.orders[label_hash])
			local script_url = msg.url("main", self.orders[label_hash][hash('/order')], "order")
			deleted = utils.ORDERS[go.get(script_url, "type")] .. go.get(script_url, "number")
			self.orders[label_hash] = nil
		end
		local name
		if utils.is_port(tostring(label_hash)) then
			name = "Port of " .. label.get_text(labels.LABEL_IDS[tile_num - 1] .. "#label")
		else
			name = label.get_text(labels.LABEL_IDS[tonumber(tile_num)] .. "#label")
		end
		msg.post("/gui", "show_orders_menu", { 
			label = label_hash, 
			tile_num = tile_num, 
			name = name, 
			deleted = deleted 
		})
	end
end

function _M:add_order(message)
	local label = type(message.label) == "number" and hash(labels.LABEL_IDS[message.label]) or message.label
	labels:unselect(label)
	local order_type = hash(message.order:sub(1, -2))
	local order_num = tonumber(message.order:sub(-1))
	local position = vmath.vector3(0, 25, 0)
	local order = collectionfactory.create("/map#orderfactory", position, nil, 
	{
		[hash("/order")] = {
			type = order_type,
			number = order_num,
			is_opened = message.is_opened,
			house = hash(message.house),
		}
	},
	.75)
	go.set_parent(order[hash("/order")], label)
	msg.post(order[hash("/order")], "set_urls", {
		star_url = order[hash("/star")],
		opened_url = order[hash("/opened")],
		closed_url = order[hash("/closed")],
		number_url = order[hash("/number")],
	})
	self.orders[label] = order
end

function _M:remove_order(tile_num)
	local label_hash = hash(labels.LABEL_IDS[tile_num])
	if self.orders[label_hash] then
		go.delete(self.orders[label_hash])
		self.orders[label_hash] = nil
	end
end

function _M:set_units(tile_num, units)
	self.armies[tile_num] = units
	if not self.armies_by_house[units[1].house] then
		self.armies_by_house[units[1].house] = {}
	end
	self.armies_by_house[units[1].house][tile_num] = units
	local label_id = labels.LABEL_IDS[tonumber(tile_num)]
	local is_port = utils.is_port(label_id)
	local label_location = go.get_position(label_id)
	if not is_port then
		label_location = label_location + go.get_position("/" .. tile_num .. "shield")
	end
	for i, v in ipairs(units) do
		if utils.is_unit_commandable(v.type) then
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