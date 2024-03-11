local game_data = require "main/ui/game_data"

local _M = {
	raven_choice_prefix_text = 'The owner of the Messanger Raven chose to\n',
	raven_choice_change_order_text = 'change an order for one of the territories.',
	raven_choice_look_at_card_text = 'look at the top card of the Wildlings deck.',
	
	orders__show_orders_on_map = nil,
	player_panel__set_player_ready = nil,
	player_panel__clear_ready_all = nil,
	player_panel__set_player_turn = nil,
	raven_card_or_order__open = nil,
	hints__none_actionable_hint = nil,
}

local switch = {
	addOrder = function(self, reply)
		if reply.player_action.houseType == game_data.me then
			return
		end
		local order = reply.player_action.order ~= json.null and reply.player_action.order or {
			type = "consolidatePower"
		}
		local to_send = {
			[reply.player_action.houseType] = {
				[reply.player_action.tileNumber] = order
			}
		}
		self.orders__show_orders_on_map(to_send,
		reply.player_action.order ~= json.null and true or false)
	end,
	removeOrder = function(self, reply)
		if reply.player_action.houseType == game_data.me then
			return
		end
		msg.post("/map", "remove_order", {tile_num = reply.player_action.tileNumber})
	end,
	openOrders = function(self, reply)
		self.player_panel__set_player_ready(reply.player_action.houseType)
		if reply.player_action.orders then
			self.orders__show_orders_on_map(reply.player_action.orders, true)
			if game_data.me == game_data.tracks["court"][1] then
				self.raven_card_or_order__open()
			end
		end
	end,
	ravenChooseChangeOrderOrLookAtWildlingCard = function(self, reply)
		-- todo: show to everyone whether raven owner chose an order or a card
		local a = 0
		local text = self.raven_choice_prefix_text 
		.. (reply.current_phase.subPhase == 'ravenChangeOrder' and self.raven_choice_change_order_text or self.raven_choice_look_at_card_text)
		self.hints__none_actionable_hint(text)
	end,
}

switch.ravenChangeOrder = function(self, reply)
	switch.addOrder(self, reply)
end

function _M:process(reply)
	if not reply.player_action.actionType then
		print("no action type in reply")
	elseif not switch[reply.player_action.actionType] then
		print("unknown action type " .. reply.player_action.actionType)
	else
		switch[reply.player_action.actionType](self, reply)
		msg.post("/map", "set_phase", {phase = reply.current_phase.subPhase})
		if reply.current_phase.houseType then
			self.player_panel__clear_ready_all()
			self.player_panel__set_player_turn(reply.current_phase.houseType)
		end
	end
end

return _M