local mes_proc = require "main/messaging/message_processing"

local M = {}

local function log(...)
	local text = ""
	local len = select("#", ...)
	for i=1,len  do
		text = text .. tostring(select(i, ...)) .. (i == len and "" or ", ")
	end

	print(text)
end

local function websocket_callback(self, conn, data)
	self = M
	if data.event == websocket.EVENT_DISCONNECTED then
		log("Disconnected: " .. tostring(conn) .. " Code: " .. data.code .. " Message: " .. tostring(data.message))
		self.connection = nil
	elseif data.event == websocket.EVENT_CONNECTED then
		log("Connected: " .. tostring(conn))
		websocket.send(self.connection, json.encode({
			type = "chat",
			userId = 1,
			messageId = messageId,
			lobbyId = 2,
			body = { type = "create" }
		}))
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
		local msg = json.decode(data.message)
		if msg.type == "chat" then
			if msg.body.type == "create" then
				websocket.send(self.connection, json.encode(
						{
							type = 'action',
							lobbyId = 2,
							userId = 1,
							action = 'create_game',
							isRandomHouses = false
						}
				))
			end
		else
			mes_proc:process_message(msg)
		end
	end
end

function M.init(self)
	local function handle_response(self_, id, response)
		local data = json.decode(response.response)
		self.token = data.token

		print(self.token)
		self.url = "ws://127.0.0.1:3001/?_token=" .. self.token
		print(self.url)
		self.connection = websocket.connect(self.url, params, websocket_callback)
	end

	local headers = {
		["Content-Type"] = "application/json"
	}
	local body = json.encode({email = 'a@b.com', password = "12345678"})
	http.request("http://127.0.0.1:3001/auth/login", "POST", handle_response, headers, body)
end

return M