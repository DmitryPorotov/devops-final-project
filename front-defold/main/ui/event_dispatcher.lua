local _M = {}

local subscriptions = {}
-- note: do I need multiple handlers for the same event?

--- Subscribe to an event
---@param event string Event name
---@param callback function Event handler
---@param this self Optional self to be passed to the callback
function _M.on(event, callback, this)
	subscriptions[event] = { c = callback, t = this }
end

--- Unsubscribe from event
---@param event string Event name
---@param callback function UNUSED. A callback used to subscribe to this event. Will be used if I switch to multiple handlers for the same event
function _M.off(event, callback)
	subscriptions[event] = nil
end

--- Trigger an event
---@param event string Event name
function _M.trigger(event, ...)
	if subscriptions[event] then
		if subscriptions[event].t then
			subscriptions[event].c(subscriptions[event].t, ...)
		else
			subscriptions[event].c(...)
		end
	end
end

return _M