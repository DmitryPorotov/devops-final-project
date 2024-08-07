package generator.languageAgnosticTypeObjects

import generator.{GeneratorTemplate, LuaGenerator, PythonGenerator, TypeScriptGenerator}

import scala.util.{Success, Try}

object DependenciesMap {
  private val dependencies: Map[String, String] = Map(
    "MilitaryUnit" -> "server_module.game_state.military_unit",
    "HouseType" -> "server_module.game_state.military_unit",
    "TrackType" -> "server_module.game_state.track_type",
    "Order" -> "server_module.game_state.order",
    "GameState" -> "server_module.game_state.game_state",
    "GameRules" -> "server_module.game_state.game_state",
    "Action" -> "DTO.actions.all_actions",
    "SubPhase" -> "DTO.phases.all_phases",
  )

  private val nameSpaceTranslations: Map[String, Map[String, String]] = Map(
    PythonGenerator().getClass.getName -> Map(
      "server_module.game_state.military_unit" -> "server_module.game_state.military_unit",
      "server_module.game_state.track_type" -> "server_module.game_state.track_type",
      "server_module.game_state.order" -> "server_module.game_state.order",
      "server_module.game_state.game_state" -> "server_module.game_state.game_state",
      "DTO.actions.all_actions" -> "DTO.actions.all_actions",
      "DTO.phases.all_phases" -> "DTO.phases.all_phases",
    ),
    TypeScriptGenerator().getClass.getName -> Map(
      "server_module.game_state.military_unit" -> "unimplemented",
      "server_module.game_state.track_type" -> "unimplemented",
      "server_module.game_state.order" -> "unimplemented",
      "server_module.game_state.game_state" -> "unimplemented",
      "DTO.actions.all_actions" -> "unimplemented",
      "DTO.phases.all_phases" -> "unimplemented",
    ),
    LuaGenerator().getClass.getName -> Map(
      "server_module.game_state.military_unit" -> "unimplemented",
      "server_module.game_state.track_type" -> "unimplemented",
      "server_module.game_state.order" -> "unimplemented",
      "server_module.game_state.game_state" -> "unimplemented",
      "DTO.actions.all_actions" -> "unimplemented",
      "DTO.phases.all_phases" -> "unimplemented",
    )
  )

  def getHeaderLines(generator: GeneratorTemplate, dependencies: Set[String]): String = {
    val depMap = dependencies.foldLeft[Map[String, Set[String]]](Map())((acc, d) =>
      val dep = Try[Option[String]] {
        Some(DependenciesMap.dependencies(d))
      } match
        case Success(s) => s
        case _ => None
      if dep.nonEmpty then
        acc + (dep.head -> (acc.getOrElse(dep.head, Set()) + d))
      else acc
    )
    //todo: need to map through nameSpaceTranslations
    depMap.foldLeft[String]("")((acc, map) => {
      val (ns, classes) = map
      acc + buildImportByLanguage(generator, nameSpaceTranslations(generator.getClass.getName)(ns), classes)
    })
  }

  private def buildImportByLanguage(langGen: GeneratorTemplate, ns: String, classes: Set[String]): String = {
    if langGen.isInstanceOf[PythonGenerator] then s"from $ns import ${classes.mkString(", ")}\n"
    else ""
  }

  def buildUnionType(langGen: GeneratorTemplate, name: String, types: Seq[String]): String = {
    if langGen.isInstanceOf[PythonGenerator] then
      "from DTO.actions.planning import *\n" +
      "from DTO.actions.events import *\n" +
      "from DTO.actions.action import *\n" +
      s"\n$name = Union[${types.mkString(",\n        ")}]\n"
    else if langGen.isInstanceOf[TypeScriptGenerator] then s"export type $name = ${types.mkString("\n        | ")} \n"
    else ""
  }
}
