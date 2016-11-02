/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.core.mobile

import org.json.simple.JSONObject
import slate.common._
import slate.common.serialization.ObjectBuilderJson


class Message
{

  /**
   *
   * @param origin       : origin ( phone | user | server )
   * @param originCo     : country of origin "us", "in"
   * @param toId         : id of sender
   * @param toPhone      : phone of sender
   * @param toName       : name of sender
   * @param toPlatform   : destination platform "and" | "ios"
   * @param toDevice     : destination device id
   * @param subject      : Title of the message
   * @param details      : Details of message ( Short description, full data is in "data" field )
   * @param data         : The actual data of the message
   * @param targetDate   : Optional target date for the message
   * @param msgCategory  : Category of message ( "general" | "reg" | "share" | "alert" )
   * @param msgType      : Specifies the type of data in the message details
   * @param msgAction    : Specifies an action to perform
   * @param msgDate      : Date message was sent
   * @param msgState     : State of the data when sent
   * @param msgTag       : Tag for reference purposes
   */
  def this(
              origin             : String   = "",
              originCo           : String   = "",
              fromId             : String   = "",
              fromPhone          : String   = "",
              fromName           : String   = "",
              toId               : String   = "",
              toPhone            : String   = "",
              toName             : String   = "",
              toPlatform         : String   = "",
              toDevice           : String   = "",
              subject            : String   = "",
              details            : String   = "",
              data               : String   = "",
              targetDate         : DateTime = DateTime.now(),
              msgCategory        : String   = "",
              msgType            : String   = "",
              msgAction          : String   = "",
              msgDate            : DateTime = DateTime.now(),
              msgState           : Int = 0,
              msgTag             : String = ""
            )
  {
    this()
    this.origin           = origin
    this.originCo         = originCo
    this.fromId           = fromId
    this.fromPhone        = fromPhone
    this.fromName         = fromName
    this.toId             = toId
    this.toPhone          = toPhone
    this.toName           = toName
    this.toPlatform       = toPlatform
    this.toDevice         = toDevice
    this.subject          = subject
    this.details          = details
    this.data             = data
    this.targetDate       = targetDate
    this.msgCategory      = msgCategory
    this.msgType          = msgType
    this.msgAction        = msgAction
    this.msgDate          = msgDate
    this.msgState         = msgState
    this.msgTag           = msgTag
  }

  var  origin             : String   = ""
  var  originCo           : String   = ""
  var  fromId             : String   = ""
  var  fromPhone          : String   = ""
  var  fromName           : String   = ""
  var  toId               : String   = ""
  var  toPhone            : String   = ""
  var  toName             : String   = ""
  var  toPlatform         : String   = ""
  var  toDevice           : String   = ""
  var  subject            : String   = ""
  var  details            : String   = ""
  var  data               : String   = ""
  var  targetDate         : DateTime = DateTime.now()
  var  msgCategory        : String   = ""
  var  msgType            : String   = ""
  var  msgAction          : String   = ""
  var  msgDate            : DateTime = DateTime.now()
  var  msgState           : Int = 0
  var  msgTag             : String = ""


  def source(origin:String, country:String): Message = {
    this.origin = origin
    this.originCo = country
    this
  }


  def from(name:String, phone:String, id:String) : Message =  {
    this.fromName = name
    this.fromPhone = phone
    this.fromId = id
    this
  }


  def to(id:String, phone:String, name:String, platform:String, deviceId:String) : Message =  {
    this.toId = id
    this.toPhone = phone
    this.toName = name
    this.toPlatform = platform
    this.toDevice = deviceId
    this
  }


  def message(subject:String, details:String, data:String, targetDate:DateTime) : Message =  {
    this.subject = subject
    this.details = details
    this.targetDate = targetDate
    this.data = data
    this
  }


  def isAlert():Boolean = {
    isType(Message.CATEGORY_ALERT)
  }


  def isShare():Boolean = {
    isType(Message.CATEGORY_SHARE)
  }


  def isReg():Boolean = {
    isType(Message.CATEGORY_REG)
  }


  def isType(category:String): Boolean = {
    Strings.isMatch(msgCategory, category)
  }


  def toJson() : String =
  {
    // NOTE: Using the object builder here for
    // quick/simple serialization without 3rd party dependencies
    val json = new ObjectBuilderJson(true, "  ")
    json.begin()
    json.putString("origin"        , origin     )
    json.putString("originCo"      , originCo   )
    json.putString("fromId"        , fromId     )
    json.putString("fromPhone"     , fromPhone  )
    json.putString("fromName"      , fromName   )
    json.putString("toId"          , toId       )
    json.putString("toPhone"       , toPhone    )
    json.putString("toName"        , toName     )
    json.putString("toPlatform"    , toPlatform )
    json.putString("toDevice"      , toDevice   )
    json.putString("subject"       , subject    )
    json.putString("details"       , details    )
    json.putString("data"          , data       )
    json.putString("targetDate"    , targetDate.toStringNumeric() )
    json.putString("msgCategory"   , msgCategory)
    json.putString("msgType"       , msgType    )
    json.putString("msgAction"     , msgAction  )
    json.putString("msgDate"       , msgDate.toStringNumeric()    )
    json.putString("msgState"      , msgState.toString   )
    json.putString("msgTag"        , msgTag     )
    json.end()
    val text = json.toString()
    text
  }


  /**
    * {
    "M0": "0",
    "MS": "d;3478910170;kishore;7e2b1e3c-743f-4dd8-b6d1-b78d64414019",
    "MT": "share;va;share",
    "MM": "0"
    "Title": "Test-From-Server",
    "Details": "FullMessage2",
    "TargetDate": "20150618",
    "Data": "abc123def456",
    "Date": "2015-06-18T00:00:00",
}
    * @return
    */
  def toJsonCompact() : String =
  {
    // NOTE: Using the object builder here for
    // quick/simple serialization without 3rd party dependencies
    val json = new ObjectBuilderJson(true, "  ")
    json.begin()

    // Set the schema version of the notification.
    json.putString("MO"     , "1"     )

    // Encode the sender info into a delimited string
    json.putString("MS"     , Strings.delimited(origin, originCo, fromPhone, fromName, fromId ))

    // Encode the type info into a delimited string
    json.putString("MT"     ,  Strings.delimited(msgCategory, msgType, msgAction ))

    // Encode tag into a single string.
    json.putString("MM"     , msgTag  )

    // Now fill the message core data ( title, messag, target date ( if applicable )
    json.putString("Title"      , subject )
    json.putString("Details"    , details )
    json.putString("TargetDate" , targetDate.toStringNumeric())
    json.putString("Data"       , data )
    json.putString("Date"       , msgDate.toStringNumeric())

    // end
    json.end()
    val text = json.toString()
    text
  }
}


object Message {

  val CATEGORY_GENERAL = "gen"
  val CATEGORY_REG     = "reg"
  val CATEGORY_SHARE   = "share"
  val CATEGORY_ALERT   = "alert"
  val PLATFORM_ANDROID = "and"
  val PLATFORM_IPHONE  = "ios"


  def reg() : Message =
  {
    new Message(msgCategory = CATEGORY_REG, msgType = "register", msgAction = "-")
  }


  def regConfirm() : Message =
  {
    new Message(msgCategory = CATEGORY_REG, msgType = "confirm", msgAction = "-")
  }


  def share(typ:String = "", action:String = "") : Message =
  {
    new Message(msgCategory = CATEGORY_SHARE, msgType = typ, msgAction = action)
  }


  def alert(typ:String = "", action:String = "") : Message =
  {
    new Message(msgCategory = CATEGORY_ALERT, msgType = typ, msgAction = action)
  }


  def msg(typ:String = "", action:String = "") : Message =
  {
    new Message(msgCategory = CATEGORY_GENERAL, msgType = typ, msgAction = action)
  }


  def fromJson(content:String):Message = {
    val msg = new Message()
    val parser = new org.json.simple.parser.JSONParser()
    val json = parser.parse(content).asInstanceOf[JSONObject]
    msg.origin      = json.get("origin"        ).asInstanceOf[String]
    msg.originCo    = json.get("originCo"      ).asInstanceOf[String]
    msg.fromId      = json.get("fromId"        ).asInstanceOf[String]
    msg.fromPhone   = json.get("fromPhone"     ).asInstanceOf[String]
    msg.fromName    = json.get("fromName"      ).asInstanceOf[String]
    msg.toId        = json.get("toId"          ).asInstanceOf[String]
    msg.toPhone     = json.get("toPhone"       ).asInstanceOf[String]
    msg.toName      = json.get("toName"        ).asInstanceOf[String]
    msg.toPlatform  = json.get("toPlatform"    ).asInstanceOf[String]
    msg.toDevice    = json.get("toDevice"      ).asInstanceOf[String]
    msg.subject     = json.get("subject"       ).asInstanceOf[String]
    msg.details     = json.get("details"       ).asInstanceOf[String]
    msg.data        = json.get("data"          ).asInstanceOf[String]
    msg.targetDate  = DateTime.parseNumericVal(json.get("targetDate").asInstanceOf[String])
    msg.msgCategory = json.get("msgCategory"   ).asInstanceOf[String]
    msg.msgType     = json.get("msgType"       ).asInstanceOf[String]
    msg.msgAction   = json.get("msgAction"     ).asInstanceOf[String]
    msg.msgDate     = DateTime.parseNumericVal(json.get("msgDate"   ).asInstanceOf[String])
    msg.msgState    = json.get("msgState"      ).asInstanceOf[String].toInt
    msg.msgTag      = json.get("msgTag"        ).asInstanceOf[String]
    msg
  }
}
