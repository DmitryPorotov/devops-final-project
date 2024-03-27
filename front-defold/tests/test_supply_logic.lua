local luaunit = require 'luaunit'

local supply_logic = require 'main/ui/supply_logic'

supply_logic.set_available_supplies(1)

supply_logic.set_usage_rules({
	[2] = {3,2}
})

function test_get_max_armies()
	local max_armies = supply_logic.get_max_armies()
	luaunit.assertEquals(#max_armies, 2, 'should be allowed 2 armies')
	luaunit.assertTableContains(max_armies, 3, 'an army of 3')
	luaunit.assertTableContains(max_armies, 2, 'an army of 2')
end



os.exit( luaunit.LuaUnit.run() )
