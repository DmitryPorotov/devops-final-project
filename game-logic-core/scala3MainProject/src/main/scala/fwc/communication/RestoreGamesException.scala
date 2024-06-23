package fwc.communication

class RestoreGamesException extends Exception {
  private final var _games: List[String] = null
  def games: List[String] = _games
  private final var _messageId: String = null
  def messageId: String = _messageId
  def this(games: List[String], messageId: String) =
    this()
    _messageId = messageId
    _games = games
}
