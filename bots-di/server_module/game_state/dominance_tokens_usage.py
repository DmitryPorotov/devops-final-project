from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


class DominanceTokensUsage(dict[str, bool]):
    # valyrianSword: bool
    # messengerRaven: bool
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

    def compare(self, other: "DominanceTokensUsage") -> bool:
        if self['valyrianSword'] != other['valyrianSword']:
            raise StateDiscrepancyException("valyrianSwords are not equal local {} other {}".format(self['valyrianSword'], other['valyrianSword']))
        if self['messengerRaven'] != other['messengerRaven']:
            raise StateDiscrepancyException("messengerRavens are not equal local {} other {}".format(self['messengerRaven'], other['messengerRaven']))
        return True