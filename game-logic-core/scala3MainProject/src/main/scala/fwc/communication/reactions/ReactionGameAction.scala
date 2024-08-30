package fwc.communication.reactions

import fwc.{GameSettings, JsonSerializable, PlayerInputting}
import fwc.game.phases.*
import fwc.game.phases.actionSubPhases.*
import fwc.game.actions.action.*
import fwc.game.actions.{Action, ActionSetCard, PlayerAction}
import fwc.game.actions.planning.{ActionAddOrder, ActionOpenOrders, ActionRavenGetWildlingsCard}
import fwc.game.actions.roundEvents.*
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.{SubPhaseAddOrder, SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard, SubPhaseRavenGetWildlingsCard}
import fwc.game.phases.roundEventsSubPhases.*
import fwc.game.{FWCException, GameState, gameRules}
import fwc.gameSaving.GameReplay

import scala.util.boundary
import scala.annotation.tailrec
import scala.util.Random

object ReactionGameAction {
  def apply(userId: Int, gameReplay: GameReplay, gameAction: ujson.Value): (GameReplay, ujson.Value) = {
    val player = gameReplay.gameSettings.players.head.find(_.userId == userId)
    def findInputtingPlayer(players: Seq[PlayerInputting]): Option[PlayerInputting] = boundary {
      players.foldLeft(None: Option[PlayerInputting])(
        (acc, cur) =>
          if cur.userId == userId
          then boundary.break(Some(cur))
          else acc
      )
    }
    val inputtingPlayer =
      if gameReplay.gameSettings.isInputOnly then
        gameReplay.gameSettings.playersInputting.foldLeft(None: Option[PlayerInputting])(
          (acc, cur: Seq[PlayerInputting]) => findInputtingPlayer(cur)
        )
      else None
    val action = Action.fromJson(gameReplay.currentGameState, gameAction)

    //todo: handle set cards

    action match
      case a: PlayerAction =>

        if !gameReplay.gameSettings.isInputOnly then
          if player.isEmpty || player.head.house.head != a.getHouseType
          then throw new FWCException("House does not belong to player")
        else
          if inputtingPlayer.isEmpty || !inputtingPlayer.head.forHouses.contains(a.getHouseType)
            then throw new FWCException("The player does not input for this house")

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
  private def loop(action: Action, gameReplay: GameReplay, reply: ujson.Arr = ujson.Arr()): (GameReplay, ujson.Arr) = {
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

      case phasePassive: SubPhasePassive => loop(
        matchSubPhaseToAction(
          phasePassive,
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
      case _: SubPhaseAutoKillUnitsAfterBattle => ActionAutoKillUnitsAfterBattle(gameState)
      case _: SubPhaseAutoRetreatAfterBattle => ActionAutoRetreatAfterBattle(gameState)
      case _: SubPhaseCalculateGameWinner => ActionCalculateGameWinner(gameState)
      case _: SubPhaseCleanUpAfterCombat => ActionCleanUpAfterCombat(gameState)
      case _: SubPhaseGetEventCards => ActionGetEventCards(gameState, isRandom)
      case s: SubPhaseSetEventCards =>
        if s.card1.exists(_.code == 3) || s.card2.exists(_.code == 3)
        then ActionGetEventCards(gameState, isRandom)
        else ActionSetEventCards(gameState, s.card1.head, s.card2.head, s.card3.head)
      case _: SubPhaseGetTidesOfBattleCards => ActionGetTidesOfBattleCards(gameState, isRandom)
      case s: SubPhaseSetTidesOfBattleCards =>
        if s.defenderCard.isEmpty || s.attackerCard.isEmpty
        then ActionGetTidesOfBattleCards(gameState, isRandom)
        else ActionSetTidesOfBattleCards(gameState, s.attackerCard.head, s.defenderCard.head)
      case _: SubPhaseGetWildlingsCard => ActionGetWildlingsCard(gameState, isRandom)
      case s: SubPhaseSetWildlingsCard => ActionSetWildlingsCard(gameState, s.subPhaseWildlingsCard.cardCode)
      case _: SubPhaseRavenGetWildlingsCard => ActionRavenGetWildlingsCard(gameState, isRandom)
      case _: SubPhaseRefreshTidesOfBattleDeck => ActionRefreshTidesOfBattleDeck(gameState, Random.shuffle(gameRules.boardCards.tidesOfBattle))
      case _: SubPhaseResolveConsolidatePowerOrder => ActionResolveConsolidatePowerOrder(gameState)
      case _: SubPhaseCollectTaxes => ActionCollectTaxes(gameState)
      case s: SubPhaseDisableOrder => ActionDisableOrder(gameState, s.orderType)
      case _: SubPhaseCleanUpAfterRound => ActionCleanUpAfterRound(gameState, isRandom)
      case _: SubPhaseRecalculateSupplies => ActionRecalculateSupplies(gameState)
      case s: SubPhaseResolveCardRose2 => ActionResolveCardRose2(gameState, s.houseType)
      case s: SubPhaseOpenTrackBids => ActionOpenTrackBids(gameState, gameState.bids)
//      case _ => throw new RuntimeException("SubPhase " + subPhase + " has no matching action.")


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
        case a: ActionCalculateGameWinner => buildMessageToAll(
          a.toJson.obj.addOne(
            "winner" -> updatedGameState.winner.head.toString
          )
        )
        case a: ActionCleanUpAfterCombat => buildMessageToAll(
          a.toJson.obj.addOne(
            "state" -> ActionCleanUpAfterCombat.buildMessage(updatedGameState)
          )
        )
        case a: ActionCleanUpAfterRound => buildMessageToAll(
          a.toJson.obj.addAll(
            ActionCleanUpAfterRound.buildMessage(updatedGameState).obj
          )
        )
        case a: ActionGetTidesOfBattleCards =>
          val sp = updatedGameState.subPhase.asInstanceOf[SubPhaseSetTidesOfBattleCards]
          if sp.defenderCard.isEmpty
          then ujson.Obj(
            "to" -> findPlayerIdByHouse(updatedGameState.combat.attackerHouse),
            "player_action" -> a.toJson.obj.addAll(Map(
              "code" -> sp.attackerCard.head,
              "houseType" -> a.gameState.combat.attackerHouse.toString
            ))
          )
          else ujson.Obj(
            "to" -> findPlayerIdByHouse(updatedGameState.combat.defenderHouse),
            "player_action" -> a.toJson.obj.addAll(Map(
              "code" -> sp.defenderCard.head,
              "houseType" -> a.gameState.combat.defenderHouse.toString
            ))
          )
        case a: ActionRavenGetWildlingsCard =>
          if a.isRandom
          then ujson.Obj(
            "to" -> findPlayerIdByHouse(updatedGameState.tracks.ravenOwner),
            "player_action" -> a.toJson.obj.addOne(
              "code" -> updatedGameState.boardCards.wildlings.head.code
            )
          )
          else buildMessageToAll(a.toJson)
        case a: ActionOpenOrders =>
          val json = a.toJson
          if updatedGameState.subPhase.isInstanceOf[SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard]
          then
            json.obj.addOne("orders" -> updatedGameState.placedOrders.toJson)
          buildMessageToAll(json)
        case a: ActionRecalculateSupplies => 
          val json = a.toJson
          json.obj.addOne("supplies" -> updatedGameState.supplies.toJson)
          buildMessageToAll(json)
        case a: ActionTrackBids =>
          val json = a.toJson
          json.obj("bid") = -1
          buildMessageToAll(json)
        case a => buildMessageToAll(a.toJson)
    if updatedGameState.combat != null then
      val updatedCombat =
        if (updatedGameState.combat.attackerCard == null && updatedGameState.combat.defenderCard != null)
          || (updatedGameState.combat.attackerCard != null && updatedGameState.combat.defenderCard == null)
          then updatedGameState.combat.copy(attackerCard = null, defenderCard = null)
        else updatedGameState.combat
      reply.obj.addOne(
        "combat" -> updatedCombat.toNonEmptyFieldsJson
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
