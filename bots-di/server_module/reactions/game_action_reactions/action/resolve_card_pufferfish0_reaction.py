from DTO.actions.action import ActionResolveCardPufferfish0
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveCardPufferfish0Reaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveCardPufferfish0]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveCardPufferfish0 = self._reply['player_action']
        combat = self._game_state.combat
        opponent = combat.attacker_house if combat.defender_house == HouseType.PUFFERFISH else combat.defender_house
        track = self._game_state.tracks[TrackType[pa['trackType'].upper()]]
        idx = track.index(opponent)
        track.pop(idx)
        track.append(opponent)
        self.logger.info(pa)