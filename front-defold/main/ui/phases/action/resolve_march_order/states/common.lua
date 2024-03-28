local hints = require "main/ui/hints"
local march_select_army = require "main/ui/dialogs/march_select_army"
local partial_march_orders = require "main/ui/partial_march_orders"

local function on_march_select_army_confirm_army(self, to_send)
	msg.post('/map', 'show_targets', {
		targets = self.march_order.get_possible_targets(to_send),
	})
	msg.post('/map', 'set_order_source_tile', {
		from_tile_num = self.march_order.get_source_tile_num(),
	})
	self.count = #self.march_order.get_possible_targets(to_send)
	self.current_order = 1
	hints:set_goto_count_text(self.count)
	hints:set_goto_button_enabled(true)
	hints:set_next_button_enabled(false)
	hints:set_hint_text('Select a target territory.')
end

local function createMarchOrderTarget(self)
	self.march_order.add_partial_order(self.last_selected_target_id, march_select_army:get_to_send())
	partial_march_orders:add_order(self.last_selected_target_id, march_select_army:get_order_text())
end

local function on_march_select_army_confirm_target(self, cb)
	createMarchOrderTarget(self)

	local army_left = self.march_order.get_remaining_army()
	cb(army_left)

	hints:set_goto_button_enabled(false)
	hints:set_next_button_enabled(true)
end

local function on_partial_march_remove(self, tile_num)
	if not self.march_order.delete_partial_order(tile_num) then
		msg.post('/map', 'hide_targets')
		msg.post('/map', 'set_order_source_tile', {})
		msg.post('/map', 'unselect_label')
		march_select_army:close()
	end
end

local function on_hints_goto_button_click(self)
	msg.post("/map", "move_camera_to_label", {tile_num = self.march_order.get_possible_targets()[self.current_order]})
	if self.current_order >= self.count then
		self.current_order = 1
	else
		self.current_order = self.current_order + 1
	end
end

return {
	on_march_select_army_confirm_army = on_march_select_army_confirm_army,
	on_march_select_army_confirm_target = on_march_select_army_confirm_target,
	on_hints_goto_button_click = on_hints_goto_button_click,
	on_partial_march_remove = on_partial_march_remove,
}