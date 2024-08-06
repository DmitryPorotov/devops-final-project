package generator

import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

object Main extends App {
  private val pg = PythonGenerator()
  pg.generate(InstanciesCollection.phases)
  private val dir = "generated/python/DTO/"
  private val path = Paths.get(dir + "phases/phases.py")
  Files.createDirectories(Paths.get(dir + "phases/"))
  Files.createDirectories(Paths.get(dir + "actions/"))
  Files.write(path, pg.builder.toString.getBytes(StandardCharsets.UTF_8))

  InstanciesCollection.actions.foreach((mainPhase, actions) => {
    val pg = PythonGenerator()
    pg.generate(actions)
    val path = Paths.get(s"$dir/actions/$mainPhase.py")
    Files.write(path, pg.builder.toString.getBytes(StandardCharsets.UTF_8))
  })

  private val tsg = TypeScriptGenerator()
  tsg.generate(InstanciesCollection.phases)
  private val dirTs = "generated/typescript/DTO/"
  private val pathTs = Paths.get(dirTs + "phases/phases.d.ts")
  Files.createDirectories(Paths.get(dirTs + "phases/"))
  Files.createDirectories(Paths.get(dirTs + "actions/"))
  Files.write(pathTs, tsg.builder.toString.getBytes(StandardCharsets.UTF_8))

  InstanciesCollection.actions.foreach((mainPhase, actions) => {
    val tsg = TypeScriptGenerator()
    tsg.generate(actions)
    val path = Paths.get(s"$dirTs/actions/$mainPhase.d.ts")
    Files.write(path, tsg.builder.toString.getBytes(StandardCharsets.UTF_8))
  })


  private val lg = LuaGenerator()
  lg.generate(InstanciesCollection.phases)
  private val dirL = "generated/lua/DTO/"
  private val pathL = Paths.get(dirL + "phases/phases.lua")
  Files.createDirectories(Paths.get(dirL + "phases/"))
  Files.createDirectories(Paths.get(dirL + "actions/"))
  Files.write(pathL, lg.builder.toString.getBytes(StandardCharsets.UTF_8))

  InstanciesCollection.actions.foreach((mainPhase, actions) => {
    val lg = LuaGenerator()
    lg.generate(actions)
    val path = Paths.get(s"$dirL/actions/$mainPhase.lua")
    Files.write(path, lg.builder.toString.getBytes(StandardCharsets.UTF_8))
  })
}
