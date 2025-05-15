local event_dispatcher = require "main/ui/event_dispatcher"
local events = require "main/ui/events"
local game_data = require "main/ui/game_data"
local hints = require "main/ui/hints"
local player_panels = require "main/ui/player_panel"

local _M = {
	current_order = 1,
	selected_tile = -1
}

function _M:get_raid_targets(is_star)
	local tile = game_data.gameRules.board[tonumber(self.selected_tile) + 1]
	pprint(tile)
	local neighbours = tile.neighbourTiles
	--find orders on neighbours
	
	for house, orders in pairs(game_data.placedOrders) do
		if house ~= game_data.me then
			
		end
	end
end

---@private
function _M:on_map_resolve_order(message)
	print('in on_map_resolve_order')
	pprint(message)
	if message.is_selected then
		hints:set_next_button_enabled(true)
		hints:set_hint_text('Resolve the Raid order in ' .. message.name .. '?')
		self.selected_tile = message.tile_num
		self:get_raid_targets(message.order == 'raid3')
	else
		hints:set_next_button_enabled(false)
		hints:set_hint_text('Select a Raid order to resolve.')
		self.selected_tile = -1
	end
end

function _M:on_hints_next_button_click()
end

function _M:init(house)
	player_panels:set_player_turn(house)
	if house ~= game_data.me then
		return
	end
	self:set_up_hint()
	event_dispatcher.on(events.map_resolve_order, self.on_map_resolve_order, self)
	event_dispatcher.on(events.hints_goto_button_click, self.on_hints_goto_button_click, self)
end

function _M:set_up_hint()
	self.raids = {}
	self.raids_arr = {}
	self.count = 0
	for i, v in pairs(game_data.placedOrders[game_data.me]) do
		if v.type == 'raid' then
			self.raids[i] = v
			table.insert(self.raids_arr, i)
			self.count = self.count + 1
		end
	end
	hints:set_goto_button_enabled(true)
	hints:set_next_button_enabled(false)
	hints:set_goto_count_text(self.count)
	hints:set_hint_text('Select a Raid order to resolve.')
	hints:set_enabled(true)
end

function _M:on_hints_goto_button_click()
	msg.post("/map", "move_camera_to_label", {tile_num = self.raids_arr[self.current_order]})
	if self.current_order >= self.count then
		self.current_order = 1
	else
		self.current_order = self.current_order + 1
	end
end

function _M:clean_up()
	event_dispatcher.off(events.map_resolve_order, self.on_map_resolve_order)
	-- event_dispatcher.off(events.march_select_army_ok_button_click, self.on_march_select_army_ok_button_click)
	event_dispatcher.off(events.hints_goto_button_click, self.on_hints_goto_button_click)
	event_dispatcher.off(events.hints_next_button_click, self.on_hints_next_button_click)
	-- event_dispatcher.off(events.map_target_selected, self.on_map_target_selected)
	-- event_dispatcher.off(events.partial_march_remove, self.on_partial_march_remove)
	-- msg.post('/map', 'unselect_label')
	-- msg.post('/map', 'unhighlight')
	-- hints:clean_up()
	-- march_select_army:close()
	-- partial_march_orders:set_enabled(false)
end

return _M