local event_dispatcher = require "main/ui/event_dispatcher"
local utils = require "main/utils"
local power_tokens_logic = require "main/ui/power_tokens_logic"

local _M = {}

function _M:on_player_panel_click(house)
	gui.set_enabled(self.panel, true)
	gui.play_flipbook(self.shield, hash(house))
	gui.set_text(self.house_name, "House " .. utils.HOUSE_REAL_NAMES[house])
	gui.set_text(self.power_token_count, power_tokens_logic.get(house))
end

function _M:init()
	self.panel = gui.get_node("player_screen/backdrop")
	self.shield = gui.get_node("player_screen/shield")
	self.castles_count = gui.get_node("player_screen/castle_count")
	self.power_token_count = gui.get_node("player_screen/token_count")
	self.house_name = gui.get_node('player_screen/house_name')
	event_dispatcher.on('player_panel_click', self.on_player_panel_click, self)
end

function _M:check_pressed(x, y)
	if gui.is_enabled(self.panel) and gui.pick_node(self.panel, x, y) then
		gui.set_enabled(self.panel, false)
		 return true
	end
	return false
end

function _M:check_button_pressed(x, y)
	return false
end

return _M
