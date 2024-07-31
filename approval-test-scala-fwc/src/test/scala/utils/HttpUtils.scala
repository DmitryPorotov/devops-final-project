package utils

import dto.UserDTO
import sttp.client4.Response
import sttp.client4.quick.*

object HttpUtils {

  def response(email: String, password: String = "12345678"): Response[String] =
    quickRequest
      .post(uri"http://localhost:8888/fwc/api/v1/auth/login")
      .contentType("application/json")
      .body(s"{\"email\":\"$email\",\"password\":\"$password\"}")
      .send()
  
  def login(email: String, password: String = "12345678"): UserDTO =
    val resp = quickRequest
      .post(uri"http://localhost:8888/fwc/api/v1/auth/login")
      .contentType("application/json")
      .body(s"{\"email\":\"$email\",\"password\":\"$password\"}")
      .send()
    val json = ujson.read(resp.body)
    UserDTO.fromJson(json.obj)
}
