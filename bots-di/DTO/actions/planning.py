from typing import TypedDict, NotRequired
from server_module.game_state.military_unit import HouseType
from server_module.game_state.order import Order


class ActionRemoveOrder(TypedDict):
    actionType: str  # = "removeOrder"
    houseType: HouseType
    tileNumber: int


class ActionRavenChooseChangeOrderOrLookAtWildlingCard(TypedDict):
    actionType: str  # = "ravenChooseChangeOrderOrLookAtWildlingCard"
    houseType: HouseType
    ravenChoice: str


class ActionRavenChoosePutWildlingsCardOnTopOrBottom(TypedDict):
    actionType: str  # = "ravenChoosePutWildlingsCardOnTopOrBottom"
    houseType: HouseType
    isPutOnTop: bool


class ActionAddOrder(TypedDict):
    actionType: str  # = "addOrder"
    order: Order
    houseType: HouseType
    tileNumber: int


class ActionRavenChangeOrder(TypedDict):
    actionType: str  # = "ravenChangeOrder"
    order: Order
    houseType: HouseType
    tileNumber: int


class ActionRavenGetWildlingsCard(TypedDict):
    actionType: str  # = "ravenGetWildlingsCard"
    isRandom: bool


class ActionOpenOrders(TypedDict):
    actionType: str  # = "openOrders"
    houseType: HouseType
    orders: NotRequired[dict[str, dict[str, Order]]]
