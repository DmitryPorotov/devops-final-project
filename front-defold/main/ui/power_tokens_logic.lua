local event_dispatcher = require "main/ui/event_dispatcher"
local events = require "main/ui/events"

local tokens_count
local tokens_on_map = {}

local function init(counts)
	tokens_count = counts
end

---@param house string
---@param number number
local function add(house, number)
	tokens_count[house] = tokens_count[house] + number
	if tokens_count[house] < 0 then
		tokens_count[house] = 0
	elseif tokens_count[house] > 20 - tokens_on_map[house] then
		tokens_count[house] = 20 - tokens_on_map[house]
	end
	event_dispatcher.trigger(events.set_power_tokens, house, tokens_count[house])
end

---@param house string
---@param do_count_only boolean
local function leave_power_token_on_map(house, do_count_only)
	do_count_only = do_count_only or false
	if not tokens_on_map[house] then
		tokens_on_map[house] = 0
	end
	tokens_on_map[house] = tokens_on_map[house] + 1
	if not do_count_only then
		add(house, -1)
	end
end

local function get(house)
	return tokens_count[house]
end

-- note: do I need set()?
return {
	init = init,
	add = add,
	get = get,
	leave_power_token_on_map = leave_power_token_on_map,
}