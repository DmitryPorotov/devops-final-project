-- Put functions in this file to use them in several other scripts.
-- To get access to the functions, you need to put:
-- require "my_directory.my_file"
-- in any script using the functions.
local _M = {}

function _M.process_message(self, message)
	if message.action == "game_action" then

	elseif message.action == "get_rules" then

	elseif message.action == "create_game" then
		self.set_tracks(self.tracks_gui, message.gameState.tracks)
		self.set_game_state(self.top_panel, message.gameState)
		for i, v in pairs(message.gameState.armies) do
			self.set_tile_units(i, v)
		end
	end
end

function _M.set_tile_units(tile_num, units)
	msg.post("/map", "set_units", {
		tile_num = tile_num,
		units = units
	})
end

function _M.set_set_tracks_cb(ctx, callback)
	_M.tracks_gui = ctx
	_M.set_tracks = callback
end

function _M.set_set_game_state_cb(ctx, callback)
	_M.top_panel = ctx
	_M.set_game_state = callback
end


return _M
