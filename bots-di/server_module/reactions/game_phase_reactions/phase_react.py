import json
import logging
from typing import Optional, Type

from dependency_injector.wiring import Provide, inject

from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import SubPhaseWildlingsKillUnits
from containers_module import App
from redis_service import RedisConnector
from server_module.game_state.house_type import HouseType
from server_module.games_data_service import GamesDataService, GameHandle
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from server_module.reactions.game_phase_reactions.no_reply_needed_exception import NoReplyNeedException
from utils_ import print_file_lineno_error


class PhaseReact:
    game_data: Optional[GamesDataService] = None
    redis: Optional[RedisConnector] = None
    __update_state_sub_phases = ['setEventCards', 'wildlingsCard']

    @staticmethod
    @inject
    def init(game_data: GamesDataService = Provide[App.game_manager],
             redis: RedisConnector = Provide[App.redis_service]):
        PhaseReact.game_data = game_data
        PhaseReact.redis = redis
        PhaseReact.logger = logging.getLogger(
            f"{__name__}.{PhaseReact.__class__.__name__}",
        )

    @staticmethod
    def react(phase_reaction_cls: Type[BasePhaseReaction],
              game_id: str,
              sub_phase: SubPhase,
              ):
        game = PhaseReact.game_data.get_game(game_id)
        if 'houseType' in sub_phase:
            if sub_phase['houseType'] in game.houses:
                PhaseReact.__act_on_house(game_id, game, sub_phase['houseType'], phase_reaction_cls, sub_phase)
        elif 'houseTypes' in sub_phase:
            if isinstance(sub_phase['houseTypes'], list):
                if sub_phase['subPhase'] in PhaseReact.__update_state_sub_phases:
                    PhaseReact.__act_on_house(game_id, game, sub_phase['houseTypes'][0], phase_reaction_cls, sub_phase)
                else:
                    for h in sub_phase['houseTypes']:
                        if h in game.houses:
                            game.multi_house_reaction.react(
                                sub_phase,
                                h,
                                lambda: PhaseReact.__act_on_house(game_id, game, h, phase_reaction_cls, sub_phase)
                            )
            elif isinstance(sub_phase['houseTypes'], dict):
                # note this is for wildlings killing units etc.
                sp: SubPhaseWildlingsKillUnits = sub_phase
                game.multi_house_reaction.set_house_map(sp['houseTypes'])
                for h, num in sp['houseTypes'].items():
                    game.multi_house_reaction.react(
                        sp,
                        h,
                        lambda: PhaseReact.__act_on_house(game_id, game, h, phase_reaction_cls, sub_phase)
                    )
                pass
            else:
                raise RuntimeError("houseTypes should be list or dict.")

        else:
            warning = 'sub_phase has neither "houseTypes" nor "houseType". ' + str(sub_phase)
            logging.warning(warning)
            raise RuntimeWarning(warning)

    @staticmethod
    def __act_on_house(game_id: str, game: GameHandle, house_name: str, phase_cls: Type[BasePhaseReaction], sub_phase: SubPhase):
        try:
            house = HouseType[house_name.upper()]
            reaction_handler = phase_cls(game_id, house, game.state, PhaseReact.game_data.game_rules, sub_phase)
            try:
                json_like_arr = reaction_handler.get_actions()
                for m in json_like_arr:
                    PhaseReact.redis.send(game.worker + ".game" + game_id, json.dumps(m))
                if final_move := reaction_handler.finalizing_move_json(game_id):
                    PhaseReact.redis.send(game.worker + ".game" + game_id,
                                          json.dumps(final_move))
            except NoReplyNeedException:
                pass
        except Exception as e:
            print_file_lineno_error(e)