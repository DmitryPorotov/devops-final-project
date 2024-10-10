local event_dispatcher = require "main/ui/event_dispatcher"
local events = require "main/ui/events"
local game_data = require "main/ui/game_data"
local leave_power_token = require "main/ui/dialogs/leave_power_token"

local _M = {}

local function on_leave_power_token_click()
	local m = leave_power_token:build_message()
	event_dispatcher.trigger(events.ws_send, m)
end

function _M:init(houseType, tile_num)
	if houseType ~= game_data.me then
		return
	end
	local tile_name = game_data.gameRules.board[tile_num + 1].name
	leave_power_token:set_tile(tile_name, tile_num)
	event_dispatcher.on(events.leave_power_token_click, on_leave_power_token_click)
	leave_power_token:open()
end

function _M:clean_up()
	event_dispatcher.off(events.leave_power_token_click, on_leave_power_token_click)
	leave_power_token:close()
end

return _M
