import org.approvaltests.Approvals
import org.junit.jupiter.api.{Assertions, Test}
import utils.{CreateGame, HttpUtils}

class CreateGameSuite {

  @Test
  def testCreateGame(): Unit = {
    val json = ujson.read(HttpUtils.response("a@b.com").body)
    Assertions.assertTrue(!json.obj("token").isNull)
    json.obj("token") = "a string"
    Approvals.verify(json.render(2))
//    CreateGame.createGame()
    
  }

}
