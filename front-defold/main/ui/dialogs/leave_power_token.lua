local event_dispatcher = require "main/ui/event_dispatcher"
local base = require "main/ui/dialogs/base_dialog"

---@module LeavePowerToken : BaseDialog
local _M = {
	subtitle_text = 'Do you want to spend 1 power\ntoken to keep control of '
}

setmetatable(_M, base)

function _M:init()
	self.panel = gui.get_node("leave_power_token/panel")
	self.yes_button = gui.get_node("leave_power_token/spend")
	self.no_button = gui.get_node("leave_power_token/dont_spend")
	self.subtitle = gui.get_node("leave_power_token/subtitle")
end

local selected

function _M:on_closed()
	selected = nil
end

function _M:set_tile(name, tile_num)
	self.tile_num = tile_num
	gui.set_text(self.subtitle, self.subtitle_text .. name)
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.panel) and selected == nil then
		if gui.pick_node(self.yes_button, x, y) then
			selected = true
			event_dispatcher.trigger('leave_power_token_click')
			return true
		elseif gui.pick_node(self.no_button, x, y) then
			selected = false
			event_dispatcher.trigger('leave_power_token_click')
			return true
		end
	end
	return false
end

function _M:build_message()
	local m = {
		player_action = {
			actionType = 'leavePowerTokenAtTile',
			doLeave = selected,
			tileNumber = self.tile_num
		}
	}
	return m
end

return _M
