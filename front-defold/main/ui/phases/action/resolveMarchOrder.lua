local event_dispatcher = require "main/ui/event_dispatcher"

local _M = {}

function _M:init()
	event_dispatcher.on('map_resolve_order', function(message) 
		
	end)
end

function _M:clean_up()
	event_dispatcher.off('map_resolve_order')
end

return _M
