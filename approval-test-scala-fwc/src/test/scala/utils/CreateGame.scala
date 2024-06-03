package utils
import sttp.client4.quick.*
import sttp.client4.Response

object CreateGame {

  val response: Response[String] = quickRequest
    .post(uri"http://localhost:8888/api/v1/auth/login")
    .contentType("application/json")
    .body("{\"email\":\"a@b.com\",\"password\":\"12345678\"}")
    .send()

}
