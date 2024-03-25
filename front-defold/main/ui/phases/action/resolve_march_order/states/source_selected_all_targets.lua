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
	hints:set_hints_enabled(true)
	hints:set_next_button_enabled(true)
	hints:set_goto_button_enabled(false)
	hints:set_hint_text('Confirm order.')
	march_select_army:close()
end

local function on_partial_march_remove(self, tile_num)
	if self.march_order.delete_partial_order(tile_num) then
		--self.state = no_source_selected
		--self.state.init(self)
		to_start_state()
	else
		--self.state = source_selected_partial_targets
		--self.state.init(self, self.march_order.get_remaining_army())
		to_prev_state()
	end
end

return {
	init = init,
	on_map_resolve_order = utils.noop,
	on_march_select_army_confirm_army = utils.noop,
	on_march_select_army_confirm_target = utils.noop,
	on_hints_goto_button_click = utils.noop,
	on_map_target_selected = utils.noop,
	on_partial_march_remove = on_partial_march_remove,

	set_to_prev_state = set_to_prev_state,
	set_to_start_state = set_to_start_state,
}