package = "front-defold"
version = "dev-1"
source = {
   url = "git+ssh://git@github.com/DmitryPorotov/table-games-monorepo.git"
}
description = {
   summary = "This project was created from the \"empty\" project template.",
   detailed = "This project was created from the \"empty\" project template.",
   homepage = "*** please enter a project homepage ***",
   license = "*** please specify a license ***"
}
build = {
   type = "builtin",
   modules = {
      ["main.labels"] = "main/labels.lua",
      ["main.map"] = "main/map.lua",
      ["main.messaging.action_reply_processing"] = "main/messaging/action_reply_processing.lua",
      ["main.messaging.message_processing"] = "main/messaging/message_processing.lua",
      ["main.messaging.websocket"] = "main/messaging/websocket.lua",
      ["main.messaging.websocket_native"] = "main/messaging/websocket_native.lua",
      ["main.ui.dialogs.raven_choose_card_or_order"] = "main/ui/dialogs/raven_choose_card_or_order.lua",
      ["main.ui.game_data"] = "main/ui/game_data.lua",
      ["main.ui.hints"] = "main/ui/hints.lua",
      ["main.ui.login.debug_login"] = "main/ui/login/debug_login.lua",
      ["main.ui.misc_buttons"] = "main/ui/misc_buttons.lua",
      ["main.ui.orders"] = "main/ui/orders.lua",
      ["main.ui.phases.addOrder"] = "main/ui/phases/addOrder.lua",
      ["main.ui.phases.ravenChangeOrder"] = "main/ui/phases/ravenChangeOrder.lua",
      ["main.ui.player_panel"] = "main/ui/player_panel.lua",
      ["main.ui.settings.list_of_saves"] = "main/ui/settings/list_of_saves.lua",
      ["main.ui.settings.save_load"] = "main/ui/settings/save_load.lua",
      ["main.ui.supply_panel"] = "main/ui/supply_panel.lua",
      ["main.ui.top_panel"] = "main/ui/top_panel.lua",
      ["main.ui.tracks"] = "main/ui/tracks.lua",
      ["main.utils"] = "main/utils.lua"
   }
}
