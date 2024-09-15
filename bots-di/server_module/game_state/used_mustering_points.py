from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


class UsedMusteringPoints(dict[str, int]):
    def compare(self, other: "UsedMusteringPoints") -> bool:
        for tn, used in self.items():
            if tn not in other:
                raise StateDiscrepancyException("UsedMusteringPoints local tile number {} is not in other".format(tn))
            if used != other[tn]:
                raise StateDiscrepancyException("UsedMusteringPoints local tile number {} is not equal to other".format(tn))
        for tn, used in other.items():
            if tn not in self:
                raise StateDiscrepancyException("UsedMusteringPoints other tile number {} is not in local".format(tn))
        return True
