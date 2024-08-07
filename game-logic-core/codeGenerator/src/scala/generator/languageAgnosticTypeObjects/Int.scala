package generator.languageAgnosticTypeObjects

case class Int(
                override val isOptional: Boolean = false
              ) extends TypeObject(isOptional)
