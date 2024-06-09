package fwc.game

class FWCException (message: String) extends RuntimeException(message) {
  private var _gameId: String = null
  private var _userId: Int = -1;
  private var _messageId: String = null;
  def gameId: String = _gameId
  def userId: Int = _userId
  def messageId: String = _messageId
  def this(message: String, cause: Throwable) = {
    this(message)
    initCause(cause)
  }
  
  def this(message: String, gameId: String = null, userId: Int = -1) = {
    this(message)
    this._gameId = gameId
    this._userId = userId
  }

  def this(message: String, gameId: String, messageId: String) = {
    this(message)
    this._gameId = gameId
    this._messageId = messageId
  }

  def this(message: String, cause: Throwable, gameId: String, userId: Int) = {
    this(message, cause)
    this._gameId = gameId
    this._userId = userId
  }

  def this(message: String, gameId: String, userId: Int, messageId: String) = {
    this(message, gameId, userId)
    this._messageId = messageId
  }

  def this(cause: Throwable) = {
    this(Option(cause).map(_.toString).orNull, cause)
  }

  def this() = {
    this(null: String)
  }
}