local ws_to_use = require "main/ui/websocket_native"

local M = {}

function M.init(self)
	ws_to_use:init()
end

return M