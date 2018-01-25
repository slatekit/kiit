package slatekit.core.push

import org.json.simple.JSONObject
import slatekit.common.DateTime


object MessageBuilder {


    fun reg() : Message {
        return Message(msgCategory = MessageConsts.CATEGORY_REG, msgType = MessageConsts.TYPE_REG, msgAction = "-")
    }


    fun regConfirm() : Message {
        return Message(msgCategory = MessageConsts.CATEGORY_REG, msgType = MessageConsts.TYPE_CONFIRM, msgAction = "-")
    }


    fun share(typ:String = "", action:String = "") : Message {
        return Message(msgCategory = MessageConsts.CATEGORY_SHARE, msgType = typ, msgAction = action)
    }


    fun alert(typ:String = "", action:String = "") : Message {
        return Message(msgCategory = MessageConsts.CATEGORY_ALERT, msgType = typ, msgAction = action)
    }


    fun msg(typ:String = "", action:String = "") : Message {
        return Message(msgCategory = MessageConsts.CATEGORY_GENERAL, msgType = typ, msgAction = action)
    }


    fun fromJson(content:String):Message {

        val parser = org.json.simple.parser.JSONParser()
        val json = parser.parse(content) as JSONObject
        val msg = Message(
                origin      = json.get("origin"        ) as String,
                originCo    = json.get("originCo"      ) as String,
                fromId      = json.get("fromId"        ) as String,
                fromPhone   = json.get("fromPhone"     ) as String,
                fromName    = json.get("fromName"      ) as String,
                toId        = json.get("toId"          ) as String,
                toPhone     = json.get("toPhone"       ) as String,
                toName      = json.get("toName"        ) as String,
                toPlatform  = json.get("toPlatform"    ) as String,
                toDevice    = json.get("toDevice"      ) as String,
                subject     = json.get("subject"       ) as String,
                details     = json.get("details"       ) as String,
                data        = json.get("data"          ) as String,
                targetDate  = DateTime.parseNumeric(json.get("targetDate") as String),
                msgCategory = json.get("msgCategory"   ) as String,
                msgType     = json.get("msgType"       ) as String,
                msgAction   = json.get("msgAction"     ) as String,
                msgDate     = DateTime.parseNumeric(json.get("msgDate") as String),
                msgState    = (json.get("msgState"      ) as String).toInt(),
                msgTag      = (json.get("msgTag"        ) as String)
                )
                return msg
    }
}
