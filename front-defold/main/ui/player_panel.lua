local _M = {}

-- TODO move to it's own file
local function set_player_panel(self, player_panel_num, house_name)
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

-- TODO move to it's own file
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

function _M.set_players_panels(tracks_gui)
	set_players_panels(tracks_gui)
end

return _M