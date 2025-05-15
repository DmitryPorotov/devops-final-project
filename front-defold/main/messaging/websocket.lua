local game_data = require "main/ui/game_data"
local event_dispatcher = require "main/ui/event_dispatcher"
local mes_proc = require "main/messaging/message_processing"
local events = require "main/ui/events"
local ws_to_use

local _M = {}

local function wrap(message)
	message.lobbyId = game_data.game_id
	if game_data.user_data then
		message.userId = game_data.user_data.id
	end
	if message.player_action and message.action ~= 'fill_with_bots' then
		message.player_action.houseType = game_data.me
		message.type = 'action'
		message.action = 'game_action'
	end
	return message
end

function _M.send(message)
	ws_to_use:send(wrap(message))
end

function _M.on_update(dt)
	ws_to_use:on_update(dt)
end

function _M:init()
	if game_data.is_html5 then
		ws_to_use = require "main/messaging/websocket_web_browser"
	else
		ws_to_use = require "main/messaging/websocket_native"
	end

	event_dispatcher.on(events.ws_send, function(message)
		self.send(message)
	end)
	mes_proc.send = self.send
	local function process_message(message)
		mes_proc:process_message(message)
		event_dispatcher.trigger(events.ws_message, message)
	end
	
	ws_to_use:init(process_message)
end

function _M.connect(creds, callback)
	ws_to_use.on_connected = callback
	ws_to_use:connect(creds)
end

return _M