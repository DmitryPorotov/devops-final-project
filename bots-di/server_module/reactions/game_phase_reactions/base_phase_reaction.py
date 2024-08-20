import logging
import uuid
from typing import Optional

from DTO.actions.all_actions import Action
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.management.house_to_bot_id import HouseToBotId


class BasePhaseReaction:
    def __init__(
            self,
            game_id: str,
            house_type: HouseType,
            game_state: GameState,
            game_rules: GameRules,
            phase: SubPhase,
    ):
        self._game_id = game_id
        self._game_state = game_state
        self._game_rules = game_rules
        self._house_type = house_type
        self._phase = phase
        self._bot_id = HouseToBotId.get_bot_id_by_house(self._house_type)
        self.logger = logging.getLogger(
            f"{__name__}.{self.__class__.__name__}",
        )

    def get_actions(self) -> list[MessageGameAction[Action]]:
        pass
    
    def finalizing_move_json(self, game_id) -> Optional[MessageGameAction]:
        return None

    def _to_json(self, **kwargs) -> list[MessageGameAction] | MessageGameAction:
        message: MessageGameAction = {
            'userId': self._bot_id,
            'gameId': self._game_id,
            'messageId': str(uuid.uuid4()),
            'action': 'game_action',
            'type': 'action'
        }
        return message