local event_dispatcher = require "main/ui/event_dispatcher"
local events = require "main/ui/events"
local game_data = require "main/ui/game_data"

local _M = {
	all_game_events = {},
	offset = 0
}

function _M:init()
	self.close_button = gui.get_node("log/close")
	self.log_window = gui.get_node("log/back_drop")
	self.up_button = gui.get_node('log/up_button')
	self.down_button = gui.get_node('log/down_button')
	self.logs = {
		gui.get_node("log/entry"),
		gui.get_node("log/entry1"),
		gui.get_node("log/entry2"),
		gui.get_node("log/entry3"),
		gui.get_node("log/entry4"),
	}
	event_dispatcher.on(events.ws_message, function(self, message)
		self:add_entry(message)
		-- note: this will redraw text even if log is not shown, todo optimize
		self:display_entries(self.offset)
	end, _M)
end

function _M:add_entry(entry) 
	if entry.type and entry.type == 'action' and entry.action == 'game_action' and entry.reply then
		local msg = ''
		for i = #entry.reply, 1, -1 do
			for key, value in pairs(entry.reply[i].player_action) do
				msg = msg .. self:transform_reply_field(key, value)
			end
			-- msg = msg .. 'Action: ' ..  entry.reply[i].player_action.actionType .. ' House: ' ..  entry.reply[i].player_action.houseType 
			-- ..
			-- ' Territory: ' .. entry.reply[i].player_action.tileNumber .. ' - ' .. game_data.gameRules.board[entry.reply[i].player_action.tileNumber + 1].name
		end
		table.insert(self.all_game_events, msg)
	end
end

function _M:transform_reply_field(k, v)
	if k == 'actionType' then
		return ' Action: ' .. v .. ';'
	elseif k == 'houseType' then
		return ' House: ' .. v .. ';'
	elseif k == 'tileNumber' then
		return ' Territory: ' .. v .. ' - ' .. game_data.gameRules.board[v + 1].name .. ';'
	elseif k == 'sourceTileNumber' then
		return ' From territory: ' .. v .. ' - ' .. game_data.gameRules.board[v + 1].name .. ';'
	elseif k == 'targets' then
		local retval = ''
		for key, value in pairs(v) do
			retval = retval .. ' to territory ' .. key .. ' - ' .. game_data.gameRules.board[tonumber(key) + 1].name .. ' unit(s): '
			for _, val in ipairs(value) do
				retval = retval .. val.type
			end
		end
		return retval  .. ';'
	else
		pprint(k)
		return ' ' .. k .. ': ' .. tostring(v)
	end
end

function _M:display_entries(offset)
	if not offset then
		offset = 0
	end
	for i = 1, #self.logs, 1 do
		if self.all_game_events[#self.all_game_events - offset - (i - 1)] then
			gui.set_text(self.logs[i], tostring(#self.all_game_events - offset - (i - 1)) .. ' - ' .. self.all_game_events[#self.all_game_events - offset - (i - 1)])
		end
	end
end

function _M:check_button_pressed(x, y)
	if gui.is_enabled(self.log_window) then
		if gui.pick_node(self.close_button, x, y) then
			gui.set_enabled(self.log_window, false)
			msg.post('/camera', 'take_focus')
			return true
		elseif gui.pick_node(self.down_button, x, y) then
			self.offset = self.offset + #self.logs
			if self.offset > #self.all_game_events - #self.logs then
				self.offset = #self.all_game_events - #self.logs
			end
			self:display_entries(self.offset)
			return true
		elseif gui.pick_node(self.up_button, x, y) then
			self.offset = self.offset - #self.logs
			if self.offset < 0 then
				self.offset = 0
			end
			self:display_entries(self.offset)
			return true
		end
	end
	return false
end

function _M:check_pressed(x, y)
	if gui.is_enabled(self.log_window) and gui.pick_node(self.log_window, x, y) then
		return true
	end
	return false
end

return _M