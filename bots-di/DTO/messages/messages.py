from typing import TypedDict
from DTO.actions.all_actions import Action


class Message(TypedDict):
    userId: int
    gameId: str
    messageId: str
    type: str
    action: str


class MessageGameAction(TypedDict):
    userId: int
    gameId: str
    messageId: str
    type: str
    action: str
    game_action: Action

