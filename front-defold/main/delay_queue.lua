local _M = {
	lastTimer = nil
}

function _M:add_item(delay, callback)
	if self.lastTimer then
		local info = timer.get_info(self.lastTimer)
		if info then
			delay = delay + info.time_remaining
		end
	end
	self.lastTimer = timer.delay(delay, false, callback)
end

return _M