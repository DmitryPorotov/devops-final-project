local _M = {
	process_message = nil,
	on_connected = nil,
	login_data = nil
}

function _M:send(message)
	websocket.send(self.connection, json.encode(message))
end

local function log(...)
	local text = ""
	local len = select("#", ...)
	for i=1,len  do
		text = text .. tostring(select(i, ...)) .. (i == len and "" or ", ")
	end

	print(text)
end

local function websocket_callback(self, conn, data)
	self = _M
	if data.event == websocket.EVENT_DISCONNECTED then
		log("Disconnected: " .. tostring(conn) .. " Code: " .. data.code .. " Message: " .. tostring(data.message))
		self.connection = nil
	elseif data.event == websocket.EVENT_CONNECTED then
		log("Connected: " .. tostring(conn))
		self.on_connected(self.login_data)
		-- websocket.send(self.connection, json.encode({
		-- 	type = "chat",
		-- 	userId = 1,
		-- 	messageId = messageId,
		-- 	lobbyId = 2,
		-- 	body = { type = "create" }
		-- }))
	elseif data.event == websocket.EVENT_ERROR then
		log("Error: '" .. tostring(data.message) .. "'")
		if data.handshake_response then
			log("Handshake response status: '" .. tostring(data.handshake_response.status) .. "'")
			for key, value in pairs(data.handshake_response.headers) do
				log("Handshake response header: '" .. key .. ": " .. value .. "'")
			end
			log("Handshake response body: '" .. tostring(data.handshake_response.response) .. "'")
		end
	elseif data.event == websocket.EVENT_MESSAGE then
		log("Receiving: '" .. tostring(data.message) .. "'")
		local msg_ = json.decode(data.message)

		self.process_message(msg_)
	end
end

function _M:init(process_message)
	self.process_message = process_message
end

function _M:on_update(dt)

end

function _M:connect(creds)
	local function handle_response(self_, id, response)
		if response.status == 0 then
			local s = gui.get_node("debug")
			gui.set_text(s, tostring("Could not connect to the server."))
			return
		end

		local data = json.decode(response.response)
		self.login_data = data

		print(self.login_data.token)
		self.url = "ws://127.0.0.1:3001/ws?_token=" .. self.login_data.token
		print(self.url)
		self.connection = websocket.connect(self.url, params, websocket_callback)
	end

	local headers = {
		["Content-Type"] = "application/json"
	}
	local body = json.encode(creds)
	http.request("http://127.0.0.1:3001/api/v1/auth/login", "POST", handle_response, headers, body)
end

return _M