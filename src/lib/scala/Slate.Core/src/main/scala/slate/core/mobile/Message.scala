/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.mobile

import org.json.simple.JSONObject
import slate.common._
import slate.common.serialization.ObjectBuilderJson

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
case class Message(
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
  def source(origin:String, country:String): Message = {
    this.copy(origin = origin, originCo = country)
  }


  def from(name:String, phone:String, id:String) : Message =  {
    this.copy(fromName = name, fromPhone = phone, fromId = id)
  }


  def to(id:String, phone:String, name:String, platform:String, deviceId:String) : Message =  {
    this.copy(
      toId = id,
      toPhone = phone,
      toName = name,
      toPlatform = platform,
      toDevice = deviceId
    )
  }


  def message(subject:String, details:String, data:String, targetDate:DateTime) : Message =  {
    this.copy(
      subject = subject,
      details = details,
      targetDate = targetDate,
      data = data
    )
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

    val parser = new org.json.simple.parser.JSONParser()
    val json = parser.parse(content).asInstanceOf[JSONObject]
    val msg = new Message(
      origin      = json.get("origin"        ).asInstanceOf[String],
      originCo    = json.get("originCo"      ).asInstanceOf[String],
      fromId      = json.get("fromId"        ).asInstanceOf[String],
      fromPhone   = json.get("fromPhone"     ).asInstanceOf[String],
      fromName    = json.get("fromName"      ).asInstanceOf[String],
      toId        = json.get("toId"          ).asInstanceOf[String],
      toPhone     = json.get("toPhone"       ).asInstanceOf[String],
      toName      = json.get("toName"        ).asInstanceOf[String],
      toPlatform  = json.get("toPlatform"    ).asInstanceOf[String],
      toDevice    = json.get("toDevice"      ).asInstanceOf[String],
      subject     = json.get("subject"       ).asInstanceOf[String],
      details     = json.get("details"       ).asInstanceOf[String],
      data        = json.get("data"          ).asInstanceOf[String],
      targetDate  = DateTime.parseNumericVal(json.get("targetDate").asInstanceOf[String]),
      msgCategory = json.get("msgCategory"   ).asInstanceOf[String],
      msgType     = json.get("msgType"       ).asInstanceOf[String],
      msgAction   = json.get("msgAction"     ).asInstanceOf[String],
      msgDate     = DateTime.parseNumericVal(json.get("msgDate"   ).asInstanceOf[String]),
      msgState    = json.get("msgState"      ).asInstanceOf[String].toInt,
      msgTag      = json.get("msgTag"        ).asInstanceOf[String]
    )
    msg
  }
}
