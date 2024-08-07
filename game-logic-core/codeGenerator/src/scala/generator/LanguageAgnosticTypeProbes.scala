package generator

import fwc.JsonSerializable

import scala.util
import scala.util.{Success, Try}
import languageAgnosticTypeObjects.*

object LanguageAgnosticTypeProbes {
  def probeType(fromObj: JsonSerializable, objKey: String, value: ujson.Value): TypeObject = {
    val isOptional = Try[Boolean] {
      fromObj.getClass.getDeclaredField(objKey).getType.getName == "scala.Option"
    } match
        case Success(o) => o
        case _ => false
    value.getClass.getName match
      case "ujson.Str" => probeString(objKey, value.asInstanceOf[ujson.Str])
      case "ujson.Num" => Int(isOptional)
      case "ujson.Bool" => Bool(isOptional)
      case "ujson.False$" => Bool(isOptional)
      case "ujson.True$" => Bool(isOptional)
      case "ujson.Obj" => probeObj(fromObj, objKey)
      case "ujson.Arr" => probeArray(fromObj, objKey, value.asInstanceOf[ujson.Arr])

      case _ => throwIfNoField(fromObj.getClass.getName, objKey)
  }
  
  private def probeArray(fromObj: JsonSerializable, objKey: String, arr: ujson.Arr): TypeObject = {
    if arr.value.isEmpty then 
      Arr()
    else 
      Arr(Some(probeType(fromObj, objKey, arr.value(0))))
  }

  private def probeString(objKey: String, value: ujson.Str): TypeObject = {
    if objKey.contains("ouseType") then Enum("HouseType") //"enum<HouseType>"
    else if objKey == "trackType" then Enum("TrackType")//"enum<TrackType>"
    else if objKey == "actionType" || objKey == "mainPhase" || objKey == "subPhase" then Str(Some(value.value))
    else Str()
  }

  private def probeObj(fromObj: JsonSerializable, objKey: String): TypeObject = {
    val className = fromObj.getClass.getName.split("\\.").last
    className match
      case "ActionResolveSpecialConsolidatePower" =>
        keyIsUnitLike("ActionResolveSpecialConsolidatePower", objKey)
      case "ActionDisbandUnitsAfterCombat" =>
        keyIsUnitLike("ActionDisbandUnitsAfterCombat", objKey)
      case "ActionKillUnitsAfterBattle" =>
        keyIsUnitLike("ActionKillUnitsAfterBattle", objKey)
      case "ActionWildlingsKillUnit" =>
        keyIsUnitLike("ActionWildlingsKillUnit", objKey)
      case "ActionRavenChangeOrder" =>
        keyIsOrder("ActionRavenChangeOrder", objKey)
      case "ActionAddOrder" =>
        keyIsOrder("ActionAddOrder", objKey)
      case "ActionDisbandUnitDueToSupplies" =>
        keyIsUnitLike("ActionDisbandUnitDueToSupplies", objKey)
      case "ActionMuster" =>
        keyIsUnitLike("ActionMuster", objKey)
      case "SubPhaseSetWildlingsCard" =>
        wildlingCard("SubPhaseSetWildlingsCard", objKey)
      case "SubPhaseGetWildlingsCard" =>
        wildlingCard("SubPhaseGetWildlingsCard", objKey)
      case "SubPhaseWildlingsKillUnits" =>
        houseTypesMap("SubPhaseWildlingsKillUnits", objKey)
      case "SubPhaseWildlingsDowngradeKnights" =>
        houseTypesMap("SubPhaseWildlingsDowngradeKnights", objKey)
      case "ActionResolveMarchOrder" =>
        if objKey == "targets" then Obj(keyType = Some(Int()), valueType = Some(Arr(Some(Obj(Some("MilitaryUnit")))))) else throwIfNoField(className, objKey)
      case "ReplyJoinGame" =>
        if objKey == "gameSettings"
        then Obj(Some("GameSettings"))
        else throwIfNoField(className, objKey)
      case "ReplyGetStatus" =>
        if objKey == "status"
        then Obj(Some("GameStatus"))
        else throwIfNoField(className, objKey)
      case "StatusDetails" =>
        objKey match
          case "gameSettings" => Obj(Some("StatusDetails"))
          case "subPhase" => Obj(Some("SubPhase"))
          case _ => throwIfNoField(className, objKey)
      case "GameStatus" =>
        if objKey == "details"
        then Obj(Some("StatusDetails"))
        else throwIfNoField(className, objKey)
      case "ReplyGameAction" =>
        if objKey == "reply"
        then Arr(Some(Obj())) //todo : make a reply object
        else throwIfNoField(className, objKey)
      case "ReplyGetGameState" =>
        objKey match
          case "gameRules" => Obj(Some("GameRules"))
          case "gameState" => Obj(Some("GameState"))
          case _ => throwIfNoField(className, objKey)
      case "ReplyError" =>
        if objKey == "originalMessage"
        then Obj(Some("Message"))
        else throwIfNoField(className, objKey)
      case "MessageGameAction" =>
        if objKey == "game_action"
        then Obj(Some("Action"))
        else throwIfNoField(className, objKey)
      case _ => throw new RuntimeException(s"Class '$className' is not in this match. Add the class and the field '$objKey' here.")
  }

  private def keyIsUnitLike(className: String, key: String): TypeObject = {
    //note: this includes "units", "unitToMuster" etc.
    if key.startsWith("unit") then Obj(Some("MilitaryUnit")) else throwIfNoField(className, key)
  }

  private def keyIsOrder(className: String, key: String): TypeObject = {
    if key == "order" then Obj(Some("Order")) else throwIfNoField(className, key)
  }

  private def houseTypesMap(className: String, key: String): TypeObject = {
    if key == "houseTypes" then Obj(Some("obj<enum<HouseType>,int>")) else throwIfNoField(className, key)
  }

  private def wildlingCard(className: String, key: String): TypeObject = {
    if key == "subPhaseWildlingsCard" then Obj(Some("SubPhaseWildlingsCard")) else throwIfNoField(className, key)
  }

  @throws[RuntimeException]
  private def throwIfNoField(className: String, objKey: String): Nothing = {
    throw new RuntimeException(s"Class's '$className' field '$objKey' is not in the match. Add the field here.")
  }
}
