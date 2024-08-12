package generator

import fwc.JsonSerializable

import java.nio.file.{Files, Path, Paths}
import java.nio.charset.StandardCharsets

object Main extends App {
  Seq(
    "python",
    "lua",
    "typescript"
  ).foreach(l => makeTypeFiles(l))

  private def makeTypeFiles(language: String): Unit = {
    language match
      case "python" =>
        val dir = "generated/python/DTO/"
        generateForLang(language, dir, "py")
      case "lua" =>
        val dirL = "generated/lua/DTO/"
        generateForLang(language, dirL, "lua")
      case "typescript" =>
        val dirTs = "generated/typescript/DTO/"
        generateForLang(language, dirTs, "d.ts")
      case l => throw new RuntimeException(s"Unknown PL $l")
  }

  private def generateForLang(language: String, dir: String, fileExt: String): Unit = {
    Files.createDirectories(Paths.get(dir + "replies/"))
    Files.createDirectories(Paths.get(dir + "messages/"))
    Files.createDirectories(Paths.get(dir + "phases/"))
    Files.createDirectories(Paths.get(dir + "actions/"))
    val pathRep = Paths.get(s"${dir}replies/replies.$fileExt")
    genAndWrite(pathRep, getGeneratorInst(language), InstanciesCollection.replies)
    val pathMes = Paths.get(s"${dir}messages/messages.$fileExt")
    genAndWrite(pathMes, getGeneratorInst(language), InstanciesCollection.messages)
    val pathPhase = Paths.get(s"${dir}phases/phases.$fileExt")
    genAndWrite(pathPhase, getGeneratorInst(language), InstanciesCollection.phases)
    val phasesUnionPath = Paths.get(s"${dir}phases/all_phases.$fileExt")
    genUnionAndWrite(phasesUnionPath, getGeneratorInst(language), "SubPhase")
    InstanciesCollection.actions.foreach((mainPhase, actions) => {
      val path = Paths.get(s"${dir}actions/$mainPhase.$fileExt")
      genAndWrite(path, getGeneratorInst(language), actions)
    })
    val actionUnionPath = Paths.get(s"${dir}actions/all_actions.$fileExt")
    genUnionAndWrite(actionUnionPath, getGeneratorInst(language), "Action")
  }

  private def genAndWrite(path: Path, generatorTemplate: GeneratorTemplate, instancies: Map[String, JsonSerializable]): Unit = {
    generatorTemplate.generate(instancies)
    Files.write(path, generatorTemplate.toString.getBytes(StandardCharsets.UTF_8))
  }

  private def genUnionAndWrite(path: Path, generatorTemplate: GeneratorTemplate, unionName: String): Unit = {
    generatorTemplate.generateUnion(unionName)
    Files.write(path, generatorTemplate.toString(true).getBytes(StandardCharsets.UTF_8))
  }

  private def getGeneratorInst(language: String): GeneratorTemplate = {
    language match
      case "python" => PythonGenerator()
      case "lua" => LuaGenerator()
      case "typescript" => TypeScriptGenerator()
      case l => throw new RuntimeException(s"Unknown PL $l")
  }

  println("Finished generating.")


}
