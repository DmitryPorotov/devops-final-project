local event_dispatcher = require "main/ui/event_dispatcher"

local tokens_count

local function init(counts)
	tokens_count = counts
end

---@param house string
---@param number number
local function add(house, number)
	tokens_count[house] = tokens_count[house] + number
	if tokens_count[house] < 0 then
		tokens_count[house] = 0
	-- todo : wtf do I do with power tokens left on the map?
	elseif tokens_count[house] > 20 then
		tokens_count[house] = 20
	end
	event_dispatcher.trigger('set_power_tokens', house, tokens_count[house])
end

local function get(house)
	return tokens_count[house]
end

-- note: do I need set()?
return {
	init = init,
	add = add,
	get = get
}