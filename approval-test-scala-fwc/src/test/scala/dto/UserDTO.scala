package dto

case class UserDTO(name: String, id: Int, token: String, email: String)

case object UserDTO:
  def fromJson(json :ujson.Obj): UserDTO =
    UserDTO(
      json.obj("name").str,
      json.obj("id").num.toInt,
      json.obj("token").str,
      json.obj("email").str,
    )