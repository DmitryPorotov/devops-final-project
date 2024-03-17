local event_dispatcher = require "main/ui/event_dispatcher"
local orders = require "main/ui/orders"
local hints = require "main/ui/hints"
local ravenChoose = require "main/ui/phases/planning/ravenChooseChangeOrderOrLookAtWildlingCard"
local _M = {}

function _M:init()
	event_dispatcher.on('map_show_orders_menu', function(message)
		orders:open(
				message.label,
				message.tile_num,
				message.name,
				message.deleted,
				true
		)
	end)
	event_dispatcher.on('order_button_click', function()
		local order = orders:get_order_to_send()
		event_dispatcher.trigger('ws_send', order)
		orders:add_order_to_map()
	end)
	local text = ravenChoose.raven_choice_prefix_text
			.. ravenChoose.raven_choice_change_order_text
	hints:none_actionable_hint(text)
end

function _M:clean_up()
	event_dispatcher.off('map_show_orders_menu')
	event_dispatcher.off('order_button_click')
end

return _M
