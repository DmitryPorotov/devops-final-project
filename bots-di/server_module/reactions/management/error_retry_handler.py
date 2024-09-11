from dependency_injector.wiring import Provide, inject

from DTO.actions.action import ActionRetreatUnitsAfterBattle, ActionResolveSpecialConsolidatePower
from DTO.actions.events import ActionMuster, ActionWildlingsMusterAtCastle
from DTO.actions.planning import ActionAddOrder
from DTO.messages.messages import ErrorMessage
from DTO.actions.all_actions import Action, ActionResolveMarchOrder
from DTO.phases.phases import SubPhaseResolveMarchOrder, SubPhaseResolveHouseCard, SubPhaseRetreatUnitsAfterBattle, \
    SubPhaseMuster, SubPhaseWildlingsMusterAtCastle, SubPhaseAddOrder, SubPhaseResolveSpecialConsolidatePower
from containers_module import App
from events_service import EventSourcesService
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_phase_reactions.phase_reactor import react_to_phase


class ErrorRetryHandler:
    __NUM_RETRIES = 10

    @inject
    def __init__(self, events=Provide[App.events], game_data=Provide[App.game_manager]):
        self._events: EventSourcesService = events
        self._events.react_to_game_event_sources.message_error.subscribe(on_next=self.on_error_message)
        self._game_data: GamesDataService = game_data
        self.__retry_counter = 0
        self.__retry_key = ""

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
            elif pa['actionType'] == 'muster':
                pa3: ActionMuster = pa
                sp3: SubPhaseMuster = {
                    "mainPhase": "phaseRoundEvents",
                    "subPhase": "muster",
                    "houseType": pa3["houseType"],
                }
                if self.__can_retry('muster', pa["houseType"]):
                    react_to_phase(game_id, sp3)
                else:
                    pseudo_phase_finish_mustering = {
                        "mainPhase": "phaseRoundEvents",
                        "subPhase": "finishMustering",
                        "houseType": pa3["houseType"],
                    }
                    react_to_phase(game_id, pseudo_phase_finish_mustering)
            elif pa['actionType'] == 'resolveMarchOrder':
                pa4: ActionResolveMarchOrder = pa
                sp4: SubPhaseResolveMarchOrder = {
                    "mainPhase": "phaseRoundEvents",
                    "subPhase": "muster",
                    "houseType": pa4["houseType"],
                }

                react_to_phase(game_id, sp4)
            elif pa['actionType'] == 'wildlingsMusterAtCastle':
                pa5: ActionWildlingsMusterAtCastle = pa
                sp5: SubPhaseWildlingsMusterAtCastle = {
                    "mainPhase": "phaseRoundEvents",
                    "subPhase": "wildlingsMusterAtCastle",
                    "houseType": pa5["houseType"],
                }
                react_to_phase(game_id, sp5)
            elif pa['actionType'] == 'addOrder':
                pa5: ActionAddOrder = pa
                state = self._game_data.get_game(game_id).state
                state.placed_orders.remove_order(pa5['tileNumber'])
                state.available_orders.return_order(pa5['houseType'], Order.from_json(pa5['order']))
                if 'There is an order on this tile' not in msg['message']:
                    sp5: SubPhaseAddOrder = {
                        "mainPhase": "phasePlanning",
                        "subPhase": "addOrder",
                        "houseTypes": [pa5["houseType"]],
                    }
                    react_to_phase(game_id, sp5)
            elif pa['actionType'] == 'openOrders' and 'not placed any' not in msg['message']:
                pa5: ActionAddOrder = pa
                sp5: SubPhaseAddOrder = {
                    "mainPhase": "phasePlanning",
                    "subPhase": "addOrder",
                    "houseTypes": [pa5["houseType"]],
                }
                react_to_phase(game_id, sp5)
            elif pa['actionType'] == 'resolveSpecialConsolidatePower':
                pa6: ActionResolveSpecialConsolidatePower = pa
                sp6: SubPhaseResolveSpecialConsolidatePower = {
                    "mainPhase": "phaseAction",
                    "subPhase": "resolveSpecialConsolidatePower",
                    "houseType": pa6["houseType"],
                }
                react_to_phase(game_id, sp6)
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

    def reset_retries(self):
        self.__retry_key = ''

    def __can_retry(self, phase, house_type) -> bool:
        key = "{}-{}".format(phase, house_type)
        if self.__retry_key != key:
            self.__retry_key = key
            self.__retry_counter = 0
        self.__retry_counter += 1
        return self.__retry_counter < self.__NUM_RETRIES