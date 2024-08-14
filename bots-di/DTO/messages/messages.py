from typing import TypedDict, TypeVar, Generic, Optional

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
    player_action: Optional[_T]
    reply: Optional[list[Reply[_T]]]

