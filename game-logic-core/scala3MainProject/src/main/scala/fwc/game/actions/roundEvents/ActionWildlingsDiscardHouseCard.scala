package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.{CardCode, isValid}
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsDiscardHouseCard
import ujson.Value

case class ActionWildlingsDiscardHouseCard(
                                            gameState: GameState,
                                            houseType: HouseType,
                                            cardCode: CardCode,
                                          )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsDiscardHouseCard]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseWildlingsDiscardHouseCard]
    
    if !currentPhase.houseTypes.contains(houseType) 
    then throw new ActionException("Wrong house")
    
    if !cardCode.isValid
    then throw new ActionException("The card code is invalid")
    
    if gameState.discardedHouseCards(houseType).contains(cardCode)
    then throw new ActionException("The card is already discarded")
    
    val housesLeft = currentPhase.houseTypes.filter(_ != houseType)
    
    gameState.copy(
      subPhase =
        if housesLeft.nonEmpty
        then currentPhase.copy(houseTypes = housesLeft)
        else WildlingsCards.getNextNonWildlingsPhase(
          gameState.wildlingsStartedFrom12Points.head, 
          gameState.tracks, 
          gameState.boardCards,
          gameState.wildlingCounter,
        ),
      discardedHouseCards = gameState.discardedHouseCards + (houseType -> (gameState.discardedHouseCards(houseType) :+ cardCode)),
      wildlingsStartedFrom12Points =
        if housesLeft.nonEmpty
        then gameState.wildlingsStartedFrom12Points
        else None
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "wildlingsDiscardHouseCard",
    "houseType" -> houseType.toString,
    "cardCode" -> cardCode
  )
}

object ActionWildlingsDiscardHouseCard extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsDiscardHouseCard =
    ActionWildlingsDiscardHouseCard(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("cardCode").num.toInt
    )
}