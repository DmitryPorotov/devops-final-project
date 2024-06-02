package fwc.game

class FWCException (message: String) extends RuntimeException(message) {
  private var _gameId: String = null
  private var _userId: Int = -1;
  def gameId: String = _gameId
  def userId: Int = _userId
  def this(message: String, cause: Throwable) = {
    this(message)
    initCause(cause)
  }
  
  def this(message: String, gameId: String = null, userId: Int = -1) = {
    this(message)
    this._gameId = gameId
    this._userId = userId
  }

  def this(message: String, cause: Throwable, gameId: String, userId: Int) = {
    this(message, cause)
    this._gameId = gameId
    this._userId = userId
  }

  def this(cause: Throwable) = {
    this(Option(cause).map(_.toString).orNull, cause)
  }

  def this() = {
    this(null: String)
  }
}