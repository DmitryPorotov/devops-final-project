from dependency_injector.wiring import Provide, inject

from DTO.actions.action import ActionRetreatUnitsAfterBattle, ActionResolveSpecialConsolidatePower, \
    ActionResolveCardMoose3
from DTO.actions.events import ActionMuster, ActionWildlingsMusterAtCastle, ActionTrackBids
from DTO.actions.planning import ActionAddOrder
from DTO.messages.messages import ErrorMessage
from DTO.actions.all_actions import Action, ActionResolveMarchOrder
from DTO.phases.phases import SubPhaseResolveMarchOrder, SubPhaseResolveHouseCard, SubPhaseRetreatUnitsAfterBattle, \
    SubPhaseMuster, SubPhaseWildlingsMusterAtCastle, SubPhaseAddOrder, SubPhaseResolveSpecialConsolidatePower, \
    SubPhaseTracksBids
from containers_module import App
from events_service import EventSourcesService
from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_phase_reactions.phase_reactor import react_to_phase

class ErrorRetryHandler:
    @inject
    def __init__(self, events=Provide[App.events], game_data=Provide[App.game_service]):
        self._events: EventSourcesService = events
        self._events.react_to_game_event_sources.message_error.subscribe(on_next=self.on_error_message)
        self._game_data: GamesDataService = game_data

    def on_error_message(self, msg: ErrorMessage):
        if 'originalMessage' in msg:
            game_id = msg['originalMessage']['gameId']
            game = self._game_data.get_game(game_id)
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
                if game.error_retry_counter.can_retry('muster', pa["houseType"]):
                    react_to_phase(game_id, sp3)
                else:
                    pseudo_phase_finish_mustering = {
                        "mainPhase": "phaseRoundEvents",
                        "subPhase": "finishMustering",
                        "houseType": pa3["houseType"],
                    }
                    game.error_retry_counter.reset_retries()
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
                if game.error_retry_counter.can_retry('wildlingsMusterAtCastle', pa["houseType"]):
                    sp5: SubPhaseWildlingsMusterAtCastle = {
                        "mainPhase": "phaseRoundEvents",
                        "subPhase": "wildlingsMusterAtCastle",
                        "houseType": pa5["houseType"],
                    }
                    react_to_phase(game_id, sp5)
                else:
                    pseudo_phase_finish_mustering = {
                        "mainPhase": "phaseRoundEvents",
                        "subPhase": "wildlingsFinishMusteringAtCastle",
                        "houseType": pa5["houseType"],
                    }
                    game.error_retry_counter.reset_retries()
                    react_to_phase(game_id, pseudo_phase_finish_mustering)
            elif pa['actionType'] == 'addOrder':
                pa5: ActionAddOrder = pa
                state = game.state
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
            elif pa['actionType'] == 'wildlingsBids':
                if 'power tokens which is not enough to place this bid' in msg['message']:
                    pa7: ActionTrackBids = pa
                    my_tokens = int(msg['message'].split()[2])
                    game.state.power_tokens[HouseType[pa7["houseType"].upper()]] = my_tokens
                    sp7: SubPhaseTracksBids = {
                        "mainPhase": "phaseAction",
                        "subPhase": "tracksBids",
                        "houseTypes": [pa7["houseType"]],
                        'trackType': game.other['last_track']
                    }
                    react_to_phase(game_id, sp7)
            elif pa['actionType'] == 'resolveCardMoose3':
                pa8: ActionResolveCardMoose3 = pa
                sp8: SubPhaseResolveHouseCard = {
                    "mainPhase": "phaseAction",
                    "subPhase": "resolveHouseCard",
                    "houseType": pa8['houseType'],
                    "cardCode": 3
                }
                react_to_phase(game_id, sp8)
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
