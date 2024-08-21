package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.{MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseResolveHouseCard
import fwc.game.planningPhase.OrderType
import ujson.Value
import enrichment.ExtSeq
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}

case class ActionResolveCardMoose2(
                                    gameState: GameState,
                                    houseType: HouseType,
                                    tileNumber: TileNumber
                                  ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat) = CardResolve.validateAndGetCombat(gameState.subPhase, houseType, gameState.combat)

    if !gameState.combat.winnerCard.exists(_.isMoose2)
    then throw new ActionException("This phase is for moose 2")

    if tileNumber < 0 then return gameState.copy(
      subPhase = NextOrderFinder.nextSubPhase(gameState, OrderType.March, updatedCombat.attackerHouse),
      combat = null
    )

    if  updatedCombat.defenderTileNum != tileNumber ||
      {
        if isAttackerAction
        then !updatedCombat.attackerSupport.contains(tileNumber)
        else !updatedCombat.defenderSupport.contains(tileNumber)
      }
      || !gameState.armies.exists(
        (ta: (TileNumber, Seq[MilitaryUnit])) =>
          ta._1 == tileNumber && ta._2.exists(mu => mu.unitType == MilitaryUnitType.Footmen && mu.house == HouseType.Moose)
      )
    then throw new ActionException(s"There is no footman to upgrade at tile number $tileNumber")

    val footmen = gameState.armies(tileNumber).find(_.unitType == MilitaryUnitType.Footmen).head

    val army: Seq[MilitaryUnit] = gameState.armies(tileNumber).deleteFirstMatch(footmen) :+ MilitaryUnit(
      HouseType.Moose,
      MilitaryUnitType.Knights
    )

    val updatedArmies = gameState.armies + (tileNumber -> army)

    gameState.copy(
      subPhase = NextOrderFinder.nextSubPhase(gameState, OrderType.March, updatedCombat.attackerHouse),
      armies = updatedArmies,
      combat = null,
      discardedHouseCards = gameState.discardedHouseCards.resetDecksAfterCombat(updatedCombat)
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardMoose2",
    "houseType" -> houseType.toString,
    "tileNumber" -> tileNumber
  )
}

object ActionResolveCardMoose2 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardMoose2 =
    ActionResolveCardMoose2(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt
    )
}
