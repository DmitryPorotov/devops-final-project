package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.{TrackType, Tracks}
import fwc.game.houses.{HouseKraken, HousePufferfish, HouseType}
import fwc.game.phases.actionSubPhases.SubPhaseResolveHouseCard
import fwc.gameLoading.HouseCard
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveCardPufferfish0(
                                         gameState: GameState,
                                         houseType: HouseType,
                                         trackType: TrackType
                                       ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat, updatedPhase) = CardResolveBeforeCombat.validateAndGetCombatAndCardPhase(
      gameState.subPhase, 
      houseType, 
      gameState.combat,
      gameState.powerTokens(HouseKraken)
    )
    
    val updatedTracks =
      if isAttackerAction
      then
        if isPufferFishCard0(gameState.combat.attackerCard)
        then updateTracks(gameState.combat.defenderHouse)
        else throw new ActionException(s"Attacker has no card ${gameState.combat.attackerCard.name}")
      else if isPufferFishCard0(gameState.combat.defenderCard)
      then updateTracks(gameState.combat.attackerHouse)
      else throw new ActionException(s"Defender has no card ${gameState.combat.defenderCard.name}")

    gameState.copy(
      subPhase = updatedPhase,
      tracks = updatedTracks,
      combat = updatedCombat
    )
  }

  private def isPufferFishCard0(houseCard: HouseCard): Boolean =
    houseCard.house == HousePufferfish && houseCard.code == 0

  def updateTracks(houseType: HouseType): Tracks = {
    gameState.tracks +
      (trackType -> (gameState.tracks(trackType).filter(_ != houseType) :+ houseType))
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardPufferfish0",
    "houseType" -> houseType.toString,
    "trackType" -> trackType.toString
  )
}

object ActionResolveCardPufferfish0 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardPufferfish0 =
    ActionResolveCardPufferfish0(
      gameState,
      HouseType.fromString(json("houseType").str),
      TrackType.fromString(json("trackType").str)
    )
}