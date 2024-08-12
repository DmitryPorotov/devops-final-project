from typing import TypedDict, Optional
from DTO.phases.all_phases import SubPhase
from server_module.game_state.game_state import GameState, GameRules


class PlayerInputting(TypedDict):
    userId: int
    forHouses: list[str]


class Player(TypedDict):
    userId: int
    house: str
    name: str


class GameSettings(TypedDict):
    gameId: str
    gameUuid: str
    ownerId: int
    isInputOnly: bool
    isRandomHouses: bool
    isRandomEventsServerSide: bool
    players: Optional[list[Player]]
    playersInputting: Optional[list[PlayerInputting]]


class StatusDetails(TypedDict):
    roundCounter: int
    gameSettings: GameSettings
    subPhase: SubPhase


class ReplyJoinGame(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str
    userId: int
    gameSettings: GameSettings


class GameStatus(TypedDict):
    created: bool
    details: StatusDetails


class ReplyTestConnectivity(TypedDict):
    type: str
    action: str


class ReplySaveGame(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str
    userId: int
    saveName: str


class ReplyListSaves(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str
    userId: int
    saves: list[str]


class ReplyGetStatus(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str
    userId: int
    status: GameStatus


class ReplyGameAction(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str
    reply: list[dict]


class ReplyCreateGame(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str


class ReplyGetGameState(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str
    userId: int
    gameRules: GameRules
    gameState: GameState


class ReplyNewGame(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str
    gamesCount: int


class ReplyLoadGame(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str


class ReplyError(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str
    userId: int
    message: str
    originalMessage: Message


class ReplyStartGame(TypedDict):
    type: str
    action: str
    gameId: str
    messageId: str



