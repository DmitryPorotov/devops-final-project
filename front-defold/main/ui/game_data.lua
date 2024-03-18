local _M = {
	players = {
		moose = { id = -1, name = "Waiting for player..." },
		lion = { id = -1, name = "Waiting for player..." },
		wolf = { id = -1, name = "Waiting for player..." },
		pufferfish = { id = -1, name = "Waiting for player..." },
		kraken = { id = -1, name = "Waiting for player..." },
		rose = { id = -1, name = "Waiting for player..." }
	},
	i_joined = false,
	me = nil,
	user_data = nil,

	gameRules = nil,

	tracks = nil,
	armies = nil,
	subPhase = nil,
	supplies = nil,
	wildlingCounter = 0,
	discardedHouseCards = nil,

	game_id = 2,
	creating_new_game = false
}

function _M:is_my_port(port_num)
	local army_in_port = self.armies[tostring(port_num)]
	if army_in_port and army_in_port[1] and army_in_port[1].house == self.me then
		return true
	else
		local army_on_land = self.armies[tostring(port_num - 1)]
		if army_on_land and army_on_land[1] and army_on_land[1].house == self.me then
			return true
		end
	end
	return false
end

function _M:calculate_possible_destinations(from_tile)
	local f_t = self.gameRules.board[from_tile + 1]
	if f_t.number ~= from_tile then
		error('Tile number ' .. from_tile .. 'does not correspond to ' .. f_t.number)
	end
	local candidates = {}
	if f_t.tileType == 'sea' then
		for i, v in ipairs(f_t.neighbourTiles) do
			local c = self.gameRules.board[v + 1]
			if c.tileType == 'sea' or (c.tileType == 'port' and self:is_my_port(c.number)) then
				candidates[#candidates + 1] = c.number
			end
		end
	elseif f_t.tileType == 'port' then
		for i, v in ipairs(f_t.neighbourTiles) do
			local c = self.gameRules.board[v + 1]
			if c.tileType == 'sea' then
				candidates[#candidates + 1] = c.number
			end
		end
	else
		for i, v in ipairs(f_t.neighbourTiles) do
			local c = self.gameRules.board[v + 1]
			if c.tileType == 'land' then
				candidates[#candidates + 1] = c.number
			end
			-- todo traveling by sea
		end
	end
	return candidates
end

return _M
