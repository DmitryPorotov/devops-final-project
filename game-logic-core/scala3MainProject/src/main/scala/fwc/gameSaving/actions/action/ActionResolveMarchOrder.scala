package fwc.gameSaving.actions.action

import fwc.{JsonSerializable, game}
import enrichment.ExtSeq
import fwc.game.{GameState, gameRules}
import fwc.game.actionPhase.Combat
import fwc.game.board.*
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseLeavePowerTokenAtTile, SubPhaseResolveMarchOrder, SubPhaseResolveSupportOrder}
import fwc.game.planningPhase.OrderType
import fwc.gameLoading.BoardTileType
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Try

case class ActionResolveMarchOrder(
                                    gameState: GameState,
                                    houseType: HouseType,
                                    sourceTileNumber: TileNumber,
                                    targets: Map[Int, Seq[MilitaryUnit]]
                                  ) extends Action(gameState)
  with PlayerAction(houseType)
  with MarchRetreatTrait(gameState, houseType)
  with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseResolveMarchOrder]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseResolveMarchOrder].houseType != houseType
    then throw new ActionException("Wrong house")

    val sourceOrderOpt = gameState.placedOrders.getOrderByTileNumber(sourceTileNumber)
    val sourceOrder = if sourceOrderOpt.isEmpty
      || sourceOrderOpt.head._2.orderType != OrderType.March
      || sourceOrderOpt.head._1 != houseType
    then throw new ActionException(s"There is no march order of house \"$houseType\" in the source tile")
    else sourceOrderOpt.head._2
    
    if null == targets || targets.isEmpty then
      val updatedGameState = gameState.copy(
        placedOrders = gameState.placedOrders.removeOrder(houseType, sourceTileNumber)
      )
      return updatedGameState.copy(
        subPhase = NextOrderFinder.nextSubPhase(updatedGameState, OrderType.March, houseType)
      )

    validateTargetTiles(sourceTileNumber, targets)

    val targetTileNumbers: Seq[Int] = targets.map((t, _) => t).toSeq
    val enemyArmiesAtTargets: Map[Int, Seq[MilitaryUnit]] = gameState.armies.filter(
      (tileNum, armies: Seq[MilitaryUnit]) =>
        targetTileNumbers.contains(tileNum)
          && armies.head.house != houseType
          && !(armies.size == 1 && armies.head.unitType == MilitaryUnitType.PowerToken)
    )

    if enemyArmiesAtTargets.size > 1
    then throw new ActionException("Can not start more then one combat with one march order")

    val hasAttackerPowerToken = gameState.powerTokens(houseType) > 0

    val gameStateOrderRemoved = gameState.copy(placedOrders = gameState.placedOrders.removeOrder(houseType, sourceTileNumber))
    if enemyArmiesAtTargets.isEmpty
    then
      val updatedArmies = moveMultipleArmies(gameStateOrderRemoved.armies, targets)
      val hasArmyLeftAtSourceTile = updatedArmies.contains(sourceTileNumber) || (gameRules.board(sourceTileNumber).homeOf == houseType)
      gameStateOrderRemoved.copy(
        subPhase =
          if hasAttackerPowerToken && !hasArmyLeftAtSourceTile && game.gameRules.board(sourceTileNumber).tileType == BoardTileType.Land
          then SubPhaseLeavePowerTokenAtTile(houseType, sourceTileNumber)
          else NextOrderFinder.nextSubPhase(gameStateOrderRemoved, OrderType.March, houseType)
        ,
        armies = updatedArmies
      )
    else {
      val tileNumberUnderAttack = enemyArmiesAtTargets.head._1
      val fightingArmy = targets(tileNumberUnderAttack)
      val notFightingArmies = targets - tileNumberUnderAttack
      val unitsLeftInAttackingTile = Armies.subtractArmies(gameStateOrderRemoved.armies(sourceTileNumber), fightingArmy)
      val hasNoArmyLeft = unitsLeftInAttackingTile.isEmpty
        && !(gameRules.board(sourceTileNumber).homeOf == houseType)
      val tmpArmies = moveMultipleArmies(gameStateOrderRemoved.armies, notFightingArmies) - tileNumberUnderAttack

      val gameStateNoPhase = gameStateOrderRemoved.copy(
        armies =
          if unitsLeftInAttackingTile.nonEmpty
          then tmpArmies + (sourceTileNumber -> unitsLeftInAttackingTile)
          else tmpArmies - sourceTileNumber
        ,
        combat = Combat(
          sourceTileNumber,
          houseType,
          fightingArmy,
          sourceOrder,
          null,
          false,
          null,
          Seq(),
          tileNumberUnderAttack,
          enemyArmiesAtTargets.head._2.head.house,
          enemyArmiesAtTargets.head._2,
          if gameState.placedOrders.getOrderByTileNumber(tileNumberUnderAttack).isEmpty
          then null else gameState.placedOrders.getOrderByTileNumber(tileNumberUnderAttack).get._2,
          null,
          false,
          null,
          Seq()
        )
      )

      if hasAttackerPowerToken && hasNoArmyLeft && game.gameRules.board(sourceTileNumber).tileType == BoardTileType.Land
      then gameStateNoPhase.copy(subPhase = SubPhaseLeavePowerTokenAtTile(houseType, sourceTileNumber))
      else
        try {
          val newPhase = CombatCommon.getNewSubPhaseForMarchSupport(
            gameStateOrderRemoved.placedOrders.getSupportOrdersForTile(tileNumberUnderAttack),
            gameStateOrderRemoved.tracks(TrackType.Throne),
            houseType,
            enemyArmiesAtTargets.head._2.head.house
          )
          gameStateNoPhase.copy(
            subPhase = newPhase
          )
        } catch {
          case _: AttackNeutralException =>
            CombatCommon.attackNeutrals(gameStateNoPhase)
          case e: Throwable => throw e
        }

    }

  }

  private def validateTargetTiles(sourceTileNumber: Int,
                                  targets: Map[Int, Seq[MilitaryUnit]]): Unit = {
    if gameRules.board(sourceTileNumber).tileType == BoardTileType.Land
    then targets.foreach((tn, mus: Seq[MilitaryUnit]) => {
      if gameRules.board(tn).tileType != BoardTileType.Land
      then throw new ActionException("All target board tiles should be on land")
      else if !hasPath(sourceTileNumber, tn)
      then throw new ActionException(s"There is no path from source tile ${gameRules.board(sourceTileNumber).name}" +
          s" to the target ${gameRules.board(tn).name}")
      mus.foreach(mu =>
        if !mu.unitType.canBeMustered
        then throw new ActionException(s"Can not march ${mu.unitType}")
      )
    }
    )
    else targets.foreach((tn, _) =>
      if gameRules.board(tn).tileType == BoardTileType.Land
      then throw new ActionException("All target board tiles should be on sea or in a port")
      else {
        val tile = gameRules.board(tn)
        if !tile.isNeighbourOf(sourceTileNumber)
        then throw new ActionException(s"There is no path from source tile ${gameRules.board(sourceTileNumber).name}" +
          s" to the target ${gameRules.board(tn).name}")
        if tile.tileType == BoardTileType.Port
        then
          val landConnectedToPort = tile.neighbourTiles.find(t => gameRules.board(t).tileType == BoardTileType.Land).head
          val armyOpt = gameState.armies.get(landConnectedToPort)
          if armyOpt.isEmpty && (gameRules.board(landConnectedToPort).homeOf != houseType)
          then throw new ActionException(s"Can not enter neutral port (${tile.name})")
          if armyOpt.head.nonEmpty && armyOpt.head.head.house != houseType
          then throw new ActionException(s"Can not enter enemy port (${tile.name})")
      }
    )
  }
  @tailrec
  private def moveMultipleArmies(armies: Armies, targets: Map[Int, Seq[MilitaryUnit]]): Armies = {
    if targets.isEmpty
    then armies
    else moveMultipleArmies(
      armies.moveArmy(
        houseType,
        sourceTileNumber,
        targets.head._2,
        targets.head._1,
        gameState.supplies
      ),
      targets.tail
    )
  }



  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveMarchOrder",
    "houseType" -> houseType.toString,
    "sourceTileNumber" -> sourceTileNumber,
    "targets" -> mutable.LinkedHashMap.from(
      targets.map((tn: Int, army: Seq[MilitaryUnit]) =>
        tn.toString -> ujson.Value(army.map(_.toJson))
      )
    )
  )
}

object ActionResolveMarchOrder extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveMarchOrder =
    ActionResolveMarchOrder(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("sourceTileNumber").num.toInt,
      Try(json("targets")
        .obj
        .map(
          (tn: String, army: ujson.Value) => tn.toInt -> army.arr.map(mu => MilitaryUnit.fromJson(mu)).toSeq
        ).toMap).getOrElse(null)
    )
}