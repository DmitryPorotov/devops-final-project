package fwc.gameSaving.actions.action.ActionResolveMarchOrderTests

import fwc.game.GameState
import fwc.game.board.{MilitaryUnit, MilitaryUnitType}
import fwc.game.eventsPhase.Supplies
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseLeavePowerTokenAtTile, SubPhaseResolveConsolidatePowerOrder}
import fwc.gameLoading
import fwc.gameSaving.actions.action.ActionResolveMarchOrder
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class MoveUnitsAndRemoveEnemyPowerTokenSpec extends AnyFlatSpec with should.Matchers {

  private val initState = {
    val json = gameLoading.readJson("saves/forUnitTests/ResolveMarchNoCombat.json")
    GameState.fromJson(json)
  }

  "ActionResolveMarchOrder" should "be able to move units and remove enemy power token" in {
    val footman = MilitaryUnit(HouseType.PufferFish, MilitaryUnitType.Footmen)
    val updatedState = ActionResolveMarchOrder(initState, HouseType.PufferFish, 54, Map(
      52 -> Seq(footman),
      53 -> Seq(footman)
    )).doAction()

    assert(updatedState.armies(52).contains(footman))
    assert(!updatedState.armies(52).contains(MilitaryUnit(
      HouseType.Rose,
      MilitaryUnitType.PowerToken
    )))
    assert(updatedState.armies(53).contains(footman))
    assert(updatedState.armies(54).contains(MilitaryUnit(
      HouseType.PufferFish,
      MilitaryUnitType.Knights
    )))

    assert(updatedState.subPhase.isInstanceOf[SubPhaseResolveConsolidatePowerOrder])
  }

  "ActionResolveMarchOrder" should "be able to ask to leave a power token on empty tile" in {
    val footman = MilitaryUnit(HouseType.PufferFish, MilitaryUnitType.Footmen)
    val updatedState = ActionResolveMarchOrder(initState, HouseType.PufferFish, 54, Map(
      53 -> Seq(footman, footman, MilitaryUnit(HouseType.PufferFish, MilitaryUnitType.Knights))
    )).doAction()

    assert(updatedState.combat == null)

    assert(updatedState.subPhase.isInstanceOf[SubPhaseLeavePowerTokenAtTile])
  }
}
