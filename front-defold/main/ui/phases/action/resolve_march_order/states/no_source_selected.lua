local game_data = require "main/ui/game_data"
local march_order_ctr = require "main/ui/phases/action/resolve_march_order/march_order"
local march_select_army = require "main/ui/dialogs/march_select_army"
local army_logic = require "main/ui/army_logic"
local utils = require "main/utils"
local common = require "main/ui/phases/action/resolve_march_order/states/common"
local confirm = require "main/ui/dialogs/confirm"
local event_dispatcher = require "main/ui/event_dispatcher"

local to_next_state

local function set_to_next_state(cb)
	to_next_state = cb
end

local function on_map_resolve_order(self, message)
	local army = game_data.armies[tostring(message.tile_num)]
	self.march_order = march_order_ctr(message.tile_num, army_logic.to_gui_format(army))
	march_select_army:set_from(message.name)
	march_select_army:open_or_toggle(self.march_order.get_remaining_army(), message.label)
	self.from_name = message.name
end

local function on_hints_goto_button_click(self)
	msg.post("/map", "move_camera_to_label", {tile_num = self.marches_arr[self.current_order]})
	if self.current_order >= self.count then
		self.current_order = 1
	else
		self.current_order = self.current_order + 1
	end
end

local function on_march_select_army_confirm_army(self, to_send)
	if not next(to_send) then
		confirm:open('Do you want to remove the March order\nfrom "'
				.. self.from_name .. '" and finish your turn?',
				function(result)
					if result then
						event_dispatcher.trigger('hints_next_button_click')
					else
						msg.post('/map', 'unselect_label')
						msg.post('/map', 'hide_targets')
						msg.post('/map', 'set_order_source_tile', {})
						march_select_army:close()
					end
				end)
		return
	else
		common.on_march_select_army_confirm_army(self)
		to_next_state()
	end
end

local function init(self)
	self:set_up_hint()
	march_select_army:close()
	msg.post('/map', 'hide_targets')
	msg.post('/map', 'set_order_source_tile', {})
	msg.post('/map', 'unselect_label')
end

return {
	init = init,
	on_map_resolve_order = on_map_resolve_order,
	on_march_select_army_confirm_army = on_march_select_army_confirm_army,
	on_march_select_army_confirm_target = utils.unimplemented,
	on_hints_goto_button_click = on_hints_goto_button_click,
	on_map_target_selected = utils.unimplemented,
	on_partial_march_remove = utils.unimplemented,

	set_to_next_state = set_to_next_state
}