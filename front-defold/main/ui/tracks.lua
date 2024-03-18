--local player_panels = require "main/ui/player_panel"
local utils = require "main/utils"

local _M = {}

local STARTING_Y_POSITION = 185
local Y_STEP = 70

function _M:init(player_panels)
	self.player_panels = player_panels
	self.logic_tracks = nil
	self.is_open = false
	self.is_init = false
	self.panel = gui.get_node("tracks_full")
end

function _M:set_players(players)
	self.players = players
	if self.logic_tracks then
		self.player_panels.set_players_panels(self)
	end
end

function _M:set_me(me)
	self.me = me
end

function _M:check_button_pressed(x, y)
	if gui.pick_node(self.panel, x, y) then
		if self.is_open then
			self:close()
		else
			self:open()
		end
	end
end

function _M:check_pressed(x, y)
	return gui.pick_node(self.panel, x, y)
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

local function shift_others(my_index, prefix)
	for i = 1, my_index do
		if i == my_index then
			return
		else
			local node = gui.get_node(prefix .. i)
			-- gui.set_position(node, vmath.vector3(0, STARTING_Y_POSITION - i * Y_STEP, 0))
			gui.animate(node, "position.y", STARTING_Y_POSITION - i * Y_STEP, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
		end
	end
end

local function unshift_others(my_index, prefix)
	for i = 1, my_index do
		if i == my_index then
			return
		else
			local node = gui.get_node(prefix .. i)
			-- gui.set_position(node, vmath.vector3(0, STARTING_Y_POSITION - 10 - (i - 1) * Y_STEP, 0))
			gui.animate(node, "position.y", STARTING_Y_POSITION - 10 - (i - 1) * Y_STEP, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
		end
	end
end

local function set_closed_locations(self, track_name)
	local prefix = string.sub(track_name, 1, 1)
	for i, v in ipairs(self.logic_tracks[track_name]) do
		if self.me == v then
			local my_node = gui.get_node(prefix .. i)
			gui.move_above(my_node, gui.get_node(prefix .. 6))
			gui.set_position(my_node, vmath.vector3(0, STARTING_Y_POSITION + 5, 0))
			gui.set_scale(my_node, vmath.vector3(.7, .7, 1))
			shift_others(i, prefix)
			return i
		end
	end
end

function _M:set_tracks(tracks)
	self.logic_tracks = tracks
	if not self.is_init then
		set_shields(tracks, "throne")
		set_shields(tracks, "fiefdoms")
		set_shields(tracks, "court")
		self.my_t_idx = set_closed_locations(self, "throne")
		self.my_f_idx = set_closed_locations(self, "fiefdoms")
		self.my_c_idx = set_closed_locations(self, "court")
		self.player_panels.set_players_panels(self)
	end
end

local function ani_open(prefix, i)
	local under_shield_u = gui.get_node(prefix .. "_under_shield_u" .. i)
	gui.animate(under_shield_u, "color.w", 1, gui.EASING_LINEAR, utils.ANIMATION_TIME)
	gui.animate(under_shield_u, "color.w", 0.5, gui.EASING_LINEAR, 0.7, utils.ANIMATION_TIME, nil, gui.PLAYBACK_LOOP_PINGPONG)
	local t1 = gui.get_node(prefix .. i)
	gui.animate(t1, "scale", 1, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
	gui.animate(t1, "position.y", STARTING_Y_POSITION - 10 - (i - 1) * Y_STEP, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
	unshift_others(i, prefix)
end

function _M:open()
	gui.animate(self.panel, "position.y", 250, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
	ani_open("t", self.my_t_idx)
	ani_open("f", self.my_f_idx)
	ani_open("c", self.my_c_idx)
	self.is_open = true
end

local function ani_close(prefix, i)
	local under_shield_u = gui.get_node(prefix .. "_under_shield_u" .. i)
	gui.cancel_animation(under_shield_u, "color.w")
	gui.animate(under_shield_u, "color.w", 0, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
	local t1 = gui.get_node(prefix .. i)
	gui.animate(t1, "scale", .7, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
	gui.animate(t1, "position.y", STARTING_Y_POSITION + 5, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
	shift_others(i, prefix)
end

function _M:close()
	gui.animate(self.panel, "position.y", -120, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
	ani_close("t", self.my_t_idx)
	ani_close("f", self.my_f_idx)
	ani_close("c", self.my_c_idx)
	self.is_open = false
end

return _M