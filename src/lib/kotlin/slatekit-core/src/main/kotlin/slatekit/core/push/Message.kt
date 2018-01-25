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

package slatekit.core.push

import org.json.simple.JSONObject
import slatekit.common.DateTime


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
data class Message(
                    val origin             : String   = "",
                    val originCo           : String   = "",
                    val fromId             : String   = "",
                    val fromPhone          : String   = "",
                    val fromName           : String   = "",
                    val toId               : String   = "",
                    val toPhone            : String   = "",
                    val toName             : String   = "",
                    val toPlatform         : String   = "",
                    val toDevice           : String   = "",
                    val subject            : String   = "",
                    val details            : String   = "",
                    val data               : String   = "",
                    val targetDate         : DateTime = DateTime.now(),
                    val msgCategory        : String   = "",
                    val msgType            : String   = "",
                    val msgAction          : String   = "",
                    val msgDate            : DateTime = DateTime.now(),
                    val msgState           : Int = 0,
                    val msgTag             : String = ""
)
{
  fun source(origin:String, country:String): Message {
    return this.copy(origin = origin, originCo = country)
  }


  fun from(name:String, phone:String, id:String) : Message {
    return this.copy(fromName = name, fromPhone = phone, fromId = id)
  }


  fun to(id:String, phone:String, name:String, platform:String, deviceId:String) : Message {
    return this.copy(
      toId = id,
      toPhone = phone,
      toName = name,
      toPlatform = platform,
      toDevice = deviceId
    )
  }


  fun message(subject:String, details:String, data:String, targetDate:DateTime) : Message {
    return this.copy(
      subject = subject,
      details = details,
      targetDate = targetDate,
      data = data
    )
  }


  fun isAlert():Boolean {
    return isType(MessageConsts.CATEGORY_ALERT)
  }


  fun isShare():Boolean {
    return isType(MessageConsts.CATEGORY_SHARE)
  }


  fun isReg():Boolean {
    return isType(MessageConsts.CATEGORY_REG)
  }


  fun isType(category:String): Boolean {
    return msgCategory == category
  }


  fun toJson() : String  {
    // NOTE: Using the object builder here for
    // quick/simple serialization without 3rd party dependencies
    val json = JSONObject()
    json.put("origin"        , origin     )
    json.put("originCo"      , originCo   )
    json.put("fromId"        , fromId     )
    json.put("fromPhone"     , fromPhone  )
    json.put("fromName"      , fromName   )
    json.put("toId"          , toId       )
    json.put("toPhone"       , toPhone    )
    json.put("toName"        , toName     )
    json.put("toPlatform"    , toPlatform )
    json.put("toDevice"      , toDevice   )
    json.put("subject"       , subject    )
    json.put("details"       , details    )
    json.put("data"          , data       )
    json.put("targetDate"    , targetDate.toStringNumeric() )
    json.put("msgCategory"   , msgCategory)
    json.put("msgType"       , msgType    )
    json.put("msgAction"     , msgAction  )
    json.put("msgDate"       , msgDate.toStringNumeric()    )
    json.put("msgState"      , msgState.toString()   )
    json.put("msgTag"        , msgTag     )
    val text = json.toString()
    return text
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
  fun toJsonCompact() : String {
    // NOTE: Using the object builder here for
    // quick/simple serialization without 3rd party dependencies
    val json = JSONObject()

    // Set the schema version of the notification.
    json.put("mver"     , "1"     )

    // Encode the sender info into a delimited string
    json.put("msender"     , arrayOf(origin, originCo, fromPhone, fromName, fromId ).joinToString(","))

    // Encode the type info into a delimited string
    json.put("mtype"     ,  arrayOf(msgCategory, msgType, msgAction ).joinToString(","))

    // Encode tag into a single string.
    json.put("mtag"     , msgTag  )

    // Now fill the message core data ( title, messag, target date ( if applicable )
    json.put("title"      , subject )
    json.put("details"    , details )
    json.put("targetDate" , targetDate.toStringNumeric())
    json.put("data"       , data )
    json.put("date"       , msgDate.toStringNumeric())

    val text = json.toString()
    return text
  }
}

