package fwc.game.eventsPhase.cards

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.{FWCException, gameRules}
import fwc.gameLoading.{CardTrait, RoundEventCard, TidesOfBattleCard, WildlingCard}
import ujson.Value

import scala.annotation.tailrec
import scala.collection.immutable.Seq
import scala.util.Random

case class BoardCards(
                       roundEvents1: Seq[RoundEventCard],
                       roundEvents2: Seq[RoundEventCard],
                       roundEvents3: Seq[RoundEventCard],
                       wildlings: Seq[WildlingCard],
                       tidesOfBattle: Seq[TidesOfBattleCard]
                     ) extends JsonSerializable {
  def toJson: ujson.Value = {
    def buildJsonArray(cards: Seq[CardTrait]): ujson.Value = ujson.Value(cards.map(r => ujson.Num(r.getCode)))

    ujson.Obj(
      "deck1" -> buildJsonArray(roundEvents1),
      "deck2" -> buildJsonArray(roundEvents2),
      "deck3" -> buildJsonArray(roundEvents3),
      "wildlings" -> buildJsonArray(wildlings),
      "tidesOfBattle" -> buildJsonArray(tidesOfBattle)
    )
  }
  
  def toRulesJson: ujson.Value = {
    def buildJsonArray(cards: Seq[JsonSerializable]): ujson.Value = ujson.Value(cards.map(r => r.toJson))
    ujson.Obj(
      "deck1" -> buildJsonArray(roundEvents1),
      "deck2" -> buildJsonArray(roundEvents2),
      "deck3" -> buildJsonArray(roundEvents3),
      "wildlings" -> buildJsonArray(wildlings),
      "tidesOfBattle" -> buildJsonArray(tidesOfBattle)
    )
  }

  def dequeueTidesOfBattleCard(): (TidesOfBattleCard, BoardCards) =
    if tidesOfBattle.isEmpty
    then throw new TidesOfBattleDeckEmptyException
    else (tidesOfBattle.head, copy(tidesOfBattle = tidesOfBattle.tail))
}

object BoardCards extends JsonParsable {
  def initialize(): BoardCards = {
    val deck1 = Random.shuffle(gameRules.boardCards.roundEvents1)
    val deck2 = Random.shuffle(gameRules.boardCards.roundEvents2)
    val deck3 = Random.shuffle(gameRules.boardCards.roundEvents3)
    val wild = Random.shuffle(gameRules.boardCards.wildlings)
    val tide = Random.shuffle(gameRules.boardCards.tidesOfBattle)

    BoardCards(deck1, deck2, deck3, wild, tide)
  }

  def initializeEmpty(): BoardCards = BoardCards(Seq(),Seq(),Seq(),Seq(),Seq())

  def initializeForRules(
                          eventCards: Seq[Seq[RoundEventCard]],
                          wildlingCards: Seq[WildlingCard],
                          tideOfBattleCards: Seq[TidesOfBattleCard]
                        ): BoardCards = {
    val deck1 = eventCards.head
    val deck2 = eventCards.tail.head
    val deck3 = eventCards.tail.tail.head
    val wild = wildlingCards
    val tide = tideOfBattleCards

    BoardCards(deck1, deck2, deck3, wild, tide)
  }

  override def fromJson(json: Value): BoardCards = {
    BoardCards(
      json.obj("deck1").arr.map(
        cc => gameRules.boardCards.roundEvents1.find(_.getCode == cc.num.toInt)
          .getOrElse(throw new FWCException("Round event card code of deck 1 is out of range"))
      ).toSeq,
      json.obj("deck2").arr.map(
        cc => gameRules.boardCards.roundEvents2.find(_.getCode == cc.num.toInt)
          .getOrElse(throw new FWCException("Round event card code of deck 2 is out of range"))
      ).toSeq,
      json.obj("deck3").arr.map(
        cc => gameRules.boardCards.roundEvents3.find(_.getCode == cc.num.toInt)
          .getOrElse(throw new FWCException("Round event card code of deck 3 is out of range"))
      ).toSeq,
      json.obj("wildlings").arr.map(
        cc => gameRules.boardCards.wildlings.find(_.getCode == cc.num.toInt)
          .getOrElse(throw new FWCException("Wildlings card code is out of range"))
      ).toSeq,
      json.obj("tidesOfBattle").arr.map(
        cc => gameRules.boardCards.tidesOfBattle.find(_.getCode == cc.num.toInt)
          .getOrElse(throw new FWCException("Tides of battle card code is out of range"))
      ).toSeq,
    )
  }
}
