from DTO.actions.action import ActionCalculateCombatOutcome
from DTO.messages.reply import Reply
from server_module.game_state.combat import Combat
from server_module.game_state.game_state import GameState
from server_module.game_state.house_card import HouseCard
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class CalculateCombatOutcomeReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionCalculateCombatOutcome]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        combat: Combat = Combat.from_json(self._reply['combat'])
        self._game_state.combat = combat

        self._game_state.discarded_house_cards.discard_card(combat.attacker_card)
        self._game_state.discarded_house_cards.discard_card(combat.defender_card)

        self._game_state.placed_orders.remove_order(combat.attacker_tile_num, combat.attacker_house)
        if combat.combat_outcome.winner == combat.attacker_house:
            self._game_state.placed_orders.remove_order(combat.defender_tile_num, combat.defender_house)

