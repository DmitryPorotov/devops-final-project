from typing import TypedDict, Optional, NotRequired

from server_module.game_state.military_unit import HouseType, MilitaryUnit
from server_module.game_state.track_type import TrackType


class ActionResolveCardLion1(TypedDict):
    actionType: str  # = "resolveCardLion1"
    houseType: HouseType
    tileNumber: int


class ActionDisbandUnitsAfterCombat(TypedDict):
    actionType: str  # = "disbandUnitsAfterCombat"
    houseType: HouseType
    unit: MilitaryUnit


class ActionResolveCardMoose3(TypedDict):
    actionType: str  # = "resolveCardMoose3"
    houseType: HouseType
    cardCode: int


class ActionRetreatUnitsAfterBattle(TypedDict):
    actionType: str  # = "retreatUnitsAfterBattle"
    houseType: HouseType
    targetTileNumber: int


class ActionResolveCardWolf0(TypedDict):
    actionType: str  # = "resolveCardWolf0"
    houseType: HouseType
    targetTileNumber: int


class ActionResolveCardMoose2(TypedDict):
    actionType: str  # = "resolveCardMoose2"
    houseType: HouseType
    tileNumber: int


class ActionUseValyrianSteelBlade(TypedDict):
    actionType: str  # = "useValyrianSteelBlade"
    houseType: HouseType
    choice: str


class ActionRefreshTidesOfBattleDeck(TypedDict):
    actionType: str  # = "refreshTidesOfBattleDeck"
    newCards: list[int]

class AfterCombatStateJson(TypedDict):
    armies: dict
    discardedHouseCards: dict
    placedOrders: dict
    powerTokens: dict

class ActionCleanUpAfterCombat(TypedDict):
    actionType: str  # = "cleanUpAfterCombat"
    state: AfterCombatStateJson
    doCardResolve: bool


class ActionResolveCardKraken6(TypedDict):
    actionType: str  # = "resolveCardKraken6"
    houseType: HouseType
    newCardCode: int


class ActionCalculateGameWinner(TypedDict):
    actionType: str  # = "calculateGameWinner"


class ActionResolveCardRose4(TypedDict):
    actionType: str  # = "resolveCardRose4"
    houseType: HouseType
    tileNumber: int


class ActionChooseHouseCard(TypedDict):
    actionType: str  # = "chooseHouseCard"
    houseType: HouseType
    cardCode: int


class ActionResolveCardLion5(TypedDict):
    actionType: str  # = "resolveCardLion5"
    houseType: HouseType
    doCancelCard: bool


class ActionResolveSupportOrder(TypedDict):
    actionType: str  # = "resolveSupportOrder"
    fromHouseType: HouseType
    toHouseType: HouseType
    tileNumbers: list[int]


class ActionResolveCardPufferfish0(TypedDict):
    actionType: str  # = "resolveCardPufferfish0"
    houseType: HouseType
    trackType: TrackType


class ActionCalculateCombatOutcome(TypedDict):
    actionType: str  # = "calculateCombatOutcome"


class ActionResolveConsolidatePowerOrder(TypedDict):
    actionType: str  # = "resolveConsolidatePowerOrder"
    powerTokens: dict[HouseType | str, int]


class ActionGetTidesOfBattleCards(TypedDict):
    actionType: str  # = "getTidesOfBattleCards"
    isRandom: bool


class ActionCleanUpAfterRound(TypedDict):
    actionType: str  # = "cleanUpAfterRound"
    isRandom: bool
    round: int


class ActionResolveCardRose2(TypedDict):
    actionType: str  # = "resolveCardRose2"
    houseType: HouseType


class ActionResolveSpecialConsolidatePower(TypedDict):
    actionType: str  # = "resolveSpecialConsolidatePower"
    houseType: HouseType
    fromTile: int
    toTile: NotRequired[int]
    unitToMuster: NotRequired[MilitaryUnit]
    isUpgrade: bool


class ActionSetTidesOfBattleCards(TypedDict):
    actionType: str  # = "setTidesOfBattleCards"
    attackerCardCode: int
    defenderCardCode: int


class ActionKillUnitsAfterBattle(TypedDict):
    actionType: str  # = "killUnitsAfterBattle"
    houseType: HouseType
    units: list[MilitaryUnit]


class ActionLeavePowerTokenAtTile(TypedDict):
    actionType: str  # = "leavePowerTokenAtTile"
    houseType: HouseType
    doLeave: bool
    tileNumber: int


class ActionResolveMarchOrder(TypedDict):
    actionType: str  # = "resolveMarchOrder"
    houseType: HouseType
    sourceTileNumber: int
    targets: dict[int, list[MilitaryUnit]]


class ActionResolveRaidOrder(TypedDict):
    actionType: str  # = "resolveRaidOrder"
    houseType: HouseType
    sourceTileNumber: int
    targetTileNumber: int


class ActionChooseHouseCardAfterLion5(TypedDict):
    actionType: str  # = "chooseHouseCardAfterLion5"
    houseType: HouseType
    cardCode: int
