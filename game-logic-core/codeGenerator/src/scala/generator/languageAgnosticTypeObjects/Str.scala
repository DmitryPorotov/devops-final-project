package generator.languageAgnosticTypeObjects

case class Str(
                value: Option[String] = None,
                override val isOptional: Boolean = false
              ) extends TypeObject(isOptional)
