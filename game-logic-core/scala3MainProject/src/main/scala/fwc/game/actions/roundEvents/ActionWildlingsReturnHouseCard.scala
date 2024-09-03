package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.{CardCode, isValid}
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsReturnHouseCard
import ujson.Value

import scala.util.Try

case class ActionWildlingsReturnHouseCard(
                                           gameState: GameState,
                                           houseType: HouseType,
                                           cardCode: Option[CardCode],
                                         ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsReturnHouseCard]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseWildlingsReturnHouseCard]

    if currentPhase.houseType != houseType
    then throw new ActionException("Wrong house")

    if cardCode.nonEmpty && !cardCode.head.isValid
    then throw new ActionException("The card code is invalid")

    val gameStateWithPhase = gameState.copy(
      subPhase = WildlingsCards.getNextNonWildlingsPhase(
        gameState.wildlingsStartedFrom12Points.head,
        gameState.tracks,
        gameState.boardCards,
        gameState.wildlingCounter,
      ),
      wildlingsStartedFrom12Points = None
    )

    if cardCode.isEmpty
    then gameStateWithPhase
    else
      if !gameState.discardedHouseCards(houseType).contains(cardCode)
      then throw new ActionException("The card is not discarded")

      val updatedDiscardedHouseCards = gameStateWithPhase.discardedHouseCards
        + (houseType -> gameStateWithPhase.discardedHouseCards(houseType).filter(_ != cardCode.head))

      gameStateWithPhase.copy(
        discardedHouseCards = updatedDiscardedHouseCards
      )
  }

  override def toJson: Value =
    val json = ujson.Obj(
      Action.actionTypeJsonKey -> "wildlingsReturnHouseCard",
      "houseType" -> houseType.toString,
    )
    if cardCode.nonEmpty
    then json.obj.addOne("cardCode" -> cardCode.head)
    json
}


object ActionWildlingsReturnHouseCard extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsReturnHouseCard =
    ActionWildlingsReturnHouseCard(
      gameState,
      HouseType.fromString(json("houseType").str),
      Try(json("cardCode").numOpt.map(_.toInt)).getOrElse(None)
    )
}