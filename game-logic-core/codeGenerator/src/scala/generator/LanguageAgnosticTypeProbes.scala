package generator

import fwc.JsonSerializable

import scala.util
import scala.util.{Success, Try}

object LanguageAgnosticTypeProbes {
  def probeType(fromObj: JsonSerializable, objKey: String, value: ujson.Value): String = {
    val isOptional = Try[Boolean] {
      fromObj.getClass.getDeclaredField(objKey).getType.getName == "scala.Option"
    } match
        case Success(o) => o
        case _ => false
    val type_ = value.getClass.getName match
      case "ujson.Str" => probeString(objKey, value.asInstanceOf[ujson.Str])
      case "ujson.Num" => "int"
      case "ujson.Bool" => "bool"
      case "ujson.False$" => "bool"
      case "ujson.True$" => "bool"
      case "ujson.Obj" => probeObj(fromObj, objKey)
      case "ujson.Arr" => probeArray(fromObj, objKey, value.asInstanceOf[ujson.Arr])

      case _ => ""

    if isOptional && !type_.isBlank then
      type_ + "-opt"
    else type_
  }
  
  private def probeArray(fromObj: JsonSerializable, objKey: String, arr: ujson.Arr): String = {
    if arr.value.isEmpty then 
      "arr<any>"
    else 
      "arr<" + probeType(fromObj,objKey: String, arr.value(0)) + ">"
  }

  private def probeString(objKey: String, value: ujson.Str): String = {
    if objKey.contains("ouseType") then "enum<HouseType>"
    else if objKey == "trackType" then "enum<TrackType>"
    else if objKey == "actionType" || objKey == "mainPhase" || objKey == "subPhase" then "str-val-" + value.value
    else "str"
  }

  private def probeObj(fromObj: JsonSerializable, objKey: String): String = {
    val className = fromObj.getClass.getName.split("\\.").last
    className match
      case "ActionResolveSpecialConsolidatePower" =>
        keyIsUnitLike(objKey)
      case "ActionDisbandUnitsAfterCombat" =>
        keyIsUnitLike(objKey)
      case "ActionKillUnitsAfterBattle" =>
        keyIsUnitLike(objKey)
      case "ActionWildlingsKillUnit" =>
        keyIsUnitLike(objKey)
      case "ActionRavenChangeOrder" =>
        keyIsOrder(objKey)
      case "ActionAddOrder" =>
        keyIsOrder(objKey)
      case "ActionDisbandUnitDueToSupplies" =>
        keyIsUnitLike(objKey)
      case "ActionMuster" =>
        keyIsUnitLike(objKey)
      case "SubPhaseSetWildlingsCard" =>
        wildlingCard(objKey)
      case "SubPhaseGetWildlingsCard" =>
        wildlingCard(objKey)
      case "SubPhaseWildlingsKillUnits" =>
        houseTypesMap(objKey)
      case "SubPhaseWildlingsDowngradeKnights" =>
        houseTypesMap(objKey)
      case "ActionResolveMarchOrder" =>
        if objKey == "targets" then "obj<int,arr<MilitaryUnit>>" else ""
      case _ => throw new RuntimeException("Class " + className + " is not in this match. Add the class here.")
  }

  private def keyIsUnitLike(key: String):String = {
    //note: this includes "units", "unitToMuster" etc.
    if key.startsWith("unit") then "MilitaryUnit" else ""
  }

  private def keyIsOrder(key: String): String = {
    if key == "order" then "Order" else ""
  }

  private def houseTypesMap(key: String): String = {
    if key == "houseTypes" then "obj<enum<HouseType>,int>" else ""
  }

  private def wildlingCard(key: String): String = {
    if key == "subPhaseWildlingsCard" then "SubPhaseWildlingsCard" else ""
  }
}
