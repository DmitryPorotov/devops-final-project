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
	house = false,
	to_select_from_tile = false
}

function _M:get_center_of_territories(house)
	local positions = {}
	for i, v in pairs(self.armies_by_house[house]) do
		positions[#positions + 1] = go.get_position(labels.LABEL_IDS[tonumber(i)])
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
		self:tile_select_for_add_order(tile_num, label_hash)
	elseif self.phase == "openOrders" then
	elseif self.phase == "resolveMarchOrder" then
		if self.to_select_from_tile then
			self:tile_select_target(tile_num, label_hash)
		else
			self:tile_select_for_order_resolve(tile_num, label_hash, 'march')
		end
	elseif self.phase == "ravenChangeOrder" then
		self:tile_select_for_raven_change_order(tile_num, label_hash)
	end
end

function _M:set_phase(phase)
	self.phase = phase
end

function _M:set_house(house)
	self.house = house
end

function _M:set_to_select_from_tile(tile_num)
	self.to_select_from_tile = tile_num
end

local function get_order_type(self, label_hash)
	local script_url = msg.url("main", self.orders[label_hash][hash('/order')], "order")
	return utils.ORDERS[go.get(script_url, "type")] .. go.get(script_url, "number"), utils.HOUSES[go.get(script_url, "house")]
end

local function delete_order_if_exists(self, label_hash)
	if self.orders[label_hash] then
		local order_type = get_order_type(self, label_hash)
		go.delete(self.orders[label_hash], true)
		self.orders[label_hash] = nil
		return order_type
	end
end

local function get_tile_name(tile_num, label_hash)
	if utils.is_port(labels.LABEL_HASHES[label_hash]) then
		return label.get_text(labels.LABEL_IDS[tile_num - 1] .. "#label") .. ' port'
	else
		return label.get_text(labels.LABEL_IDS[tonumber(tile_num)] .. "#label")
	end
end

local function open_orders_menu_for_label(tile_num, label_hash, deleted, for_raven)
	msg.post("/gui", "show_orders_menu", {
		label = label_hash,
		tile_num = tile_num,
		name = get_tile_name(tile_num, label_hash),
		deleted = deleted,
		for_raven = for_raven,
	})
end

function _M:tile_select_target(tile_num, label_hash)
	if labels:is_highlighted(tile_num) then
		labels:select_target(label_hash)
		msg.post('/gui', 'target_selected', {
			label = label_hash,
			tile_num = tile_num,
			name = get_tile_name(tile_num, label_hash),
		})
	end
end

function _M:tile_select_for_add_order(tile_num, label_hash)
	if self.armies_by_house[game_data.me][tile_num] and #self.armies_by_house[game_data.me][tile_num] > 0
	and (utils.is_unit_commandable(self.armies[tile_num][1].type) or #self.armies[tile_num] > 1)
	then
		labels:select(label_hash)
		local deleted = delete_order_if_exists(self, label_hash)
		open_orders_menu_for_label(tile_num, label_hash, deleted)
	end
end

function _M:tile_select_for_raven_change_order(tile_num, label_hash)
	if game_data.tracks.court[1] == game_data.me and self.orders[label_hash] then
		labels:select(label_hash)
		local selected_order = get_order_type(self, label_hash)
		open_orders_menu_for_label(tile_num, label_hash, selected_order, true)
	end
end

function _M:tile_select_for_order_resolve(tile_num, label_hash, order_type)
	if self.house ~= game_data.me then
		return
	end
	if self.orders[label_hash] then
		local order_on_label, house = get_order_type(self, label_hash)
		if house == game_data.me and order_on_label:find(order_type) then
			labels:select(label_hash)
			msg.post("/gui", "resolve_order", {
				label = label_hash,
				tile_num = tile_num,
				name = get_tile_name(tile_num, label_hash),
				order = order_on_label
			})
		end
	end
end

function _M:add_order(message)
	local label = type(message.label) == "number" and hash(labels.LABEL_IDS[message.label]) or message.label
	delete_order_if_exists(self, label)
	labels:unselect()
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
			v.hash = factory.create("/map#millitary_unit_facrory", position, nil, {house = hash(v.house), type = hash(v.type)})
		end
	end
	-- factory.create("/map#millitary_unit_facrory", vmath.vector3(350, 250, 0.125), nil, {house = hash("lion"), type = hash("siegeEngines")})
end

local function pluck_last_unit_of_type(units_at_tile, unit)
	for i = 1 ,#units_at_tile do
		if units_at_tile[i].house == unit.house and units_at_tile[i].type == unit.type then
			return table.remove(units_at_tile, i)
		end
	end
end

function _M:move_units(from_tile, to_tile, units, through_tiles)
	local to_tile_id = labels.LABEL_IDS[tonumber(to_tile)]
	local to_tile_is_port = utils.is_port(to_tile_id)
	for _, v in ipairs(units) do
		local u = pluck_last_unit_of_type(self.armies[tostring(from_tile)], v)
		local num_units_at_target = self.armies[to_tile] and #self.armies[to_tile] or 0
		local to_pos = go.get_position(to_tile_id)
				+ (
				to_tile_is_port
				and self.PORT_SHIPS_OFFSET[num_units_at_target + 1]
				or (
						self.UNIT_OFFSETS[num_units_at_target + 1]
						+ go.get_position("/" .. to_tile .. "shield")
					)
				)
		if not self.armies[to_tile] then
			self.armies[to_tile] = {u}
		else
			self.armies[to_tile][#self.armies[to_tile]+1] = u
		end
		go.animate(u.hash, 'position', go.PLAYBACK_ONCE_FORWARD, to_pos, go.EASING_LINEAR, 1)
	end
end

return _M
