from dependency_injector.wiring import Provide, inject

from DTO.actions.action import ActionRetreatUnitsAfterBattle
from DTO.messages.messages import ErrorMessage
from DTO.actions.all_actions import Action, ActionResolveMarchOrder
from DTO.phases.phases import SubPhaseResolveMarchOrder, SubPhaseResolveHouseCard, SubPhaseRetreatUnitsAfterBattle
from containers_module import App
from events_service import EventSourcesService
from server_module.game_state.house_type import HouseType
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
            elif pa['actionType'] == 'resolveCardWolf0':
                sp1: SubPhaseResolveHouseCard = {
                    "mainPhase": "phaseAction",
                    'subPhase': 'resolveHouseCard',
                    'cardCode': 0,
                    'houseType': HouseType.WOLF
                }
                react_to_phase(game_id, sp1)
            elif pa['actionType'] == 'retreatUnitsAfterBattle':
                pa2: ActionRetreatUnitsAfterBattle = pa
                sp2: SubPhaseRetreatUnitsAfterBattle = {
                    "mainPhase": "phaseAction",
                    "subPhase": "retreatUnitsAfterBattle",
                    "houseType": pa2["houseType"],
                }
                react_to_phase(game_id, sp2)
            else:
                pass

    @staticmethod
    def __rebuild_resolve_march_phase(action: ActionResolveMarchOrder) -> SubPhaseResolveMarchOrder:
         phase: SubPhaseResolveMarchOrder = {
             'mainPhase': 'phaseAction',
             'subPhase': 'resolveMarchOrder',
             'houseType': action['houseType'],
         }
         return phase
