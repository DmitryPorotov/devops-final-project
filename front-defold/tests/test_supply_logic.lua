local luaunit = require 'luaunit'

local supply_logic = require 'main/ui/supply_logic'
local army_logic = require 'main/ui/army_logic'


supply_logic.set_available_supplies(1)

supply_logic.set_usage_rules({
	{2,2},
	{3,2},
	{3,2,2},
})

function test_get_max_armies()
	local max_armies = supply_logic.get_max_armies()
	luaunit.assertEquals(#max_armies, 2, 'should be allowed 2 armies')
	luaunit.assertTableContains(max_armies, 3, 'an army of 3')
	luaunit.assertTableContains(max_armies, 2, 'an army of 2')
end

function test_filter()
	---@type table<string, MilitaryUnit[]>
	local armies = {
		['3'] = {
			{
				house = 'wolf',
				type = 'footmen'
			},
			{
				house = 'wolf',
				type = 'footmen'
			},
			{
				house = 'wolf',
				type = 'footmen'
			},
		},
		['7'] = {
			{
				house = 'wolf',
				type = 'footmen'
			},
			{
				house = 'wolf',
				type = 'footmen'
			},
		},
	}
	local targets = {1,5,6,7,13}
	local r = supply_logic.filter_target_candidates("3", 2, targets,
			army_logic:house_armies_to_gui_format("wolf",armies)
	)
	for _, v in ipairs(targets) do
		if v== 7 then
			luaunit.assertNotTableContains(r, 7, 'should filter out 7')
		else
			luaunit.assertTableContains(r, v, 'should still contain ' .. v)
		end
	end

end

os.exit( luaunit.LuaUnit.run() )
