from typing import TypedDict, Optional
from server_module.game_state.military_unit import HouseType
from server_module.game_state.track_type import TrackType


class SubPhaseSetEventCards(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "setEventCards"
    card1: Optional[int]
    card2: Optional[int]
    card3: Optional[int]


class SubPhaseWildlingsDiscardHouseCard(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsDiscardHouseCard"
    houseTypes: list[HouseType]


class SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "ravenChoosePutWildlingsCardOnTopOrBottom"
    houseType: HouseType


class SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsChooseKill2UnitsOr2PositionsOnTrack"
    houseType: HouseType


class SubPhaseWildlingsMusterAtCastle(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsMusterAtCastle"
    houseType: HouseType


class SubPhaseResolveConsolidatePowerOrder(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "resolveConsolidatePowerOrder"


class SubPhaseMuster(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "muster"
    houseType: HouseType


class SubPhaseDisbandUnit(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "disbandUnit"
    houseType: HouseType
    nextStep: str


class SubPhaseCalculateCombatOutcome(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "calculateCombatOutcome"


class SubPhaseCollectTaxes(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "collectTaxes"


class SubPhaseAwaitingStart(TypedDict):
    mainPhase: str  # = "phasePlanning"
    subPhase: str  # = "awaitingStart"


class SubPhaseRetreatUnitsAfterBattle(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "retreatUnitsAfterBattle"
    houseType: HouseType


class SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "ravenChooseChangeOrderOrLookAtWildlingCard"
    houseType: HouseType


class SubPhaseCleanUpAfterRound(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "cleanUpAfterRound"


class SubPhaseRecalculateSupplies(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "recalculateSupplies"


class SubPhaseRavenChangeOrder(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "ravenChangeOrder"
    houseType: HouseType


class SubPhaseResolveSupportOrder(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "resolveSupportOrder"
    houseType: HouseType
    tilesNumbers: list[int]


class SubPhaseWildlingsUpgradeKnights(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsUpgradeKnights"
    houseType: HouseType


class SubPhaseAutoKillUnitsAfterBattle(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "autoKillUnitsAfterBattle"


class SubPhaseGetEventCards(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "getEventCards"


class SubPhaseResolveTiesAfterBiddingOnWildlings(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "resolveTiesAfterBiddingOnWildlings"
    houseTypes: list[HouseType]
    isWinner: bool


class SubPhaseAutoRetreatAfterBattle(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "autoRetreatAfterBattle"


class SubPhaseChooseTracksBidsOrCollectTaxes(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "chooseTracksBidsOrCollectTaxes"
    houseType: HouseType


class SubPhaseCleanUpAfterCombat(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "cleanUpAfterCombat"


class SubPhaseLeavePowerTokenAtTile(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "leavePowerTokenAtTile"
    houseType: HouseType
    tileNumber: int


class SubPhaseCalculateGameWinner(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "calculateGameWinner"


class SubPhaseKillUnitsAfterBattle(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "killUnitsAfterBattle"
    houseType: HouseType


class SubPhaseResolveMarchOrder(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "resolveMarchOrder"
    houseType: HouseType


class SubPhaseResolveSpecialConsolidatePower(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "resolveSpecialConsolidatePower"
    houseType: HouseType


class SubPhaseChooseToUseValyrianSteelBlade(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "chooseToUseValyrianSteelBlade"
    houseType: HouseType


class SubPhaseResolveHouseCard(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "resolveHouseCard"
    houseType: HouseType
    cardCode: int


class SubPhaseChooseHouseCard(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "chooseHouseCard"
    houseTypes: list[HouseType]


class SubPhaseDisableOrder(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "disableOrder"
    orderType: str


class SubPhaseResolveRaidOrder(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "resolveRaidOrder"
    houseType: HouseType


class SubPhaseRefreshTidesOfBattleDeck(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "refreshTidesOfBattleDeck"


class SubPhaseChooseDisableMarchPlus1OrDefendOrders(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "chooseDisableMarchPlus1OrDefendOrders"
    houseType: HouseType


class SubPhaseSetWildlingsCards(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "setWildlingsCards"
    subPhaseWildlingsCard: SubPhaseWildlingsCard


class SubPhaseResolveTiesAfterBiddingOnTracks(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "resolveTiesAfterBiddingOnTracks"
    houseType: HouseType
    trackType: TrackType


class SubPhaseWildlingsBids(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsBids"
    houseTypes: list[HouseType]
    numberOfParticipants: int
    wildlingsStartedFrom12Points: bool


class SubPhaseReadyToOpenOrders(TypedDict):
    mainPhase: str  # = "phasePlanning"
    subPhase: str  # = "readyToOpenOrders"
    houseTypes: list[HouseType]


class SubPhaseTracksBids(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "tracksBids"
    houseTypes: list[HouseType]
    trackType: TrackType


class SubPhaseAddOrder(TypedDict):
    mainPhase: str  # = "phasePlanning"
    subPhase: str  # = "addOrder"
    houseTypes: list[HouseType]


class SubPhaseWildlingsKillUnits(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsKillUnits"
    houseTypes: obj<enum<HouseType>,int>
    loserHouse: str


class SubPhaseWildlingsDowngradeKnights(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsDowngradeKnights"
    houseTypes: obj<enum<HouseType>,int>


class SubPhaseSetTidesOfBattleCards(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "setTidesOfBattleCards"
    attackerCard: Optional[int]
    defenderCard: Optional[int]


class SubPhaseGetWildlingsCard(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "getWildlingsCard"
    subPhaseWildlingsCard: SubPhaseWildlingsCard


class SubPhaseChooseHouseCardAfterLion5(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "chooseHouseCardAfterLion5"
    houseType: HouseType
    bannedCardCode: int


class SubPhaseChooseUpdateSupplyOrMuster(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "chooseUpdateSupplyOrMuster"
    houseType: HouseType


class SubPhaseWildlingsChooseTrackToBeLastAt(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsChooseTrackToBeLastAt"
    houseTypes: list[HouseType]


class SubPhaseGetTidesOfBattleCards(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "getTidesOfBattleCards"


class SubPhaseRavenGetWildlingsCard(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "ravenGetWildlingsCard"


class SubPhaseWildlingsChooseTrackToBeFirstAt(TypedDict):
    mainPhase: str  # = "phaseRoundEvents"
    subPhase: str  # = "wildlingsChooseTrackToBeFirstAt"
    houseType: HouseType


class SubPhaseWildlingsCard(TypedDict):
    mainPhase: str  # = "phaseAction"
    subPhase: str  # = "wildlingsCard"
    houseTypes: list[HouseType]
    loserWinnerHouse: str
    cardCode: int
    isWin: bool
