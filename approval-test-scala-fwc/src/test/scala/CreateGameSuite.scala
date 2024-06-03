

import org.approvaltests.Approvals
import org.junit.jupiter.api.{Test, Assertions}
import utils.CreateGame

class CreateGameSuite {

  @Test
  def testUsingApprovalTests(): Unit = {
    val json = ujson.read(CreateGame.response.body)
    Assertions.assertTrue(!json.obj("token").isNull)
    json.obj("token") = "a string"
    Approvals.verify(json.render(2))
  }

}
