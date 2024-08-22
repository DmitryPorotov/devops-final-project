package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.board.{MilitaryUnit, MilitaryUnitType, TileNumber, TrackType}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseLeavePowerTokenAtTile
import fwc.game.planningPhase.OrderType
import ujson.Value

case class ActionLeavePowerTokenAtTile(
                                        gameState: GameState,
                                        houseType: HouseType,
                                        doLeave: Boolean,
                                        tileNumber: TileNumber,
                                      ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseLeavePowerTokenAtTile]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseLeavePowerTokenAtTile]
    if currentPhase.houseType != houseType
    then throw new ActionException("Wrong house")

    val tileNumber = currentPhase.tileNumber

    val updatedGameState =
      if doLeave
      then
        gameState.copy(
          armies = gameState.armies + (tileNumber -> Seq(MilitaryUnit(
            houseType,
            MilitaryUnitType.PowerToken
          ))),
          powerTokens = gameState.powerTokens + (houseType -> (gameState.powerTokens(houseType) - 1))
        )
      else gameState

    try {
      val updatedSubPhase =
        if gameState.combat == null
        then NextOrderFinder.nextSubPhase(gameState, OrderType.March, houseType)
        else CombatCommon.getNewSubPhaseForMarchSupport(
          updatedGameState.placedOrders.getSupportOrdersForTile(gameState.combat.defenderTileNum),
          gameState.tracks(TrackType.Throne),
          gameState.combat.attackerHouse,
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
    "doLeave" -> doLeave,
    "tileNumber" -> tileNumber,
  )
}

object ActionLeavePowerTokenAtTile extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionLeavePowerTokenAtTile =
    ActionLeavePowerTokenAtTile(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("doLeave").bool,
      json("tileNumber").num.toInt,
    )

}
