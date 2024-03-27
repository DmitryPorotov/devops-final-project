local utils = require "main/utils"
local hints = require "main/ui/hints"
local march_select_army = require "main/ui/dialogs/march_select_army"

local to_prev_state, to_start_state

local function set_to_prev_state(cb)
	to_prev_state = cb
end

local function set_to_start_state(cb)
	to_start_state = cb
end

local function init()
	hints:set_enabled(true)
	hints:set_next_button_enabled(true)
	hints:set_goto_button_enabled(false)
	hints:set_hint_text('Confirm order.')
	msg.post('/map', 'hide_targets')
	march_select_army:close()
end

local function on_partial_march_remove(self, tile_num)
	if self.march_order.delete_partial_order(tile_num) then
		to_start_state()
	else
		to_prev_state()
	end
end

return {
	init = init,
	on_map_resolve_order = utils.unimplemented,
	on_march_select_army_confirm_army = utils.unimplemented,
	on_march_select_army_confirm_target = utils.unimplemented,
	on_hints_goto_button_click = utils.unimplemented,
	on_map_target_selected = utils.unimplemented,
	on_partial_march_remove = on_partial_march_remove,

	set_to_prev_state = set_to_prev_state,
	set_to_start_state = set_to_start_state,
}