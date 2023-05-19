package fwc

import fwc.game.FWCException
import fwc.game.board.*
import fwc.game.houses.*
import fwc.game.planningPhase.*

import scala.io.Source.fromFile
import scala.util.{Failure, Success, Try}

package object gameLoading {
  def loadBoard(): Vector[BoardTile] = {
    val jsonData = readJson("textures/board.json")

    val board =
      (for (tile <- jsonData.arr)
        yield BoardTile(
          tile("number").num.toInt,
          BoardTileType.fromString(tile("tileType").str),
          tile("name").str,
          tile("neighbourTiles").arr.map(x => {
            x.num.toInt
          }).toSeq,
          Try[Int](tile("musteringPoints").num.toInt).getOrElse(0),
          Try[Int](tile("supplyPoints").num.toInt).getOrElse(0),
          Try[Int](tile("powerPoints").num.toInt).getOrElse(0),
          Try[HouseType](HouseType.fromString(tile("home").str)).getOrElse(null)
        )
        ).toVector

    board
  }

  def readJson(path: String): ujson.Value = {
    val source = fromFile(path)
    val lines = try source.mkString finally source.close
    ujson.read(lines)
  }

  def loadWildlingCards(): Seq[WildlingCard] = {
    val jsonData = readJson("textures/wildlings_cards.json")

    val cards =
      (for (card <- jsonData.arr)
        yield WildlingCard(
          card("code").num.toInt,
          card("title").str,
          card("wildlingVictory").obj("lowestBidder").str,
          card("wildlingVictory").obj("everyoneElse").str,
          card("playersVictory").str
        )
      ).toSeq

    cards
  }

  def loadTideOfBattleCards(): Seq[TidesOfBattleCard] = {
    val jsonData = readJson("textures/tides_of_battle_cards.json")

    val cards =
      (for (card <- jsonData.arr)
        yield List.fill(card("numOfCards").num.toInt)(TidesOfBattleCard(
          card("code").num.toInt,
          card("power").num.toInt,
          Try[Boolean](card("death").bool).getOrElse(false),
          Try[Boolean](card("attack").bool).getOrElse(false),
          Try[Boolean](card("defense").bool).getOrElse(false)
        ))
      )
        .flatten
        .toSeq

    cards
  }

  def loadHouseCards(): Seq[HouseCard] = {
    val jsonData = readJson("textures/house_cards.json")

    val cards =
      (
        for (
          house <- jsonData.obj;
          card <- house._2.arr
        )
          yield HouseCard(
            HouseType.fromString(house._1),
            card("code").num.toInt,
            card("name").str,
            card("strength").num.toInt,
            Try[String](card("text").str).getOrElse(""),
            Try[Int](card("attack").num.toInt).getOrElse(0),
            Try[Int](card("defense").num.toInt).getOrElse(0),
          )
      )
        .toSeq

    cards
  }

  def loadRoundEventCards(): Seq[Seq[RoundEventCard]] = {
    val jsonData = readJson("textures/round_events_cards.json")

    val cards = (
      for (
        deck <- jsonData.obj
      )
        yield deck._2.arr.flatMap(card => {
          List.fill(Try[Int](card("numOfCards").num.toInt).getOrElse(1))(RoundEventCard(
            card("code").num.toInt,
            card("title").str,
            card("text").str,
            if Try(card("hasWildling").bool).getOrElse(false) then 2 else 0
          ))
        }).toSeq
    )
      .toSeq

    cards
  }

  def loadBoardStart(): Seq[BoardStart] = {
    val jsonData = readJson("textures/board_start.json")

    val boardStart = (
      for (
        house <- jsonData.arr
      )
        yield BoardStart(
          HouseType.fromString(house("name").str),
          house("map").arr.map(tile => {
            BoardStartTile(
              tile("tileCode").num.toInt,
              Try[Int](tile("items").obj("ship").num.toInt).getOrElse(0),
              Try[Int](tile("items").obj("knight").num.toInt).getOrElse(0),
              Try[Int](tile("items").obj("footman").num.toInt).getOrElse(0),
              Try[Int](tile("items").obj("garrison").num.toInt).getOrElse(0)
            )
          }).toSeq,
          BoardStartTracks(
            Try[Int](house("tracks").obj("throne").num.toInt).getOrElse(Int.MaxValue),
            Try[Int](house("tracks").obj("fiefdoms").num.toInt).getOrElse(Int.MaxValue),
            Try[Int](house("tracks").obj("court").num.toInt).getOrElse(Int.MaxValue),
            Try[Int](house("tracks").obj("supply").num.toInt).getOrElse(Int.MaxValue)
//            house("tracks").obj("victory").num.toInt
          )
        )
    ).toSeq

    boardStart
  }

  def loadAvailableOrders(): Map[OrderType, Seq[Order]] = {
    val jsonData = readJson("textures/available_orders.json")

    import ujson.Value.JsonableString

    val orders =
    (
      for (
        order <- jsonData.obj
      )
      yield OrderType.fromString(order._1) ->
        order._2.arr.flatMap(o => {
          List.fill(Try[Int](o("amount").num.toInt).getOrElse(1))(Order(
            OrderType.fromString(order._1),
            Try[Boolean](o("star").bool).getOrElse(false),
            Try[Int](o("modifier").num.toInt).getOrElse(0)
          ))
        }).toSeq
    )
      .toMap

    orders
  }

  def loadOtherRules(): Map[String, Any] = {
    val jsonData = readJson("textures/other_rules.json")

    val rules = Map(
      "numOfTokens" -> jsonData.obj("numOfPowerTokensAtStart").num.toInt,
      "kingsCourtStars" -> jsonData.obj("kingsCourtStars").arr.view.map(_.num.toInt).toVector,
      "supplyUsage" -> jsonData.obj("supplyUsage").arr.view.map(_.arr.map(_.num.toInt).toSeq).toVector,
      "maxArmies" -> jsonData.obj("maxArmies").obj.map((k, v)=> {
        k match
          case "footmen" => MilitaryUnitFootmen -> v.num.toInt
          case "knights" => MilitaryUnitKnights -> v.num.toInt
          case "ships" => MilitaryUnitShips -> v.num.toInt
          case "siegeEngines" => MilitaryUnitSiegeEngines -> v.num.toInt
          case "powerToken" => MilitaryUnitPowerToken -> v.num.toInt
      }).toMap
    )

    rules
  }

}
