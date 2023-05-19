package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.{MilitaryUnit, MilitaryUnitFootmen, MilitaryUnitKnights, TileNumber}
import fwc.game.houses.{HouseMoose, HouseType}
import fwc.game.phases.actionSubPhases.SubPhaseResolveHouseCard
import fwc.game.planningPhase.OrderMarch
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value
import enrichment.ExtSeq

case class ActionResolveCardMoose2(
                                    gameState: GameState,
                                    houseType: HouseType,
                                    tileNumber: TileNumber
                                  ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat) = CardResolve.validateAndGetCombat(gameState.subPhase, houseType, gameState.combat)

    if !gameState.combat.winnerCard.exists(_.isMoose2)
    then throw new ActionException("This phase is for moose 2")

    if  updatedCombat.defenderTileNum != tileNumber ||
      {
        if isAttackerAction
        then !updatedCombat.attackerSupport.contains(tileNumber)
        else !updatedCombat.defenderSupport.contains(tileNumber)
      }
      || !gameState.armies.exists(
        (ta: (TileNumber, Seq[MilitaryUnit])) =>
          ta._1 == tileNumber && ta._2.exists(mu => mu.unitType == MilitaryUnitFootmen && mu.house == HouseMoose)
      )
    then throw new ActionException(s"There is no footman to upgrade at tile number $tileNumber")

    val footmen = gameState.armies(tileNumber).find(_.unitType == MilitaryUnitFootmen).head

    val army: Seq[MilitaryUnit] = gameState.armies(tileNumber).deleteFirstMatch(footmen) :+ MilitaryUnit(
      HouseMoose,
      MilitaryUnitKnights
    )

    val updatedArmies = gameState.armies + (tileNumber -> army)

    val newPhase =
      if updatedCombat.loserCard.exists(_.isMoose3)
      then SubPhaseResolveHouseCard(HouseMoose, 3)
      else NextOrderFinder.nextSubPhase(gameState, OrderMarch, updatedCombat.attackerHouse)

    gameState.copy(
      subPhase = newPhase,
      armies = updatedArmies,
      combat = if newPhase.isInstanceOf[SubPhaseResolveHouseCard] then gameState.combat else null,
      discardedHouseCards =
        if newPhase.isInstanceOf[SubPhaseResolveHouseCard]
        then gameState.discardedHouseCards
        else gameState.discardedHouseCards.resetDecksAfterCombat(updatedCombat)
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
