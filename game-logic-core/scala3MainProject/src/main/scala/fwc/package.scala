package object fwc {
  val jsonIndentation: Int = 
    System.getenv("ENVIRONMENT") match
      case "prod" => 0
      case "dev" => 2
      case _ => 2
  val savesDirectory: String = "saves"
  val isProd: Boolean = "prod".equals(System.getenv("ENVIRONMENT"))
}
