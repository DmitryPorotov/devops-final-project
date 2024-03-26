local utils = require "main/utils"
local march_select_army = require "main/ui/dialogs/march_select_army"
local common = require "main/ui/phases/action/resolve_march_order/states/common"
local hints = require "main/ui/hints"

local to_start_state, to_end_state, army_selected

local function set_to_start_state(cb)
	to_start_state = cb
end

local function set_to_end_state(cb)
	to_end_state = cb
end

local function init(army_left)
	hints:set_enabled(true)
	hints:set_next_button_enabled(true)
	hints:set_goto_button_enabled(false)
	march_select_army:open_or_toggle(army_left, nil)
	msg.post('/map', 'hide_targets')
	hints:set_hint_text('Send another army or confirm March Order')
	army_selected = false
end

local function on_partial_march_remove(self, tile_num)
	if self.march_order.delete_partial_order(tile_num) then
		to_start_state()
	else
		init(self.march_order.get_remaining_army())
	end
end

local function on_march_select_army_confirm_target(self)
	common.on_march_select_army_confirm_target(self, function(army_left)
		if next(army_left) then
			init(army_left)
		else
			to_end_state()
		end
	end)
end

local function on_march_select_army_confirm_army(self)
	print('in partial targets')
	common.on_march_select_army_confirm_army(self)
	army_selected = true
end

local  function on_hints_goto_button_click(self)
	if army_selected then
		common.on_hints_goto_button_click(self)
	end
end

return {
	init = init,
	on_map_resolve_order = utils.noop,
	on_march_select_army_confirm_army = on_march_select_army_confirm_army,
	on_march_select_army_confirm_target = on_march_select_army_confirm_target,
	on_hints_goto_button_click = on_hints_goto_button_click,
	on_map_target_selected = utils.noop,
	on_partial_march_remove = on_partial_march_remove,

	set_to_start_state = set_to_start_state,
	set_to_end_state = set_to_end_state,
}