local ws = require "main/messaging/websocket"
local tracks = require "main/ui/tracks"
local game_data = require "main/ui/game_data"

local _M = {
	login_creds = {
		kraken = {email = 'a@b.com', password = "12345678"},
		lion = {email = 'b@b.com', password = "12345678"},
		moose = {email = 'admin@b.com', password = "12345678"},
		pufferfish = {email = 'c@b.com', password = "12345678"},
		rose = {email = 'd@b.com', password = "12345678"},
		wolf = {email = 'e@b.com', password = "12345678"},
	},

}

function _M:init()
	-- Add initialization code here
	-- Learn more: https://defold.com/manuals/script/
	-- Remove this function if not needed
	self.buttons = {
		kraken = gui.get_node("login/kraken"),
		lion = gui.get_node("login/lion"),
		moose = gui.get_node("login/moose"),
		pufferfish = gui.get_node("login/pufferfish"),
		rose = gui.get_node("login/rose"),
		wolf = gui.get_node("login/wolf"),
	}
	self.back_drop = gui.get_node("login/back_drop")
end

function _M:on_input(action_id, action)
	if not self.disabled and action_id == hash("touch") then
		if action.pressed then
			for k, v in pairs(self.buttons) do
				if gui.pick_node(v, action.x, action.y) then
					local function cb(data)
						game_data.user_data = data
						-- game_data.players[k] = { id = data.id, name = data.name }
						tracks:set_players(game_data.players)
						if k == "kraken" then
							ws.send({
								type = "chat",
								userId = data.id,
								lobbyId = game_data.game_id,
								body = { type = "create" }
							})
						else
							ws.send({
								type = "chat",
								userId = data.id,
								lobbyId = game_data.game_id,
								body = { type = "join" }
							})
						end
					end
					game_data.me = k
					tracks:set_me(k)
					ws.connect(self.login_creds[k], cb)
					gui.set_enabled(gui.get_node("login/back_drop"), false)
					self.disabled = true
				end
			end
		end
		if gui.pick_node(self.back_drop, action.x, action.y) then
			return true
		end
	end
end

return _M