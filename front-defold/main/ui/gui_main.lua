local tracks = require "main/ui/tracks"
local top_panel = require "main/ui/top_panel"
local orders = require "main/ui/orders"
local login = require "main/ui/login/debug_login"
local supply_panel = require "main/ui/supply_panel"
local hints = require "main/ui/hints"
local player_panels = require "main/ui/player_panel"
local raven_card_or_order = require "main/ui/dialogs/raven_choose_card_or_order"
local leave_power_token = require "main/ui/dialogs/leave_power_token"
local march_select_army = require "main/ui/dialogs/march_select_army"
local save_load_menu = require "main/ui/settings/save_load"
local misc_buttons = require "main/ui/misc_buttons"
local list_of_saves = require "main/ui/settings/list_of_saves"
local confirm = require "main/ui/dialogs/confirm"
local partial_march_orders = require "main/ui/partial_march_orders"
local player_screen = require "main/ui/player_screen"
local combat_screen = require "main/ui/combat_screen"

local ws = require "main/messaging/websocket"
local mes_proc = require "main/messaging/message_processing"
local action_proc = require "main/messaging/action_reply_processing"
local event_dispatcher = require "main/ui/event_dispatcher"
local game_data = require "main/ui/game_data"

local _M = {}

local function register_callbacks()
	mes_proc.list_of_saves__show_saves = function(saves)
		list_of_saves:show_saves(saves)
	end

	action_proc.player_panel__set_player_turn = function(house_type)
		player_panels:set_player_turn(house_type)
	end
end

function _M:init()
	local path_ = sys.get_application_path()
	if string.match(path_, '^http') then
		game_data.is_html5 = true
		login:dispose()
	else
		game_data.is_html5 = false
		login:init()
	end

	ws:init()

	player_panels:init()

	if game_data.is_html5 then
		ws.connect({}, function()
			player_panels:set_players(game_data.players)
			tracks:set_me(game_data.me)
		end)
	end

	register_callbacks()

	tracks:init()
	top_panel:init()
	orders:init()
	supply_panel:init()
	hints:init()
	raven_card_or_order:init()
	leave_power_token:init()
	save_load_menu:init()
	misc_buttons:init(not game_data.is_html5)
	list_of_saves:init()
	march_select_army:init()
	confirm:init()
	partial_march_orders:init()
	player_screen:init()
	combat_screen:init()

	self.panels = {
		player_screen,
		misc_buttons,
		hints,
		combat_screen,
		player_panels,
		orders,
		tracks,
		raven_card_or_order,
		supply_panel,
		march_select_army,
		confirm,
		partial_march_orders,
		leave_power_token,
	}
end

function _M:check_pressed(x, y)
	for _, v in ipairs(self.panels) do
		if v:check_pressed(x, y) then
			return true
		end
	end
	return false
end

function _M:update(dt)
	ws.on_update(dt)
	local last_panel
	if self.action then
		local status, err = pcall(function ()
			if login:on_input(self.action) then
			elseif list_of_saves:check_button_pressed(self.action.x, self.action.y) then
			elseif save_load_menu:check_button_pressed(self.action.x, self.action.y) then
			else
				for _, panel in ipairs(self.panels) do
					last_panel = _
					if panel:check_button_pressed(self.action.x, self.action.y) then
						break
					end
				end
			end
		end)
		if status then self.action = nil end
		if not status then
			for key, v in pairs(self.action) do
				print(key, v)
			end
			print('last panel idx ' .. last_panel)
			self.action = nil
			error(err)
		end
	end
	if self.show_orders_menu_message then
		local status, err =
			pcall(event_dispatcher.trigger, 'map_show_orders_menu', self.show_orders_menu_message)
		self.show_orders_menu_message = nil
		if not status then
			error(err)
		end
	end
	if self.resolve_order_message then
		local status, err =
			pcall(event_dispatcher.trigger, 'map_resolve_order', self.resolve_order_message)
		self.resolve_order_message = nil
		if not status then
			error(err)
		end
	end
	if self.target_selected_message then
		local status, err =
			pcall(event_dispatcher.trigger, 'map_target_selected', self.target_selected_message)
		self.target_selected_message = nil
		if not status then
			error(err)
		end
	end
end

function _M:set_action(action)
	self.action = action
end

function _M:set_show_orders_menu_message(message)
	self.show_orders_menu_message = message
end

function _M:set_resolve_order_message(message)
	self.resolve_order_message = message
end

function _M:set_target_selected_message(message)
	self.target_selected_message = message
end

return _M