package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.{MilitaryUnit, MilitaryUnitPowerToken, TileNumber, TrackThrone}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseLeavePowerTokenAtTile
import fwc.game.planningPhase.OrderMarch
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionLeavePowerTokenAtTile(
                                        gameState: GameState,
                                        houseType: HouseType,
                                        tileNumber: TileNumber,
                                        doLeave: Boolean
                                      ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseLeavePowerTokenAtTile]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseLeavePowerTokenAtTile].houseType != houseType
    then throw new ActionException("Wrong house")

    val updatedGameState =
      if doLeave
      then
        gameState.copy(
          armies = gameState.armies + (tileNumber -> Seq(MilitaryUnit(
            houseType,
            MilitaryUnitPowerToken
          ))),
          powerTokens = gameState.powerTokens + (houseType -> (gameState.powerTokens(houseType) - 1))
        )
      else gameState

    try {
      val updatedSubPhase =
        if gameState.combat == null
        then NextOrderFinder.nextSubPhase(gameState, OrderMarch, houseType)
        else CombatCommon.getNewSubPhaseForMarchSupport(
          updatedGameState.placedOrders.getSupportOrdersForTile(gameState.combat.defenderTileNum),
          gameState.tracks(TrackThrone),
          gameState.combat.defenderHouse
        )
      updatedGameState.copy(subPhase = updatedSubPhase)
    }
    catch {
      case _: AttackNeutralException =>
        CombatCommon.attackNeutrals(updatedGameState)
      case e: Throwable => throw e
    }
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "leavePowerTokenAtTile",
    "houseType" -> ujson.Str(houseType.toString),
    "tileNumber" -> ujson.Num(tileNumber),
    "doLeave" -> doLeave
  )
}

object ActionLeavePowerTokenAtTile extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionLeavePowerTokenAtTile =
    ActionLeavePowerTokenAtTile(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt,
      json("doLeave").bool
    )

}