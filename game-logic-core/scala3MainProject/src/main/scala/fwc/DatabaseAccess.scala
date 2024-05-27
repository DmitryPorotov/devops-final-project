package fwc

import com.datastax.driver.core.{Cluster, Row, Session}
import fwc.game.eventsPhase.cards.BoardCards
import fwc.gameSaving.actions.Action

import scala.util.{Failure, Success, Try}
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.{Date, UUID}

object DatabaseAccess {
  private val CASSANDRA_CONTACT_POINT = "CASSANDRA_CONTACT_POINT"
  private val CASSANDRA_PORT = "CASSANDRA_PORT"

  private val cassandraContactPoint: String =
    if System.getenv(CASSANDRA_CONTACT_POINT) == null
    then "localhost"
    else System.getenv(CASSANDRA_CONTACT_POINT)

  private val cassandraPort: Int =
    if System.getenv(CASSANDRA_PORT) == null
    then 9042
    else Try(System.getenv(CASSANDRA_PORT).toInt) match
      case Success(value) => value
      case Failure(value) =>
        println(s"$CASSANDRA_PORT is not an integer value. Defaulting to 9042.")
        9042


  private val session: Session = Cluster.builder()
    .addContactPoint(cassandraContactPoint)
    .withPort(cassandraPort)
    .build()
    .newSession()

  private val dateFormat = new SimpleDateFormat("YYYY-MM-dd")
  private val timeFormat = new SimpleDateFormat("hh:mm:ss")

  def saveGameSettings(settings: GameSettings, startingBoardCards: BoardCards = null): Unit =
    val instant = Clock.systemUTC().instant()
    val date = new Date(instant.getEpochSecond * 1000)
    val nano = instant.getNano
    val query = s"INSERT INTO fwc.games (lobby_id, game_uuid ,date, time, settings${if startingBoardCards == null then "" else ", starting_board_cards"}) " +
      s"VALUES (${settings.gameId}, ${settings.gameUuid}, '${dateFormat.format(date)}', '${timeFormat.format(date)}.$nano', " +
      s"'${settings.toJson}'${if startingBoardCards == null then "" else s", '${startingBoardCards.toJson}'"})" +
//      s" USING TTL 86400" +
      s";"
    session.execute(query)

  def saveGameAction(lobbyId: Int, gameUuid: UUID, action: Action): Unit =
    val instant = Clock.systemUTC().instant()
    val date = new Date(instant.getEpochSecond * 1000)
    val nano = instant.getNano
    val query = "INSERT INTO fwc.actions_by_game (lobby_id, game_uuid ,date, time, action) " +
      s"VALUES ($lobbyId, $gameUuid, '${dateFormat.format(date)}', '${timeFormat.format(date)}.$nano', '${action.toJson}')" +
      //      s" USING TTL 86400" +
      s";"
    session.execute(query)

  def getGameActions(lobbyId: Int, gameUuid: UUID): Array[ujson.Value] =
    val query = s"SELECT * FROM fwc.actions_by_game WHERE lobby_id=$lobbyId AND game_uuid=$gameUuid;"
    val results = session.execute(query)
    results.all().toArray.map(r => ujson.read(r.asInstanceOf[Row].getString("action")))

  def loadGameCurrentSettings(lobbyId: Int, gameUuid: UUID): ujson.Value =
    val query = s"SELECT * FROM fwc.games WHERE lobby_id=$lobbyId AND game_uuid=$gameUuid " +
      s"LIMIT 1;"
    
    val result = session.execute(query).one()

    val settings = result.getString("settings")
    val startingCards = result.getString("starting_board_cards")
    ujson.Obj(
      "gameSettings" ->  ujson.read(settings),
      "startingBoardCards" -> ujson.read(startingCards),
    )

}
