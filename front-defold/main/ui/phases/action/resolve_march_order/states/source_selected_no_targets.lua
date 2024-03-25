local utils = require "main/utils"
local common = require "main/ui/phases/action/resolve_march_order/states/common"

local to_next_state, to_end_state

local function set_to_next_state(cb)
	to_next_state = cb
end

local function set_to_end_state(cb)
	to_end_state = cb
end

local function on_march_select_army_confirm_target(self)
	common.on_march_select_army_confirm_target(self, function(army_left)
		if next(army_left) then
			--self.state = source_selected_partial_targets
			--self.state.init(self, army_left)
			to_next_state(army_left)
		else
			to_end_state()
			--self.state = source_selected_all_targets
			--self.state.init(self)
		end
	end)
end


return {
	init = utils.noop,
	on_map_resolve_order = utils.noop,
	on_march_select_army_confirm_army = common.on_march_select_army_confirm_army,
	on_march_select_army_confirm_target = on_march_select_army_confirm_target,
	on_hints_goto_button_click = common.on_hints_goto_button_click,
	on_map_target_selected = utils.noop,
	on_partial_march_remove = utils.noop,

	set_to_next_state = set_to_next_state,
	set_to_end_state = set_to_end_state,
}