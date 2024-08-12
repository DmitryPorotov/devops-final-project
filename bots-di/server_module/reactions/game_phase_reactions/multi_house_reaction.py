import json
from typing import Optional

from dependency_injector.wiring import Provide, inject

from DTO.phases.all_phases import SubPhase
from containers_module import App
from redis_service import RedisConnector
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.track_type import TrackType
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_phase_reactions.planning.add_order_reaction import AddOrderReaction
from server_module.reactions.management.house_to_bot_id import HouseToBotId


class MultiHouseReact:
    game_data: Optional[GamesDataService] = None
    redis: Optional[RedisConnector] = None

    @staticmethod
    @inject
    def init(game_data: GamesDataService = Provide[App.game_manager],
             redis: RedisConnector = Provide[App.redis_service]):
        MultiHouseReact.game_data = game_data
        MultiHouseReact.redis = redis

    def react(self,
              phase_cls: type,
              game_id: str,
              sub_phase: SubPhase,
              ):
        game = MultiHouseReact.game_data.get_game(game_id)
        for h in sub_phase['houseTypes']:
            if h in game.houses:
                bot_id = HouseToBotId[h.upper()].value
                total_stars = MultiHouseReact.game_data.game_rules.get_total_num_stars_by_court_position(
                    game.state.tracks[TrackType('court')].index(h)
                )
                reaction_handler = phase_cls(h, game.state, total_stars)
                moves = reaction_handler.get_moves()
                json_like_arr = reaction_handler.to_json(bot_id, game_id, moves)
                for m in json_like_arr:
                    MultiHouseReact.redis.send(game.worker + ".game" + game_id, json.dumps(m))
                MultiHouseReact.redis.send(game.worker + ".game" + game_id, json.dumps({
                    'userId': bot_id,
                    'gameId': game_id,
                    'action': 'game_action',
                    'player_action': {
                        'actionType': 'openOrders',
                        'houseType': h,
                    }
                }))
