from dependency_injector.wiring import Provide, inject

from DTO.messages.messages import ErrorMessage
from DTO.actions.all_actions import Action, ActionResolveMarchOrder
from DTO.phases.phases import SubPhaseResolveMarchOrder
from containers_module import App
from events_service import EventSourcesService
from server_module.reactions.game_phase_reactions.phase_reactor import react_to_phase


class ErrorRetryHandler:
    @inject
    def __init__(self,events=Provide[App.events]):
        self._events: EventSourcesService = events
        self._events.react_to_game_event_sources.message_error.subscribe(on_next=self.on_error_message)

    def on_error_message(self, msg: ErrorMessage):
        if 'originalMessage' in msg:
            game_id = msg['originalMessage']['gameId']
            pa: Action = msg['originalMessage']['player_action']
            if pa['actionType'] == 'resolveMarchOrder':
                sp = self.__rebuild_resolve_march_phase(pa)
                react_to_phase(game_id, sp)
            else:
                pass

    def __rebuild_resolve_march_phase(self, action: ActionResolveMarchOrder) -> SubPhaseResolveMarchOrder:
         phase: SubPhaseResolveMarchOrder = {
             'mainPhase': 'phaseAction',
             'subPhase': 'resolveMarchOrder',
             'houseType': action['houseType'],
         }
         return phase