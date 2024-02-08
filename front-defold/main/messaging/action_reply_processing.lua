local game_data = require "main/ui/game_data"
local player_panel = require "main/ui/player_panel"

local _M = {
	orders__show_orders_on_map = nil,
	switch = {
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
			player_panel:set_player_ready(reply.player_action.houseType)
		end,
	}
}

function _M:process(reply)
	if not reply.player_action.actionType then
		print("no action type in reply")
	elseif not self.switch[reply.player_action.actionType] then
		print("unknown action type " .. reply.player_action.actionType)
	else
		self.switch[reply.player_action.actionType](self, reply)
	end
end

return _M