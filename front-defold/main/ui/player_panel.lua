local _M = {
	house_to_panel_num = {}
}

local function set_player_panel(self, player_panel_num, house_name)
	_M.house_to_panel_num[house_name] = player_panel_num
	local name = gui.get_node("player" .. player_panel_num .. "/player_name_text")
	gui.set_text(name, self.players[house_name]["name"])
	local shield = gui.get_node("player" .. player_panel_num .. "/shield")
	gui.play_flipbook(shield, hash(house_name))

	for i = 1, 3 do
		local icon = gui.get_node("player" .. player_panel_num .. "/icon" .. i)
		-- gui.set_color(icon, vmath.vector4(1,1,1,0))
		gui.set_enabled(icon, false)
	end

	local icon_num_to_set = 1
	if self.logic_tracks["throne"][1] == house_name then
		local icon = gui.get_node("player" .. player_panel_num .. "/icon" .. icon_num_to_set)
		-- gui.set_color(icon, vmath.vector4(1,1,1,1))
		gui.set_enabled(icon, true)
		gui.play_flipbook(icon, hash("throne_icon"))
		icon_num_to_set = icon_num_to_set + 1
	end
	if self.logic_tracks["fiefdoms"][1] == house_name then
		local icon = gui.get_node("player" .. player_panel_num .. "/icon" .. icon_num_to_set)
		-- gui.set_color(icon, vmath.vector4(1,1,1,1))
		gui.set_enabled(icon, true)
		gui.play_flipbook(icon, hash("sword_icon"))
		icon_num_to_set = icon_num_to_set + 1
	end
	if self.logic_tracks["court"][1] == house_name then
		local icon = gui.get_node("player" .. player_panel_num .. "/icon" .. icon_num_to_set)
		-- gui.set_color(icon, vmath.vector4(1,1,1,1))
		gui.set_enabled(icon, true)
		gui.play_flipbook(icon, hash("crow_icon"))
	end

end

local function set_players_panels(self)
	set_player_panel(self, 1, self.me)
	local i = 2
	for _, v in ipairs(self.logic_tracks["throne"]) do
		if v ~= self.me then
			set_player_panel(self, i, v)
			i = i + 1
		end
	end
end

function _M:set_player_ready(house)
	local bg = gui.get_node("player" .. self.house_to_panel_num[house] .. "/players_turn")
	gui.set_color(bg, vmath.vector4(1,1,1,.7))
end

function _M:clear_ready_all()
	for i = 1, 6 do
		local bg = gui.get_node("player" .. i .. "/players_turn")
		gui.cancel_animation(bg, "color.w")
		gui.set_color(bg, vmath.vector4(1,1,1,0))
	end
end

function _M:set_player_turn(house)
	self:clear_ready_all()
	local bg = gui.get_node("player" .. self.house_to_panel_num[house] .. "/players_turn")
	gui.set_color(bg, vmath.vector4(1,1,1,.5))
	gui.animate(bg, "color.w", 1, gui.EASING_LINEAR, 1, 0, nil, gui.PLAYBACK_LOOP_PINGPONG)
end

function _M.set_players_panels(tracks_gui)
	_M.panels = {
		gui.get_node('player1/player_panel'),
		gui.get_node('player2/player_panel'),
		gui.get_node('player3/player_panel'),
		gui.get_node('player4/player_panel'),
		gui.get_node('player5/player_panel'),
		gui.get_node('player6/player_panel'),
	}
	set_players_panels(tracks_gui)
end

function _M:check_pressed(x, y)
	if self.panels then 
		for _, v in ipairs(self.panels) do
			if gui.pick_node(v, x, y) then
				return true
			end
		end
	end
	return false
end

return _M