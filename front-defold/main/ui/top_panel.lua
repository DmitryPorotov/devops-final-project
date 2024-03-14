local _M = {}

function _M:init()
	self.round_counter = gui.get_node("top_panel/round_counter")
	self.wildlings_counter = gui.get_node("top_panel/wildlings_counter")
	self.label = gui.get_node("top_panel/label")
end

function _M:set_game_state(game_state)
	gui.set_text(self.round_counter, game_state.roundCounter .. "/10")
	gui.set_text(self.wildlings_counter, game_state.wildlingCounter .. "/12")
	local phase
	if game_state.subPhase.mainPhase == "phaseRoundEvents" then
		phase = "Westeros Phase"
	elseif game_state.subPhase.mainPhase == "phasePlanning" then
		phase = "Planning Phase"
	else
		phase = "Action Phase"
	end
	gui.set_text(self.label, phase)
end

return _M