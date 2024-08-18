from typing import TypedDict, TypeVar, Generic, NotRequired

from DTO.messages.reply import Reply

_T = TypeVar('_T')

class Message(TypedDict):
    userId: int
    gameId: str
    messageId: str
    type: str
    action: str


class MessageGameAction(TypedDict, Generic[_T]):
    userId: int
    gameId: str
    messageId: str
    type: str
    action: str
    player_action: NotRequired[_T]
    reply: NotRequired[list[Reply[_T]]]

