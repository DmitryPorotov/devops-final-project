package generator.languageAgnosticTypeObjects

case class Enum(
                 name: String,
                 override val isOptional: Boolean = false
               ) extends TypeObject(isOptional)
