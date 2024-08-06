from typing import TypedDict, Optional
from server_module.game_state.military_unit import HouseType, MilitaryUnit


class ActionWildlingsChooseTrackToBeFirstAt(TypedDict):
    actionType: str  # = "wildlingsChooseTrackToBeFirstAt"
    houseType: HouseType
    track: str


class ActionCollectTaxes(TypedDict):
    actionType: str  # = "collectTaxes"


class ActionThroneChooseSupplyOrMuster(TypedDict):
    actionType: str  # = "throneChooseSupplyOrMuster"
    houseType: HouseType
    choice: str


class ActionGetEventCards(TypedDict):
    actionType: str  # = "getEventCards"
    isRandom: bool


class ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack(TypedDict):
    actionType: str  # = "wildlingsChooseKill2UnitsOr2PositionsOnTrack"
    houseType: HouseType
    track: Optional[str]


class ActionResolveTiesAfterBiddingOnTracks(TypedDict):
    actionType: str  # = "resolveTiesAfterBiddingOnTracks"
    houseType: HouseType
    resolution: list[str]


class ActionResolveTiesAfterBiddingOnWildlings(TypedDict):
    actionType: str  # = "resolveTiesAfterBiddingOnWildlings"
    houseType: HouseType
    winnerLoser: str


class ActionTrackBids(TypedDict):
    actionType: str  # = "trackBids"
    houseType: HouseType
    bid: int


class ActionWildlingsChooseTrackToBeLastAt(TypedDict):
    actionType: str  # = "wildlingsChooseTrackToBeLastAt"
    houseType: HouseType
    track: str


class ActionStopMustering(TypedDict):
    actionType: str  # = "stopMustering"
    houseType: HouseType


class ActionMuster(TypedDict):
    actionType: str  # = "muster"
    houseType: HouseType
    unitToMuster: MilitaryUnit
    fromTile: int
    toTile: Optional[int]


class ActionWildlingsBids(TypedDict):
    actionType: str  # = "wildlingsBids"
    houseType: HouseType
    bid: int


class ActionRavenChooseTrackBidsOrCollectTaxes(TypedDict):
    actionType: str  # = "ravenChooseTrackBidsOrCollectTaxes"
    houseType: HouseType
    choice: str


class ActionWildlingsCard(TypedDict):
    actionType: str  # = "wildlingsCard"


class ActionSteelBladeChooseDisableMarchOrDefend(TypedDict):
    actionType: str  # = "steelBladeChooseDisableMarchOrDefend"
    houseType: HouseType
    choice: str


class ActionRecalculateSupplies(TypedDict):
    actionType: str  # = "recalculateSupplies"


class ActionWildlingsDiscardHouseCard(TypedDict):
    actionType: str  # = "wildlingsDiscardHouseCard"
    houseType: HouseType
    cardCode: int


class ActionSetEventCards(TypedDict):
    actionType: str  # = "setEventCards"
    card1: int
    card2: int
    card3: int


class ActionWildlingsMusterAtCastle(TypedDict):
    actionType: str  # = "wildlingsMusterAtCastle"
    houseType: HouseType
    sourceTile: int
    targetUnits: list


class ActionWildlingsKillUnit(TypedDict):
    actionType: str  # = "wildlingsKillUnit"
    houseType: HouseType
    tileNumber: int
    unit: MilitaryUnit


class ActionSetWildlingsCard(TypedDict):
    actionType: str  # = "setWildlingsCard"
    wildlingsCardCode: int


class ActionDisbandUnitDueToSupplies(TypedDict):
    actionType: str  # = "disbandUnitDueToSupplies"
    houseType: HouseType
    tileNumber: int
    unit: MilitaryUnit
    nextStep: str


class ActionGetWildlingsCard(TypedDict):
    actionType: str  # = "getWildlingsCard"
    isRandom: bool


class ActionWildlingsReturnHouseCard(TypedDict):
    actionType: str  # = "wildlingsReturnHouseCard"
    houseType: HouseType


class ActionWildlingsDowngradeKnights(TypedDict):
    actionType: str  # = "wildlingsDowngradeKnights"
    houseType: HouseType
    tileNumber: int


class ActionWildlingsUpgradeKnights(TypedDict):
    actionType: str  # = "wildlingsUpgradeKnights"
    houseType: HouseType
    tileNumber1: int
    tileNumber2: Optional[int]


class ActionDisableOrder(TypedDict):
    actionType: str  # = "disableOrder"
    orderType: str
