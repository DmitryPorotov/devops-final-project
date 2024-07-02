local labels = require "main/labels"
local utils = require "main/utils"
local game_data = require "main/ui/game_data"

local MOVEMENT_SPEED = 200

---@class GameWorldMilUnit: MilitaryUnit
---@field hash hash

---@class GameWorldMap
---@field armies table<string, GameWorldMilUnit[]>
---@field armies_by_house table<string, table<string, GameWorldMilUnit[]>>
local _M = {
	UNIT_OFFSETS = {
		vmath.vector3(10, -30, 0.5),
		vmath.vector3(35, -30, 0.5),
		vmath.vector3(60, -30, 0.5),
		vmath.vector3(85, -30, 0.5),
	},
	ATTACKER_UNIT_OFFSETS = {
		vmath.vector3(15, -60, 0.5),
		vmath.vector3(40, -60, 0.5),
		vmath.vector3(65, -60, 0.5),
		vmath.vector3(90, -60, 0.5),
	},
	PORT_SHIPS_OFFSET = {
		vmath.vector3(-0, 0, 0.5),
		vmath.vector3(-25, 0, 0.5),
		vmath.vector3(25, 0, 0.5),
	},
	GARRISON_URLS = {
		'/3garrison',
		'/16garrison',
		'/22garrison',
		'/26garrison',
		'/31garrison',
		'/35garrison',
		'/38garrison',
		'/55garrison',
	},
	armies = {},
	armies_by_house = {},
	phase = "addOrder",
	orders = {},
	house = false,
	to_select_from_tile = false
}

function _M:init()
	for _, url in ipairs(self.GARRISON_URLS) do
		go.set(url, 'position.z', -.5)
	end
end

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
			if i == #units and not is_port then
				labels:set_tile_owner(tile_num, v.house)
			end
			local position
			if is_port then
				position = label_location + self.PORT_SHIPS_OFFSET[i]
			else
				position = label_location + self.UNIT_OFFSETS[i]
			end
			v.hash = factory.create("/map#millitary_unit_facrory", position, nil, {house = hash(v.house), type = hash(v.type)})
			go.set_scale(go.get_scale(v.hash) / go.get_scale('/map').x, v.hash)
		elseif v.type == 'powerToken' then
			labels:enable_power_token(tonumber(tile_num))
		else
			go.set('/' .. tile_num .. 'garrison', 'position.z', 0)
		end
	end
end

function _M:set_attacker_units(tile_num, units)
	self.armies_by_house[units[1].house][tile_num] = units
	for _, v in ipairs(units) do
		self.armies[tile_num][#self.armies[tile_num] + 1] = v
	end
	local label_id = labels.LABEL_IDS[tonumber(tile_num)]
	local label_location = go.get_position(label_id) + go.get_position("/" .. tile_num .. "shield")
	for i, v in ipairs(units) do
		local position = label_location + self.ATTACKER_UNIT_OFFSETS[i]
		v.hash = factory.create("/map#millitary_unit_facrory", position, nil, {house = hash(v.house), type = hash(v.type)})
		go.set_scale(go.get_scale(v.hash) / go.get_scale('/map').x, v.hash)
	end
end

local function pluck_last_unit_of_type(units_at_tile, unit)
	for i = 1 ,#units_at_tile do
		if units_at_tile[i].house == unit.house and units_at_tile[i].type == unit.type then
			return table.remove(units_at_tile, i)
		end
	end
end

local function calc_animation_time(...)
	local arg = {...}
	local dist = 0
	for i, v in ipairs(arg) do
		if arg[i+1] then
			dist = dist + math.abs(vmath.length(v - arg[i+1]))
		end
	end
	return dist / MOVEMENT_SPEED
end

---@param self GameWorldMap
---@param source_tile_num string
local function shift_leftover_army(self, source_tile_num)
	local army = self.armies[tostring(source_tile_num)]
	if army and army[1] then
		for i, v in ipairs(army) do
			if utils.is_unit_commandable(v.type) then
				local current_pos = go.get_position(v.hash)
				local id = labels.LABEL_IDS[source_tile_num]
				local needed_pos = go.get_position(id)
				if utils.is_port(id) then
					needed_pos = needed_pos
							+ self.PORT_SHIPS_OFFSET[i]
				else
					needed_pos = needed_pos
							+ self.UNIT_OFFSETS[i]
							+ go.get_position('/'..source_tile_num..'shield')
				end
				if math.abs(vmath.length(current_pos - needed_pos)) > 1 then
					go.animate(
							v.hash,
							'position',
							go.PLAYBACK_ONCE_FORWARD,
							needed_pos,
							go.EASING_LINEAR,
							0.2
					)
				end
			end
		end
	end
end

local function move_units_serially(self, from_tile, targets, through_tiles, for_attack, call_idx)
	if not next(targets) and call_idx == 1 then
		self:reassign_labels()
		shift_leftover_army(self, from_tile)
		return
	elseif not next(targets) then
		return
	end

	local to_tile, units = next(targets)
	targets[to_tile] = nil
	local to_tile_id = labels.LABEL_IDS[tonumber(to_tile)]
	local to_tile_is_port = utils.is_port(to_tile_id)
	for i, v in ipairs(units) do
		local u = pluck_last_unit_of_type(self.armies[tostring(from_tile)], v)
		if #self.armies[tostring(from_tile)] == 0 then
			self.armies[tostring(from_tile)] = nil
		end
		local num_units_at_target = for_attack and 0 or (self.armies[to_tile] and #self.armies[to_tile] or 0)
		local to_pos = go.get_position(to_tile_id)
				+ (
				to_tile_is_port
						and self.PORT_SHIPS_OFFSET[num_units_at_target + 1]
						or (
						(
								for_attack and
								self.ATTACKER_UNIT_OFFSETS[num_units_at_target + 1] or
								self.UNIT_OFFSETS[num_units_at_target + 1]
						)
								+ go.get_position("/" .. to_tile .. "shield")
				)
		)
		if not self.armies[to_tile] then
			self.armies[to_tile] = {u}
		else
			self.armies[to_tile][#self.armies[to_tile]+1] = u
		end
		go.animate(
				u.hash,
				'position',
				go.PLAYBACK_ONCE_FORWARD,
				to_pos,
				go.EASING_LINEAR,
				calc_animation_time(go.get_position(u.hash), to_pos),
				0,
				function()
					move_units_serially(self, from_tile, targets, through_tiles, for_attack, i)
				end
		)
	end
end

function _M:move_units(from_tile, targets, through_tiles)
	move_units_serially(self, from_tile, targets, through_tiles)
end

function _M:move_units_for_attack(from_tile, targets, through_tiles)
	move_units_serially(self, from_tile, targets, through_tiles, true)
end

function _M:reassign_labels()
	for i = 0, 57 do
		local army = self.armies[tostring(i)]
		if army and #army > 0 then
			labels:set_tile_owner(i, army[1].house)
		elseif game_data.gameRules.board[i + 1].homeOf then
			labels:set_tile_owner(i, game_data.gameRules.board[i + 1].homeOf)
		else
			labels:set_tile_owner(i, 'neutral')
		end
	end
end

function _M:clean_up()
	for tile, armies in pairs(self.armies) do
		for unit_idx, v in ipairs(armies) do
			if v.hash
			then go.delete(v.hash)
			end
		end
	end
	self.armies = {}
	self.armies_by_house = {}
	for _, v in pairs(self.orders) do
		if v
		then go.delete(v)
		end
	end
	self.orders = {}
	labels:clean_up()
end

---@param message KillUnitsMessage[]
function _M:kill_units(message)
	local to_kill = {}
	for _, kum in ipairs(message) do

	end
end

return _M
