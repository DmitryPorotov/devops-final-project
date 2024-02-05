local game_data = require "main/ui/game_data"
local player_panel = require "main/ui/player_panel"

local _M = {
	switch = {
		addOrder = function(reply)
		end,
		openOrders = function(reply)
			
		end,
	}
}

function _M:process(reply)
	if not reply.player_action.actionType then
		print("no action type in reply")
	elseif not self.switch[reply.player_action.actionType] then
		print("unknown action type " .. reply.player_action.actionType)
	else
		self.switch[reply.player_action.actionType](reply)
	end
end

return _M