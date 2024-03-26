local event_dispatcher = require "main/ui/event_dispatcher"
local game_data = require "main/ui/game_data"

local march_select_army = require "main/ui/dialogs/march_select_army"
local hints = require "main/ui/hints"
local player_panels = require "main/ui/player_panel"
local partial_march_orders = require "main/ui/partial_march_orders"

local no_source_selected = require "main/ui/phases/action/resolve_march_order/states/no_source_selected"
local source_selected_no_targets = require "main/ui/phases/action/resolve_march_order/states/source_selected_no_targets"
local source_selected_partial_targets = require "main/ui/phases/action/resolve_march_order/states/source_selected_partial_targets"
local source_selected_all_targets = require "main/ui/phases/action/resolve_march_order/states/source_selected_all_targets"


local _M = {
	state = nil,
	current_order = 1,
	marches_arr = {},
	marches = {},
	count = 0,
	message_to_server = {
		actionType = 'resolveMarchOrder'
	},
	from_name = '',
	last_selected_target_id = nil,
	march_order = nil,
}

function _M:on_map_resolve_order(message)
	self.state.on_map_resolve_order(self, message)
end

function _M:on_march_select_army_ok_button_click(to_send)
	if to_send then
		self.state.on_march_select_army_confirm_army(self, to_send)
	else
		self.state.on_march_select_army_confirm_target(self)
	end
end

function _M:on_hints_goto_button_click()
	self.state.on_hints_goto_button_click(self)
end

function _M:on_hints_goto_button_click_select_target()
	self.state.on_hints_goto_button_click(self)
end


function _M:on_map_target_selected(message)
	march_select_army:set_to(message.name)
	self.last_selected_target_id = message.tile_num
end

function _M:on_partial_march_remove(tile_num)
	self.state.on_partial_march_remove(self, tile_num)
end

function _M:on_hints_next_button_click()
	event_dispatcher.trigger('ws_send', self.march_order.get_message_to_server())
	hints:set_enabled(false)
	partial_march_orders:set_enabled(false)
	march_select_army:close()
end

function _M:init()
	event_dispatcher.on('map_resolve_order', self.on_map_resolve_order, self)
	event_dispatcher.on('march_select_army_ok_button_click', self.on_march_select_army_ok_button_click, self)
	event_dispatcher.on("map_target_selected", self.on_map_target_selected, self)
	event_dispatcher.on('partial_march_remove', self.on_partial_march_remove, self)
	event_dispatcher.on('hints_goto_button_click', self.on_hints_goto_button_click, self)
	event_dispatcher.on('hints_next_button_click', self.on_hints_next_button_click, self)

	self.state = no_source_selected
	no_source_selected.init(self)
	no_source_selected.set_to_next_state(function()
		self.state = source_selected_no_targets
		self.state.init(self)
	end)

	local function to_end_state()
		self.state = source_selected_all_targets
		self.state.init()
	end
	local function to_start_state()
		self.state = no_source_selected
		self.state.init(self)
	end
	source_selected_no_targets.set_to_next_state(function(army_left)
		self.state = source_selected_partial_targets
		self.state.init(army_left)
	end)
	source_selected_no_targets.set_to_end_state(to_end_state)

	source_selected_partial_targets.set_to_end_state(to_end_state)
	source_selected_partial_targets.set_to_start_state(to_start_state)

	source_selected_all_targets.set_to_start_state(to_start_state)
	source_selected_all_targets.set_to_prev_state(function()
		self.state = source_selected_partial_targets
		self.state.init(self.march_order.get_remaining_army())
	end)

	player_panels:set_player_turn(game_data.subPhase.houseType)
end

function _M:set_up_hint()
	if game_data.subPhase.houseType ~= game_data.me then
		return
	end
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
	hints:set_next_button_enabled(false)
	hints:set_goto_count_text(self.count)
	hints:set_hint_text('Select a March order to resolve.')
	hints:set_enabled(true)
end

function _M:clean_up()
	event_dispatcher.off('map_resolve_order', self.on_map_resolve_order)
	event_dispatcher.off('march_select_army_ok_button_click', self.on_march_select_army_ok_button_click)
	event_dispatcher.off('hints_goto_button_click', self.on_hints_goto_button_click)
	event_dispatcher.off('map_target_selected', self.on_map_target_selected)
	event_dispatcher.off('partial_march_remove', self.on_partial_march_remove)
	hints:clean_up()
	march_select_army:close()
	self.from_name = ''
end

return _M
