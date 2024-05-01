local utils = require 'main/utils'

---@module BaseDialog
local _M = {
	on_closed = utils.noop,
	closed_position = 1325,
	opened_position = 1075,
	panel = nil
}

_M.__index = _M

function _M:close()
	gui.animate(
			self.panel,
			"position.x",
			self.closed_position,
			gui.PLAYBACK_ONCE_FORWARD,
			utils.ANIMATION_TIME,
			0,
			function()
				gui.set_enabled(self.panel, false)
				self:on_closed()
			end
	)
end

function _M:open()
	gui.set_enabled(self.panel, true)
	gui.animate(self.panel, "position.x", self.opened_position, gui.PLAYBACK_ONCE_FORWARD, utils.ANIMATION_TIME)
end

function _M:check_pressed(x, y)
	return gui.is_enabled(self.panel) and gui.pick_node(self.panel, x, y)
end

return _M