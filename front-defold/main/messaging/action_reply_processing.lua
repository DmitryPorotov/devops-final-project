local game_data = require "main/ui/game_data"

local _M = {
	orders__show_orders_on_map = nil,
	player_panel__set_player_ready = nil,
	player_panel__clear_ready_all = nil,
	player_panel__set_player_turn = nil,
	raven_card_or_order__open = nil,
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
			self.player_panel__clear_ready_all()
			self.player_panel__set_player_turn(game_data.tracks["court"][1])
			if game_data.me == game_data.tracks["court"][1] then
				self.raven_card_or_order__open()
			end
		end
	end,
	ravenChooseChangeOrderOrLookAtWildlingCard = function(self, reply)
		if reply.player_action.ravenChoice == 'changeOrder' 
		and reply.player_action.houseType == game_data.me then
			msg.post("/map", "set_phase", {phase = 'ravenChangeOrder'})
		end
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
	end
end

return _M