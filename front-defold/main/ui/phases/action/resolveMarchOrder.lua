local event_dispatcher = require "main/ui/event_dispatcher"
local game_data = require "main/ui/game_data"
local march_select_army = require "main/ui/dialogs/march_select_army"
local hints = require "main/ui/hints"
local player_panels = require "main/ui/player_panel"

local _M = {
	current_order = 1,
	marches_arr = {},
	marches = {},
	count = 0,
	current_tile_num = false,
	message_to_server = {}
}

function _M:init()
	event_dispatcher.on('map_resolve_order', function(message)
		local army = game_data.armies[tostring(message.tile_num)]
		self.current_tile_num = message.tile_num
		march_select_army:open(army, message.label)
	end)
	event_dispatcher.on('march_select_army_ok_button_click', function(to_send)
		self.message_to_server.sourceTileNumber = tonumber(self.current_tile_num)
		self.to_send = to_send
	end)
	if game_data.subPhase.houseType == game_data.me then
		self:set_up_hint()
	end
	player_panels:set_player_turn(game_data.subPhase.houseType)
end

function _M:set_up_hint()
	self.marches = {}
	self.marches_arr = {}
	self.count = 0
	for i, v in pairs(game_data.placedOrders[game_data.me]) do
		if v.type == 'march' then
			self.marches[i] = v
			table.insert(self.marches_arr, i)
			self.count = self.count + 1
		end
	end
	hints:set_goto_button_enabled(true)
	hints:set_goto_count_text(self.count)
	hints:set_hint_text('Select a March order to resolve.')
	hints.on_goto_button_pressed = function()
		msg.post("/map", "move_camera_to_label", {tile_num = self.marches_arr[self.current_order]})
		if self.current_order >= self.count then
			self.current_order = 1
		else
			self.current_order = self.current_order + 1
		end
	end
	hints:set_hints_enabled(true)
end

function _M:clean_up()
	event_dispatcher.off('map_resolve_order')
	event_dispatcher.off('march_select_army_ok_button_click')
end

return _M
