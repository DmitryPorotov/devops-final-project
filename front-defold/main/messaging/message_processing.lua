local game_data = require "main/ui/game_data"
local supply_panel = require "main/ui/supply_panel"
local action_proc = require "main/ui/messaging/action_reply_processing"

local _M = {
	send = nil,
}

local function update_players(old, new)
	for _, v in ipairs(new) do
		old[v.house] = {id = v.id , name = v.name}
	end
end

local function filter_my_armies(armies)
	local my = {}
	for i, v in pairs(armies) do
		if v[1].house == game_data.me then
			my[i] = v
		end
	end
	return my
end

function _M:process_message(message)
	if message.type == 'action' then
		if message.action == "game_action" then
			action_proc:process(message.reply)
		elseif message.action == "join_game" then
			update_players(game_data.players, message.gameSettings.players)
			self.send({
				type = "action",
				action = "get_game_state",
			})
		elseif message.action == "get_game_state" then
			game_data.gameRules = message.gameRules
			-- TODO encapsulate supply_panel
			supply_panel:set_supply_usage_rules(message.gameRules.supplyUsage)
			supply_panel:set_available(message.gameState.supplies[game_data.me])
			supply_panel:set_current(message.gameState.supplies[game_data.me])
			local my_armies = filter_my_armies(message.gameState.armies)
			supply_panel:update_usage(my_armies)
			self.hints__set_addOrder_hints(my_armies, message.gameState.placedOrders[game_data.me] or {})

			self.orders__calc_stars_available(message.gameState.tracks.court)
			self.orders__show_orders_on_map(message.gameState.placedOrders)
			self.tracks__set_players(game_data.players)
			self.tracks__set_tracks(message.gameState.tracks)
			self.top_panel__set_game_state(message.gameState)
			for i, v in pairs(message.gameState.armies) do
				self.set_tile_units(i, v)
			end
			gui.delete_node(gui.get_node("login/back_drop"))
			msg.post("/camera", "take_focus")
			msg.post("/map", "move_camera_to_house", {house = game_data.me})
		elseif message.action == "create_game" and game_data.me == "kraken" then
			self.send({
				type = "action",
				action = "join_game",
				joinAs = game_data.me,
				name = game_data.user_data.name
			})
		end
	elseif message.type == 'chat' then
		if message.body.type == 'create' then
			self.send({
				type = "chat",
				body = {
					type = "join"
				}
			})
		elseif message.body.type == 'join' then
			if game_data.creating_new_game then
				self.send({
					type = "action",
					action = "create_game",
					isRandomHouses = false,
				})
			else
				self.send({
					type = "action",
					action = "join_game",
					joinAs = game_data.me,
					name = game_data.user_data.name
				})
			end

		end
	else
		print(message.message)
		local s = gui.get_node("debug")
		gui.set_text(s, message.message)
	end
end

function _M.set_tile_units(tile_num, units)
	msg.post("/map", "set_units", {
		tile_num = tile_num,
		units = units
	})
end

function _M:set_set_tracks_cb(callback)
	self.tracks__set_tracks = callback
end

function _M:set_set_game_state_cb(callback)
	self.top_panel__set_game_state = callback
end

function _M:set_set_players_cb(callback)
	self.tracks__set_players = callback
end

function _M:set_calc_stars_available_cb(callback)
	self.orders__calc_stars_available = callback
end

function _M:set_show_orders_on_map_cb(callback)
	self.orders__show_orders_on_map = callback
end

function _M:set_set_addOrder_hints_cb(callback)
	self.hints__set_addOrder_hints = callback
end

return _M
