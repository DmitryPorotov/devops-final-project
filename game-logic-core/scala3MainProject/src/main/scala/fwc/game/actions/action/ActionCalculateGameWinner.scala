package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.actions.{Action, ActionException, JsonParsableAction}
import fwc.game.board.TrackType
import fwc.game.{GameState, gameRules}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseCalculateGameWinner, SubPhaseGameEnd}
import ujson.Value

case class ActionCalculateGameWinner(
                                      gameState: GameState,
                                    ) extends Action(gameState) with JsonSerializable {
  extension (ht1: HouseType)
    def isHigherOnThroneTrackThan(ht2: HouseType): Boolean =
      ht1.isHigherOnTrack(gameState.tracks(TrackType.Throne))(ht2)

  override def doAction(): GameState = {
//    if !gameState.subPhase.isInstanceOf[SubPhaseCalculateGameWinner]
//    then throw new ActionException("Wrong phase")

    val numCastles = gameRules.board.filter(_.musteringPoints > 0)
      .foldLeft(Map[HouseType, Seq[Int]]())(
        (acc, cur) =>
          if gameState.armies.contains(cur.number)
          then
            val house = gameState.armies(cur.number).head.house
              acc + (house -> (acc.getOrElse(house, Seq()) :+ cur.musteringPoints))
          else if cur.homeOf != null
          then acc + (cur.homeOf -> (acc.getOrElse(cur.homeOf, Seq()) :+ cur.musteringPoints))
          else acc
      )

    val maxCastles = numCastles.foldLeft(0)((acc, cur) => if cur._2.size > acc then cur._2.size else acc)
    val housesWithMaxCastles = numCastles.filter(_._2.size == maxCastles)
    if housesWithMaxCastles.size == 1
    then gameState.copy(
      winner = Some(housesWithMaxCastles.head._1),
      subPhase = SubPhaseGameEnd(HouseType.getSeqOfAll)
    )
    else
      val maxMusteringPoints = housesWithMaxCastles.foldLeft(0)((acc, cur) => if cur._2.sum > acc then cur._2.sum else acc)
      val housesWithMaxStrongholds = housesWithMaxCastles.filter(_._2.sum == maxMusteringPoints)

      if housesWithMaxStrongholds.size == 1
      then gameState.copy(
        winner = Some(housesWithMaxStrongholds.head._1),
        subPhase = SubPhaseGameEnd(HouseType.getSeqOfAll)
      )
      else
        val suppliesOfHousesWithMaxStrongholds: Map[HouseType, Int] = gameState.supplies.filter(hs => housesWithMaxStrongholds.contains(hs._1))
        val maxSupplies = suppliesOfHousesWithMaxStrongholds.foldLeft(0)((acc, cur) => if cur._2 > acc then cur._2 else acc)
        val housesWithMaxSupplies = suppliesOfHousesWithMaxStrongholds.filter(_._2 == maxSupplies)
        if housesWithMaxSupplies.size == 1
        then gameState.copy(
          winner = Some(housesWithMaxSupplies.head._1),
          subPhase = SubPhaseGameEnd(HouseType.getSeqOfAll)
        )
        else
          val sortedByThrone = housesWithMaxSupplies.toSeq.sortWith((a, b)=> a._1 isHigherOnThroneTrackThan b._1)
          gameState.copy(
            winner = Some(sortedByThrone.head._1),
            subPhase = SubPhaseGameEnd(HouseType.getSeqOfAll)
          )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "calculateGameWinner",
  )
}

object ActionCalculateGameWinner extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionCalculateGameWinner =
    ActionCalculateGameWinner(
      gameState,
    )
}
