local _M = {}

local subscriptions = {}

--- Subscribe to an event
---@param event string Event name
---@param callback function Event handler
---@param this self Optional self to be passed to the callback
function _M.on(event, callback, this)
	if not subscriptions[event] then
		subscriptions[event] = {}
	end
	subscriptions[event][callback] = { t = this }
end

--- Unsubscribe from event
---@param event string Event name
---@param callback function UNUSED. A callback used to subscribe to this event. Will be used if I switch to multiple handlers for the same event
function _M.off(event, callback)
	if not subscriptions[event] then
		return
	end
	subscriptions[event][callback] = nil
	if not next(subscriptions[event]) then
		subscriptions[event] = nil
	end
end

--- Trigger an event
---@param event string Event name
function _M.trigger(event, ...)
	if subscriptions[event] then
		for k, v in pairs(subscriptions[event]) do
			if v.t then
				k(v.t, ...)
			else
				k(...)
			end
		end
	end
end

return _M
