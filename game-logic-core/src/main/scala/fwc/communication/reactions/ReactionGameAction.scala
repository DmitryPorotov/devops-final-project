package fwc.communication.reactions

import fwc.{GameSettings, JsonSerializable}
import fwc.game.phases.*
import fwc.game.phases.actionSubPhases.*
import fwc.game.actionPhase.*
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.{SubPhaseAddOrder, SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard}
import fwc.game.phases.roundEventsSubPhases.*
import fwc.game.{FWCException, GameState, gameRules}
import fwc.gameSaving.GameReplay
import fwc.gameSaving.actions.action.*
import fwc.gameSaving.actions.planning.{ActionAddOrder, ActionOpenOrders, ActionRavenGetWildlingsCard}
import fwc.gameSaving.actions.roundEvents.*
import fwc.gameSaving.actions.{Action, ActionSetCard, PlayerAction}

import scala.annotation.tailrec
import scala.util.Random

object ReactionGameAction {
  def apply(userId: Int, gameReplay: GameReplay, gameAction: ujson.Value): (GameReplay, ujson.Value) = {
    val player = gameReplay.gameSettings.players.head.find(_.userId == userId)
    val action = Action.fromJson(gameReplay.currentGameState, gameAction)

    //todo: handle set cards

    action match
      case a: PlayerAction =>

        if player.isEmpty || player.head.house.head != a.getHouseType
        then throw new FWCException("House does not belong to player")

        loop(a, gameReplay)

      case a: ActionSetCard =>
        if !gameReplay.gameSettings.isRandomEventsServerSide
        then throw new FWCException("Cards are generated on the server side")

        if gameReplay.gameSettings.ownerId != userId
        then throw new FWCException("Only the game owner can set the cards")

        loop(a, gameReplay)

      case _ => ???

  }

  @tailrec
  private def loop(action: Action, gameReplay: GameReplay, reply: ujson.Arr = ujson.Arr()): (GameReplay, ujson.Arr)= {
    println(reply)
    val updatedGameState = action.doAction()
    val updatedGameReplay = gameReplay.copy(
      currentGameState = updatedGameState,
      actions = gameReplay.actions prepended action
    )
    val updatedReply = reply.arr.addOne(
      matchActionToReply(action.asInstanceOf[JsonSerializable], updatedGameState, updatedGameReplay.gameSettings)
    )
    updatedGameState.subPhase match {
      case phaseRandom: SubPhaseRandom =>
        if gameReplay.gameSettings.isRandomEventsServerSide
        then loop(
          matchSubPhaseToAction(
            phaseRandom,
            updatedGameState,
            gameReplay.gameSettings.isRandomEventsServerSide
          ),
          updatedGameReplay,
          updatedReply
        )
        else (updatedGameReplay, updatedReply)

      case phaseNoHouse: SubPhaseNoHouse => loop(
        matchSubPhaseToAction(
          phaseNoHouse,
          updatedGameState,
          gameReplay.gameSettings.isRandomEventsServerSide
        ),
        updatedGameReplay,
        updatedReply
      )

      case _ => (updatedGameReplay, updatedReply)
    }
  }

  private def matchSubPhaseToAction(subPhase: SubPhase, gameState: GameState, isRandom: Boolean = false): Action =
    subPhase match
      case _: SubPhaseCalculateCombatOutcome => ActionCalculateCombatOutcome(gameState)
      case _: SubPhaseCalculateGameWinner => ActionCalculateGameWinner(gameState)
      case _: SubPhaseCleanUpAfterCombat => ActionCleanUpAfterCombat(gameState)
      case _: SubPhaseGetEventCards => ActionGetEventCards(gameState, isRandom)
      case s: SubPhaseSetEventCards =>
        if s.card1.exists(_.code == 3) || s.card2.exists(_.code == 3)
        then ActionGetEventCards(gameState, isRandom)
        else ActionSetEventCards(gameState, s.card1.head, s.card2.head, s.card3.head)
      case _: SubPhaseGetTidesOfBattleCards => ActionGetTidesOfBattleCards(gameState, isRandom)
      case s: SubPhaseSetTidesOfBattleCards =>
        if s.defenderCard.isEmpty
        then ActionGetTidesOfBattleCards(gameState, isRandom)
        else ActionSetTidesOfBattleCards(gameState, s.attackerCard.head, s.defenderCard.head)
      case _: SubPhaseGetWildlingsCard => ActionGetWildlingsCard(gameState, isRandom)
      case s: SubPhaseSetWildlingsCard => ActionSetWildlingsCard(gameState, s.subPhaseWildlingsCard.cardCode)
      case _: SubPhaseRefreshTidesOfBattleDeck => ActionRefreshTidesOfBattleDeck(gameState, Random.shuffle(gameRules.boardCards.tidesOfBattle))
      case _: SubPhaseResolveConsolidatePowerOrder => ActionResolveConsolidatePowerOrder(gameState)
      case _: SubPhaseCollectTaxes => ActionCollectTaxes(gameState)
      case s: SubPhaseDisableOrder => ActionDisableOrder(gameState, s.orderType)


  private def matchActionToReply(
                                  previousAction: JsonSerializable,
                                  updatedGameState: GameState,
                                  gameSettings: GameSettings
                                ): ujson.Value = {
    def findPlayerIdByHouse(houseType: HouseType): Int = {
      if gameSettings.players.nonEmpty
      then
        val playerOpt = gameSettings.players.head.find(_.house.head.equals(houseType))
        if playerOpt.nonEmpty
        then return playerOpt.head.userId
        else if gameSettings.playersInputting.nonEmpty
        then
          val playerOpt = gameSettings.playersInputting.head.find(pi => pi.forHouses.contains(houseType))
          if playerOpt.nonEmpty
          then return playerOpt.head.userId

      throw new FWCException("House does not belong to a player. Game settings are corrupted.")
    }

    val reply =
      previousAction match
        case a: ActionAddOrder =>
          val json =
            if a.gameState.subPhase.isInstanceOf[SubPhaseAddOrder]
            then a.copy(order = null).toJson
            else a.toJson
  
          buildMessageToAll(json)
        case a: ActionChooseHouseCard => buildMessageToAll(a.copy(cardCode = -1).toJson)
//        case _: ActionCalculateCombatOutcome => buildMessageToAll(updatedGameState.combat.toJson)
        case _: ActionCalculateGameWinner => buildMessageToAll(
          ujson.Obj("winner" -> updatedGameState.winner.head.toString)
        )
        case _: ActionCleanUpAfterCombat => buildMessageToAll(ActionCleanUpAfterCombat.buildMessage(updatedGameState))
        case _: ActionCleanUpAfterRound => buildMessageToAll(ActionCleanUpAfterRound.buildMessage(updatedGameState))
        case a: ActionGetTidesOfBattleCards =>
          val sp = updatedGameState.subPhase.asInstanceOf[SubPhaseSetTidesOfBattleCards]
          if sp.defenderCard.isEmpty
          then ujson.Obj(
            "to" -> findPlayerIdByHouse(updatedGameState.combat.attackerHouse),
            "player_action" -> a.toJson
          )
          else ujson.Obj(
            "to" -> findPlayerIdByHouse(updatedGameState.combat.defenderHouse),
            "player_action" -> a.toJson
          )
        case a: ActionRavenGetWildlingsCard =>
          if a.isRandom
          then ujson.Obj(
            "to" -> findPlayerIdByHouse(updatedGameState.tracks.ravenOwner),
            "player_action" -> updatedGameState.boardCards.wildlings.head.toJson
          )
          else buildMessageToAll(a.toJson)
        case a: ActionOpenOrders =>
          val json = a.toJson
          if updatedGameState.subPhase.isInstanceOf[SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard]
          then
            json.obj.addOne("orders" -> updatedGameState.placedOrders.toJson)
          buildMessageToAll(json)
        case a => buildMessageToAll(a.toJson)
    if updatedGameState.combat != null then
      val updatedCombat =
        if (updatedGameState.combat.attackerCard == null && updatedGameState.combat.defenderCard != null)
          || (updatedGameState.combat.attackerCard != null && updatedGameState.combat.defenderCard == null)
          then updatedGameState.combat.copy(attackerCard = null, defenderCard = null)
        else updatedGameState.combat
      reply.obj.addOne(
        "combat" -> updatedGameState.combat.toJson
      )
    reply.obj.addOne(
      "current_phase" -> (updatedGameState.subPhase match {
        case subPhase: SubPhaseSetTidesOfBattleCards => subPhase.toCleanJson
        case s => s.toJson
      })
    )
  }
  private def buildMessageToAll(a: ujson.Value): ujson.Obj =
    ujson.Obj(
      "to" -> "*",
      "player_action" -> a
    )
}
