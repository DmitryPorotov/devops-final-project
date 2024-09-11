package fwc.gameSaving

import fwc.game.GameState
import fwc.game.actions.Action

class DebuggingExceptionWrapper(
                               message: String,
                               actionNum: Int,
                               totalActions: Int,
                               action:Action,
                               oldState: GameState,
                               cause: Throwable,
                               ) extends RuntimeException(message) {

}
