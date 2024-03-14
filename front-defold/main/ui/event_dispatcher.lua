local _M = {}

local subscriptions = {}
-- note: do I need multiple handlers for the same event?

--- Subscribe to an event
---@param event string Event name
---@param callback function Event handler
function _M.on(event, callback)
	subscriptions[event] = callback
end

--- Unsubscribe from event
---@param event string Event name
function _M.off(event)
	subscriptions[event] = nil
end

--- Trigger an event
---@param event string Event name
function _M.trigger(event, ...)
	if subscriptions[event] then
		subscriptions[event](...)
	end
end

return _M