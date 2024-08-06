package generator

import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

object Main extends App {
//  private val pgRep = PythonGenerator()
//  pgRep.generate(InstanciesCollection.replies)
  private val dir = "generated/python/DTO/"
//  private val pathRep = Paths.get(dir + "replies/replies.py")
//  Files.createDirectories(Paths.get(dir + "replies/"))
//  Files.createDirectories(Paths.get(dir + "actions/"))
//  Files.write(pathRep, pgRep.toString.getBytes(StandardCharsets.UTF_8))

  private val pgMes = PythonGenerator()
  pgMes.generate(InstanciesCollection.messages)
  private val pathMes = Paths.get(dir + "messages/messages.py")
  Files.createDirectories(Paths.get(dir + "messages/"))
  Files.write(pathMes, pgMes.toString.getBytes(StandardCharsets.UTF_8))

//  InstanciesCollection.actions.foreach((mainPhase, actions) => {
//    val pg = PythonGenerator()
//    pg.generate(actions)
//    val path = Paths.get(s"$dir/actions/$mainPhase.py")
//    Files.write(path, pg.toString.getBytes(StandardCharsets.UTF_8))
//  })
//
//  private val tsg = TypeScriptGenerator()
//  tsg.generate(InstanciesCollection.phases)
//  private val dirTs = "generated/typescript/DTO/"
//  private val pathTs = Paths.get(dirTs + "phases/phases.d.ts")
//  Files.createDirectories(Paths.get(dirTs + "phases/"))
//  Files.createDirectories(Paths.get(dirTs + "actions/"))
//  Files.write(pathTs, tsg.toString.getBytes(StandardCharsets.UTF_8))
//
//  InstanciesCollection.actions.foreach((mainPhase, actions) => {
//    val tsg = TypeScriptGenerator()
//    tsg.generate(actions)
//    val path = Paths.get(s"$dirTs/actions/$mainPhase.d.ts")
//    Files.write(path, tsg.toString.getBytes(StandardCharsets.UTF_8))
//  })
//
//
//  private val lg = LuaGenerator()
//  lg.generate(InstanciesCollection.phases)
//  private val dirL = "generated/lua/DTO/"
//  private val pathL = Paths.get(dirL + "phases/phases.lua")
//  Files.createDirectories(Paths.get(dirL + "phases/"))
//  Files.createDirectories(Paths.get(dirL + "actions/"))
//  Files.write(pathL, lg.toString.getBytes(StandardCharsets.UTF_8))
//
//  InstanciesCollection.actions.foreach((mainPhase, actions) => {
//    val lg = LuaGenerator()
//    lg.generate(actions)
//    val path = Paths.get(s"$dirL/actions/$mainPhase.lua")
//    Files.write(path, lg.toString.getBytes(StandardCharsets.UTF_8))
//  })

}
