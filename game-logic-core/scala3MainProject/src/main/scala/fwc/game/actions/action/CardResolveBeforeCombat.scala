package fwc.game.actions.action

import fwc.game.actionPhase.Combat
import fwc.game.actions.ActionException
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.actionSubPhases.{SubPhaseResolveHouseCard, SubPhaseGetTidesOfBattleCards}

object CardResolveBeforeCombat {
  def validateAndGetCombatAndCardPhase(
                                        subPhase: SubPhase,
                                        houseType: HouseType,
                                        combat: Combat,
                                        krakenTokens: Int
                                      ): (Boolean, Combat, SubPhase) = {
    val (isAttackerAction_, updatedCombat) = CardResolve.validateAndGetCombat(subPhase, houseType, combat)

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
        else SubPhaseGetTidesOfBattleCards(Seq(combat.attackerHouse, combat.defenderHouse))
      }
      else SubPhaseGetTidesOfBattleCards(Seq(combat.attackerHouse, combat.defenderHouse))

    (isAttackerAction_, updatedCombat, cardPhase)
  }

}
