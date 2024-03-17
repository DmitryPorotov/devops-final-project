local game_data = require "main/ui/game_data"
local utils = require "main/utils"

local tracks = require "main/ui/tracks"
local top_panel = require "main/ui/top_panel"
local orders = require "main/ui/orders"
local supply_panel = require "main/ui/supply_panel"
local hints = require "main/ui/hints"

local _M = {}

local function set_tile_units(tile_num, units)
	msg.post("/map", "set_units", {
		tile_num = tile_num,
		units = units
	})
end

local function clean_up()
	hints:clean_up()
end

function _M:init(message)
	game_data.gameRules = message.gameRules
	for k, v in pairs(message.gameState) do
		game_data[k] = v
	end

	supply_panel:set_supply_usage_rules(message.gameRules.supplyUsage)
	supply_panel:set_available(message.gameState.supplies[game_data.me])
	supply_panel:set_current(message.gameState.supplies[game_data.me])
	local my_armies = utils.filter_my_armies(message.gameState.armies, game_data.me)
	supply_panel:update_usage(my_armies)
	orders:calc_stars_available(message.gameState.tracks.court)
	orders:show_orders_on_map(message.gameState.placedOrders, "addOrder" ~= message.gameState.subPhase.subPhase)
	tracks:set_players(game_data.players)
	tracks:set_tracks(message.gameState.tracks)
	top_panel:set_game_state(message.gameState)
	local phase_to_map_msg = {
		phase = message.gameState.subPhase.subPhase,
	}
	if message.gameState.subPhase.houseType then
		phase_to_map_msg.house = message.gameState.subPhase.houseType
	end
	msg.post("/map", "set_phase", phase_to_map_msg)
	for i, v in pairs(message.gameState.armies) do
		set_tile_units(i, v)
	end

	pcall(function() gui.delete_node(gui.get_node("login/back_drop")) end)

	msg.post("/camera", "take_focus")
	msg.post("/map", "move_camera_to_house", {house = game_data.me})

	clean_up()
end

return _M