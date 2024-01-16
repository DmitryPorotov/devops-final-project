-- Put functions in this file to use them in several other scripts.
-- To get access to the functions, you need to put:
-- require "my_directory.my_file"
-- in any script using the functions
local _M = {}
local ANIMATION_TIME = .15

local STARTING_Y_POSITION = 185
local Y_STEP = 70

function _M.init(self, my_house, players)
	self.logic_tracks = nil
	self.is_open = false
	self.is_init = false
	self.me = my_house
	self.players = players
	self.tracks = gui.get_node("tracks_full")
end

local function set_shields(tracks, track_name)
	local prefix = string.sub(track_name, 1, 1)
	for i = 1, 6 do
		local tmp = gui.get_node("shield-" .. tracks[track_name][i])
		local shield_clone = gui.clone_tree(tmp)
		local shield = shield_clone["shield-" .. tracks[track_name][i]]
		gui.set_enabled(shield, true)
		gui.set_id(shield, prefix .. "_shield-" .. i)
		gui.set_parent(shield, gui.get_node(prefix .. i))
		gui.set_position(shield, vmath.vector3(0, -4, 0))
	end
end

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

local function shift_others(my_index, prefix)
	for i = 1, my_index do
		if i == my_index then
			return
		else
			local node = gui.get_node(prefix .. i)
			gui.set_position(node, vmath.vector3(0, STARTING_Y_POSITION - i * Y_STEP, 0))
		end
	end
end

local function unshift_others(my_index, prefix)
	for i = 1, my_index do
		if i == my_index then
			return
		else
			local node = gui.get_node(prefix .. i)
			gui.set_position(node, vmath.vector3(0, STARTING_Y_POSITION - 10 - (i - 1) * Y_STEP, 0))
		end
	end
end

function _M.set_closed_locations(self, track_name)
	local prefix = string.sub(track_name, 1, 1)
	for i, v in ipairs(self.logic_tracks[track_name]) do
		if self.me == v then
			local my_node = gui.get_node(prefix .. i)
			gui.set_position(my_node, vmath.vector3(0, STARTING_Y_POSITION + 5, 0))
			gui.set_scale(my_node, vmath.vector3(.7, .7, 1))
			shift_others(i, prefix)
			return i
		end
	end
end

function _M.set_tracks(self, tracks)
	self.logic_tracks = tracks
	if not self.is_init then
		set_shields(tracks, "throne")
		set_shields(tracks, "fiefdoms")
		set_shields(tracks, "court")
		self.my_t_idx = self:set_closed_locations("throne")
		self.my_f_idx = self:set_closed_locations("fiefdoms")
		self.my_c_idx = self:set_closed_locations("court")
		set_players_panels(self)
	end
end

local function ani_open(prefix, i)
	local under_shield_u = gui.get_node(prefix .. "_under_shield_u" .. i)
	gui.animate(under_shield_u, "color.w", 1, gui.EASING_LINEAR, ANIMATION_TIME)
	gui.animate(under_shield_u, "color.w", 0.5, gui.EASING_LINEAR, 0.7, ANIMATION_TIME, nil, gui.PLAYBACK_LOOP_PINGPONG)
	local t1 = gui.get_node(prefix .. i)
	gui.animate(t1, "scale", 1, gui.PLAYBACK_ONCE_FORWARD, ANIMATION_TIME)
	gui.animate(t1, "position.y", STARTING_Y_POSITION - 10 - (i - 1) * Y_STEP, gui.PLAYBACK_ONCE_FORWARD, ANIMATION_TIME)
	unshift_others(i, prefix)
end

function _M.open(self)
	gui.animate(self.tracks, "position.y", 250, gui.PLAYBACK_ONCE_FORWARD, ANIMATION_TIME)
	ani_open("t", self.my_t_idx)
	ani_open("f", self.my_f_idx)
	ani_open("c", self.my_c_idx)
	self.is_open = true
end

local function ani_close(prefix, i)
	local under_shield_u = gui.get_node(prefix .. "_under_shield_u" .. i)
	gui.cancel_animation(under_shield_u, "color.w")
	gui.animate(under_shield_u, "color.w", 0, gui.PLAYBACK_ONCE_FORWARD, ANIMATION_TIME)
	local t1 = gui.get_node(prefix .. i)
	gui.animate(t1, "scale", .7, gui.PLAYBACK_ONCE_FORWARD, ANIMATION_TIME)
	gui.animate(t1, "position.y", STARTING_Y_POSITION + 5, gui.PLAYBACK_ONCE_FORWARD, ANIMATION_TIME)
	shift_others(i, prefix)
end

function _M.close(self)
	gui.animate(self.tracks, "position.y", -120, gui.PLAYBACK_ONCE_FORWARD, ANIMATION_TIME)
	ani_close("t", self.my_t_idx)
	ani_close("f", self.my_f_idx)
	ani_close("c", self.my_c_idx)
	self.is_open = false
end

return _M