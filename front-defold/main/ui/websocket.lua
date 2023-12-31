local ws_to_use = require "main/ui/websocket_native"
-- local mes_poc = require "main/messaging/message_processing"

local M = {}

function M.init(self)
	ws_to_use:init()
end

-- function M.set_set_tracks_cb(ctx, callback)
-- 	mes_poc.set_set_tracks_cb(ctx, callback)
-- end

return M