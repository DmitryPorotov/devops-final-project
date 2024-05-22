package fwc.gameSaving.actions.action

import fwc.game.actionPhase.Combat
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.actionSubPhases.{SubPhaseResolveHouseCard, SubPhaseGetTidesOfBattleCards}
import fwc.gameSaving.actions.ActionException

object CardResolveBeforeCombat {
  def validateAndGetCombatAndCardPhase(
                                        subPhase: SubPhase,
                                        houseType: HouseType,
                                        combat: Combat,
                                        krakenTokens: Int
                                      ): (Boolean, Combat, SubPhase) = {
    val tmp = CardResolve.validateAndGetCombat(subPhase, houseType, combat)

    val isAttackerAction_ = tmp._1
    val updatedCombat = tmp._2

    val cardPhase =
      if (isAttackerAction_ && !updatedCombat.defenderCardResolved) ||
        (!isAttackerAction_ && !updatedCombat.attackerCardResolved)
      then {
        val p =
          CombatCommon.getImmediatelyResolvableCardSubPhase(
            if isAttackerAction_ then updatedCombat.defenderCard else updatedCombat.attackerCard,
            krakenTokens
          )
        if p != null
        then p
        else SubPhaseGetTidesOfBattleCards()
      }
      else SubPhaseGetTidesOfBattleCards()

    (isAttackerAction_, updatedCombat, cardPhase)
  }

}
