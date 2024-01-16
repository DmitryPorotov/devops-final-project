local mes_proc = require "main/messaging/message_processing"
local ws_to_use = require "main/ui/websocket_native"
-- local mes_poc = require "main/messaging/message_processing"

local _M = {}

local function wrap(message)
	return message
end

function _M.send(self, message)
	ws_to_use:send(wrap(message))
end

function _M.init(self)
	local function process_message(message)
		mes_proc:process_message(message)
	end
	
	ws_to_use:init(process_message)
end

-- function M.set_set_tracks_cb(ctx, callback)
-- 	mes_poc.set_set_tracks_cb(ctx, callback)
-- end

return _M