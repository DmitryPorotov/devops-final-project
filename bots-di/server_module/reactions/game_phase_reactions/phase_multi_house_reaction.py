import json
import logging
from typing import Optional, Type

from dependency_injector.wiring import Provide, inject

from DTO.phases.all_phases import SubPhase
from containers_module import App
from redis_service import RedisConnector
from server_module.game_state.house_type import HouseType
from server_module.games_data_service import GamesDataService, GameHandle
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class MultiHouseReact:
    game_data: Optional[GamesDataService] = None
    redis: Optional[RedisConnector] = None

    @staticmethod
    @inject
    def init(game_data: GamesDataService = Provide[App.game_manager],
             redis: RedisConnector = Provide[App.redis_service]):
        MultiHouseReact.game_data = game_data
        MultiHouseReact.redis = redis
        MultiHouseReact.logger = logging.getLogger(
            f"{__name__}.{MultiHouseReact.__class__.__name__}",
        )


    @staticmethod
    def react(phase_cls: Type[BasePhaseReaction],
              game_id: str,
              sub_phase: SubPhase,
              ):
        game = MultiHouseReact.game_data.get_game(game_id)
        if 'houseTypes' in sub_phase:
            for h in sub_phase['houseTypes']:
                if h in game.houses:
                    MultiHouseReact.__act_on_house(game_id, game, h, phase_cls)
        elif 'houseType' in sub_phase:
            if sub_phase['houseType'] in game.houses:
                MultiHouseReact.__act_on_house(game_id, game, sub_phase['houseType'], phase_cls)
        else:
            warning = 'sub_phase has neither "houseTypes" nor "houseType".'
            logging.warning(warning)
            raise RuntimeWarning(warning)

    @staticmethod
    def __act_on_house(game_id: str, game: GameHandle, h: str, phase_cls: Type[BasePhaseReaction]):
        reaction_handler = phase_cls(game_id, HouseType[h.upper()], game.state, MultiHouseReact.game_data.game_rules)
        json_like_arr = reaction_handler.get_actions()
        for m in json_like_arr:
            MultiHouseReact.redis.send(game.worker + ".game" + game_id, json.dumps(m))
        if final_move := reaction_handler.finalizing_move_json(game_id):
            MultiHouseReact.redis.send(game.worker + ".game" + game_id,
                                       json.dumps(final_move))