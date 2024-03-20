local utils = require 'main/utils'

local _M = {
	unit_names = {
		'footmen',
		'knights',
		'siegeEngines',
		'ships'
	},
}

function _M.build_unit_and_count_phrase(type, count)
	if type == _M.unit_names[1] then
		if count == 1 then
			return '1 footman'
		else
			return count .. ' footmen'
		end
	elseif type == _M.unit_names[2] then
		if count == 1 then
			return '1 knight'
		else
			return count .. ' knights'
		end
	elseif type == _M.unit_names[3] then
		if count == 1 then
			return '1 ship'
		else
			return count .. ' ships'
		end
	elseif type == _M.unit_names[4] then
		if count == 1 then
			return '1 siege engine'
		else
			return count .. ' siege engines'
		end
	end
end

---@param army table Server format army
---@return table GUI format army
function _M.to_gui_format(army)
	local avail_counts = {}
	for _, v in ipairs(army) do
		if utils.index_of(_M.unit_names, v.type) and not v.isDefeated then
			avail_counts[v.type] = (avail_counts[v.type] or 0) + 1
		end
	end
	return avail_counts
end

return _M