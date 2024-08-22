from server_module.game_state.military_unit import MilitaryUnit


def subtract_army(from_army: list[MilitaryUnit], army_to_subtract: list[MilitaryUnit]) -> list[MilitaryUnit]:
    for smu in army_to_subtract:
        for i, fmu in enumerate(army_to_subtract):
            if fmu.house == smu.house and fmu.unit_type == smu.unit_type and fmu.is_defeated == smu.is_defeated:
                army_to_subtract.pop(i)
                break
    return army_to_subtract