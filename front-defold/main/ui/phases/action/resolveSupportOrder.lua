local event_dispatcher = require "main/ui/event_dispatcher"
local events = require "main/ui/events"
local player_panels = require "main/ui/player_panel"
local game_data = require "main/ui/game_data"

---@module ResolveSupportOrder
local _M = {
	attacker_house,
	defender_house,
	tile_numbers
}
---@param house string
---@param tile_numbers number[]
---@param attacker_house string
---@param defender_house string
function _M:init(house, tile_numbers, attacker_house, defender_house)
	player_panels:set_player_turn(house)
	if house ~= game_data.me then
		return
	end
	self.attacker_house = attacker_house
	self.defender_house = defender_house
	self.tile_numbers = tile_numbers
	event_dispatcher.on(events.support_order_send_click, self.on_support_order_send_click, self)
end

function _M:on_support_order_send_click(support_who)

	event_dispatcher.trigger(events.ws_send, self:build_payload(support_who))
end

function _M:build_payload(support_who)
	local msg = {
		player_action = {
			actionType = "resolveSupportOrder",
			fromHouseType = game_data.me,
			tileNumbers = self.tile_numbers,
		}
	}
	if support_who == 'attacker' then
		msg.player_action.toHouseType = self.attacker_house
	elseif support_who == 'defender' then
		msg.player_action.toHouseType = self.defender_house
	end
	return msg
end

function _M:clean_up()
	event_dispatcher.off(events.support_order_send_click, self.on_support_order_send_click)
end

return _M