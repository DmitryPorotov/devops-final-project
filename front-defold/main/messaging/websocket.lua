local game_data = require "main/ui/game_data"
local mes_proc = require "main/messaging/message_processing"
local ws_to_use = require "main/messaging/websocket_native"
-- local mes_poc = require "main/messaging/message_processing"

local _M = {}

local function wrap(message)
	message.lobbyId = game_data.game_id
	if game_data.user_data then
		message.userId = game_data.user_data.id
	end
	return message
end

function _M.send(message)
	ws_to_use:send(wrap(message))
end

function _M.init(self)
	mes_proc.send = self.send
	local function process_message(message)
		mes_proc:process_message(message)
	end
	
	ws_to_use:init(process_message)
end

function _M.connect(creds, callback)
	ws_to_use.on_connected = callback
	ws_to_use:connect(creds)
end

return _M