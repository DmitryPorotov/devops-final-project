from server_module.game_rules.game_rules import GameRules
from server_module.game_state.armies import Armies
from server_module.game_state.house_type import HouseType
from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


class Supplies(dict[HouseType, int]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht in kwargs:
                self[HouseType[ht.upper()]] = kwargs[ht]

    def is_enough_supply(self, game_rules: GameRules, armies: Armies, house_type: HouseType) -> bool:
        armies_able_to_be_supplied: list[int] = game_rules.supply_usage[self[house_type]]
        my_armies = ((tn, ar) for tn, ar in armies.items() if len(ar) and ar[0].house == house_type)
        my_armies_numbers: dict[str, int] = {}
        for tn, ar in my_armies:
            for mu in ar:
                if mu.unit_type.get_mustering_points():
                    if tn not in my_armies_numbers:
                        my_armies_numbers[tn] = 0
                    my_armies_numbers[tn] += 1
        if len(armies_able_to_be_supplied) < len(my_armies_numbers):
            return False
        my_armies_sorted_tuples: list[tuple[str, int]] = []
        for tn, num_units in my_armies_numbers:
            my_armies_sorted_tuples.append((tn, num_units))
        my_armies_sorted_tuples.sort(key=lambda x: x[1], reverse=True)

        for i in range(len(my_armies_sorted_tuples)):
            if i not in armies_able_to_be_supplied:
                return True
            if armies_able_to_be_supplied[i] < my_armies_numbers[i]:
                return False
            return True

    def compare(self, other: "Supplies") -> bool:
        for ht, n in self.items():
            if n != other[ht]:
                raise StateDiscrepancyException("Supplies of {} in local {} are not equal to other {}".format(ht, n, other[ht]))
        return True