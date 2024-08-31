package fwc.game.actions.action

import fwc.game.actionPhase.Combat
import fwc.game.actions.ActionException
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.actionSubPhases.{SubPhaseResolveCardRose2, SubPhaseResolveHouseCard}

object CardResolve {
  def validateAndGetCombat(
                                        subPhase: SubPhase,
                                        houseType: HouseType,
                                        combat: Combat
                                      ): (Boolean, Combat) = {
    if !subPhase.isInstanceOf[SubPhaseResolveHouseCard] && !subPhase.isInstanceOf[SubPhaseResolveCardRose2]
    then throw new ActionException("Wrong phase")

    subPhase match
      case card: SubPhaseResolveHouseCard if card.houseType != houseType => throw new ActionException("Wrong house")
      case card: SubPhaseResolveCardRose2 if card.houseType != houseType => throw new ActionException("Wrong house")
      case _ =>

    val isAttackerAction_ = isAttackerAction(houseType, combat)

    if isAttackerAction_ && combat.attackerCardResolved
    then throw new ActionException(s"Attacker house card was already resolved")

    if !isAttackerAction_ && combat.defenderCardResolved
    then throw new ActionException(s"Defender house card was already resolved")

    val updatedCombat =
      if isAttackerAction_
      then combat.copy(attackerCardResolved = true)
      else combat.copy(defenderCardResolved = true)

    (isAttackerAction_, updatedCombat)
  }

  def isAttackerAction(houseType: HouseType, combat: Combat): Boolean =
    if combat.attackerHouse == houseType
    then true
    else if combat.defenderHouse == houseType
    then false
    else throw new ActionException(s"$houseType does not participate in this combat")
}
