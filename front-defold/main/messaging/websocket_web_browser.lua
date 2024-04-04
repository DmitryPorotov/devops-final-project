local game_data = require "main/ui/game_data"

local _M = {
	process_message = nil,
	time_since_last_check = 0,
	on_connected = nil,
}

function _M:init(process_message)
	self.process_message = process_message
	game_data.game_id = tonumber(html5.run("window.workerComm.init()"))
end

function _M:on_update(dt)
	self.time_since_last_check = self.time_since_last_check + dt
	if self.time_since_last_check >= 0.1 then
		self.time_since_last_check = 0
		local reply = html5.run("window.workerComm.getNewMessages()")
		if reply then
			local msgs = json.decode(reply)
			for _, v in ipairs(msgs) do
				self.process_message(v)
			end
		end
	end
end

function _M:send(message)
	html5.run("window.workerComm.send('" .. json.encode(message) .. "')")
end

function _M:connect(creds)
	local data = html5.run("window.localStorage.getItem('_user')")
	local me = html5.run("window.localStorage.getItem('_lobby".. game_data.game_id .."house')")
	game_data.user_data = json.decode(data)
	game_data.me = me
	self:send({
		type = "action",
		action = "join_game",
		joinAs = game_data.me,
		name = game_data.user_data.name,
	})
	self.on_connected(json.decode(data))
end

return _M
