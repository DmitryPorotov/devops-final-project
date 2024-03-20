local event_dispatcher = require "main/ui/event_dispatcher"
local game_data = require "main/ui/game_data"
local travel_logic = require "main/ui/travel_logic"
local army_logic = require "main/ui/army_logic"

local march_select_army = require "main/ui/dialogs/march_select_army"
local hints = require "main/ui/hints"
local player_panels = require "main/ui/player_panel"
local confirm = require "main/ui/dialogs/confirm"
local partial_march_orders = require "main/ui/partial_march_orders"

local _M = {
	current_order = 1,
	marches_arr = {},
	marches = {},
	count = 0,
	current_tile_num = false,
	message_to_server = {
		actionType = 'resolveMarchOrder'
	},
	from_name = '',
	target_ids = {},
	available_army_at_tile = {},
}

function _M:on_map_resolve_order(message)
	local army = game_data.armies[tostring(message.tile_num)]
	self.current_tile_num = tonumber(message.tile_num)
	self.available_army_at_tile = army_logic.to_gui_format(army)
	march_select_army:open(self.available_army_at_tile, message.label)
	march_select_army:set_from(message.name)
	self.from_name = message.name
end

local targets

local function calculate_remaining_army(self)
	local army = game_data.armies[tostring(message.tile_num)]

end

local function createMarchOrderTarget(self)
	partial_march_orders:add_order(self.target_ids[#self.target_ids], march_select_army:get_order_text())
end

function _M:on_march_select_army_ok_button_click(to_send)
	if to_send then
		self.message_to_server.sourceTileNumber = self.current_tile_num
		if not next(to_send) then
			confirm:open('Do you want to remove the March order\nfrom "'
					.. self.from_name .. '" and finish your turn?',
					function(result)
						if result then
							msg.post('/map', 'unselect_label')
							self:clean_up()
							self.message_to_server.targets = {}
							event_dispatcher.trigger('ws_send', {
								player_action = self.message_to_server
							})
						else
							msg.post('/map', 'unselect_label')
							msg.post('/map', 'hide_targets')
							march_select_army:close()
						end
					end)
			return
		end
		self.to_send = to_send
		targets = travel_logic:calculate_possible_destinations(self.current_tile_num)
		msg.post('/map', 'show_targets', {
			from_tile_num = self.current_tile_num,
			targets = targets
		})
		self.count = #targets
		self.current_order = 1
		hints:set_goto_count_text(self.count)
		hints:set_hint_text('Select a target territory.')
		event_dispatcher.off('hints_goto_button_click', self.on_hints_goto_button_click_select_source)
		event_dispatcher.on('hints_goto_button_click', self.on_hints_goto_button_click_select_target, self)
	else
		createMarchOrderTarget(self)
		--todo recalculate remaining army

		--todo check has more armies
		--todo disable ok again
		--todo unselect destination
	end
end

function _M:on_hints_goto_button_click_select_target()
	msg.post("/map", "move_camera_to_label", {tile_num = targets[self.current_order]})
	if self.current_order >= self.count then
		self.current_order = 1
	else
		self.current_order = self.current_order + 1
	end
end

function _M.on_march_select_army_to_send_changed(to_send)
	local text = ''
	if next(to_send) ~= nil then
		for k, v in pairs(to_send) do
			text = text .. army_logic.build_unit_and_count_phrase(k ,v) .. ', '
		end
		text = text:sub(1, -3)
	end
	march_select_army:set_who(text)
end

local function transform_army_to_send(to_send)
	local army = {}
	for k, v in pairs(to_send) do
		for i = 1, v do
			army[#army + 1] = {
				house = game_data.me,
				type = k
			}
		end
	end
	return army
end

function _M:on_map_target_selected(message)
	march_select_army:set_to(message.name)
	if not self.message_to_server.targets then
		self.message_to_server.targets = {}
	end
	self.message_to_server.targets[tostring(message.tile_num)] = transform_army_to_send(self.to_send)
	self.target_ids[#self.target_ids + 1] = message.tile_num
end

function _M:init()
	event_dispatcher.on('map_resolve_order', self.on_map_resolve_order, self)
	event_dispatcher.on('march_select_army_ok_button_click', self.on_march_select_army_ok_button_click, self)
	event_dispatcher.on('march_select_army_to_send_changed', self.on_march_select_army_to_send_changed)
	event_dispatcher.on("map_target_selected", self.on_map_target_selected, self)
	if game_data.subPhase.houseType == game_data.me then
		self:set_up_hint()
	end
	player_panels:set_player_turn(game_data.subPhase.houseType)
end

function _M:on_hints_goto_button_click_select_source()
	msg.post("/map", "move_camera_to_label", {tile_num = self.marches_arr[self.current_order]})
	if self.current_order >= self.count then
		self.current_order = 1
	else
		self.current_order = self.current_order + 1
	end
end

function _M:set_up_hint()
	self.marches = {}
	self.marches_arr = {}
	self.count = 0
	for i, v in pairs(game_data.placedOrders[game_data.me]) do
		if v.type == 'march' then
			self.marches[i] = v
			table.insert(self.marches_arr, i)
			self.count = self.count + 1
		end
	end
	hints:set_goto_button_enabled(true)
	hints:set_goto_count_text(self.count)
	hints:set_hint_text('Select a March order to resolve.')
	event_dispatcher.off('hints_goto_button_click', self.on_hints_goto_button_click_select_target)
	event_dispatcher.on('hints_goto_button_click', self.on_hints_goto_button_click_select_source, self)
	hints:set_hints_enabled(true)
end

function _M:clean_up()
	event_dispatcher.off('map_resolve_order', self.on_map_resolve_order)
	event_dispatcher.off('march_select_army_ok_button_click', self.on_march_select_army_ok_button_click)
	event_dispatcher.off('march_select_army_to_send_changed', self.on_march_select_army_to_send_changed)
	event_dispatcher.off('hints_goto_button_click', self.on_hints_goto_button_click_select_target)
	event_dispatcher.off('map_target_selected', self.on_map_target_selected)
	event_dispatcher.off('hints_goto_button_click', self.on_hints_goto_button_click_select_source)
	hints:clean_up()
	march_select_army:close()
	self.from_name = ''
end

return _M
