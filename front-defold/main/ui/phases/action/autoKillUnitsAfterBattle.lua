local army_logic = require "main/ui/army_logic"

---@class KillUnitsMessage
---@field tile_num number
---@field army MilitaryUnit[]

local _M = {}

---@param updated_combat Combat
function _M:kill_units(updated_combat)
	local old_combat = army_logic.combat
	local message = {}
	if old_combat.attackerArmy and not updated_combat.attackerArmy then
		message[#message + 1] = {
			tile_num = old_combat.defenderTileNum,
			army = old_combat.attackerArmy
		}
	end
	if old_combat.defenderArmy and not updated_combat.defenderArmy then
		message[#message + 1] = {
			tile_num = old_combat.defenderTileNum,
			army = old_combat.defenderArmy
		}
	end
	msg.post('/map', "kill_units", message)
end

return _M