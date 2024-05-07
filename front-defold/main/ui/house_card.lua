---@module HouseCardWrapper
---@field card HouseCard
---@field house string
---@field gui table
---@field bg userdata
local _M = {}

---@param self HouseCardWrapper
local function build_card_gui(self)
	local tmp = gui.get_node('house_card/bg')
	self.gui = gui.clone_tree(tmp)

	self.bg = self.gui[hash('house_card/bg')]
	gui.set_text(self.gui[hash('house_card/power_text')], self.card.strength)
	gui.set_text(self.gui[hash('house_card/name_text')], self.card.name)
	if self.card.text ~= '' then
		gui.set_text(self.gui[hash('house_card/ability_text')], self.card.text)
	else
		local num_icons = self.card.attack + self.card.defense
		local sword_hash = hash('card_sword_icon')
		local tower_hash = hash('card_tower_icon')
		if num_icons == 1 then
			local i = self.gui[hash('house_card/icon2')]
			gui.set_enabled(i, true)
			if self.card.attack == 1 then
				gui.play_flipbook(i, sword_hash)
			else
				gui.play_flipbook(i, tower_hash)
			end
		elseif num_icons == 2 then
			local i1 = self.gui[hash('house_card/icon1')]
			local i2 = self.gui[hash('house_card/icon2')]
			gui.set(i1, 'position.x', -40)
			gui.set(i2, 'position.x', 40)
			gui.set_enabled(i1, true)
			gui.set_enabled(i2, true)
			if self.card.attack == 2 then
				gui.play_flipbook(i1, sword_hash)
				gui.play_flipbook(i2, sword_hash)
			elseif self.card.attack == 1 then
				gui.play_flipbook(i1, sword_hash)
				gui.play_flipbook(i2, tower_hash)
			else
				gui.play_flipbook(i1, tower_hash)
				gui.play_flipbook(i2, tower_hash)
			end
		else
			local i1 = self.gui[hash('house_card/icon1')]
			local i2 = self.gui[hash('house_card/icon2')]
			local i3 = self.gui[hash('house_card/icon3')]
			gui.set_enabled(i1, true)
			gui.set_enabled(i2, true)
			gui.set_enabled(i3, true)
			if self.card.attack == 3 then
				gui.play_flipbook(i1, sword_hash)
				gui.play_flipbook(i2, sword_hash)
				gui.play_flipbook(i3, sword_hash)
			elseif self.card.attack == 2 then
				gui.play_flipbook(i1, sword_hash)
				gui.play_flipbook(i2, sword_hash)
				gui.play_flipbook(i3, tower_hash)
			elseif self.card.attack == 1 then
				gui.play_flipbook(i1, sword_hash)
				gui.play_flipbook(i2, tower_hash)
				gui.play_flipbook(i3, tower_hash)
			-- note: defense == 3 does not exist
			end
		end
	end
end

---@param logic_card HouseCard
---@return HouseCardWrapper
function _M:new(logic_card)
	local o = {
		house = house,
		card = logic_card
	}
	build_card_gui(o)
	setmetatable(o, self)
	self.__index = self
	return o
end

function _M:delete()
	gui.delete_node(self.bg)
end

function _M:check_press(x, y)
	return gui.pick_node(self.bg, x, y)
end

function _M:set_parent(parent)
	gui.set_parent(self.bg, parent)
end

function _M:set_position(position)
	gui.set_position(self.bg, position)
end

return _M
