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

function _M:dispose()
	pcall(function() gui.delete_node(self.back_drop) end)
end

function _M:init()
	self.buttons = {
		kraken = gui.get_node("login/kraken"),
		lion = gui.get_node("login/lion"),
		moose = gui.get_node("login/moose"),
		pufferfish = gui.get_node("login/pufferfish"),
		rose = gui.get_node("login/rose"),
		wolf = gui.get_node("login/wolf"),
		create_game = gui.get_node("login/create_game")
	}
	self.back_drop = gui.get_node("login/back_drop")
end

function _M:on_input(action)
	if not self.disabled then
		if action.pressed then
			for k, v in pairs(self.buttons) do
				if gui.pick_node(v, action.x, action.y) then
					local function cb(data)
						game_data.user_data = data
						tracks:set_players(game_data.players)
						if k == "create_game" then
							ws.send({
								type = "chat",
								body = { type = "create" }
							})
							game_data.creating_new_game = true
						else
							ws.send({
								type = "chat",
								body = { type = "join" }
							})
						end
					end
					local house = k == "create_game" and "kraken" or k
					game_data.me = house
					tracks:set_me(house)
					ws.connect(self.login_creds[house], cb)
					
					self.disabled = true
					return true
				end
			end
		end
		if gui.pick_node(self.back_drop, action.x, action.y) then
			return true
		end
	end
end

return _M