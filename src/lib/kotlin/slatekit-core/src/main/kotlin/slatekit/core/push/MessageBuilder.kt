package slatekit.core.push

import org.json.simple.JSONObject
import slatekit.common.DateTime


object MessageBuilder {


    fun reg() : Message {
        return Message.of(meta = Meta.of(Reg, MessageConsts.TYPE_REG, ""))
    }


    fun regConfirm() : Message {
        return Message.of(meta = Meta.of(Reg, MessageConsts.TYPE_CONFIRM, ""))
    }


    fun share(typ:String = "", action:String = "") : Message {
        return Message.of(meta = Meta.of(Share, typ, action))
    }


    fun alert(typ:String = "", action:String = "") : Message {
        return Message.of(meta = Meta.of(Alert, typ, action))
    }


    fun msg(cat: Category, typ:String = "", action:String = "") : Message {
        return Message.of(meta = Meta.of(cat, typ, action))
    }


    fun fromJson(content:String):Message {

        val parser = org.json.simple.parser.JSONParser()
        val json = parser.parse(content) as JSONObject
        val sender = json.get("sender") as JSONObject
        val recipient = json.get("recipient") as JSONObject
        val meta = json.get("meta") as JSONObject
        val content = json.get("content") as JSONObject

        fun getDate(name:String, json:JSONObject): DateTime {
           if(!json.containsKey(name)) return DateTime.MIN
           val date = json.get(name)
            return when(date) {
                null -> DateTime.MIN
                ""   -> DateTime.MIN
                else -> DateTime.parseNumeric(date as String)
            }
        }
        val msg = Message(
                meta = Meta(
                    category   = parseCategory(meta.get("category") as String),
                    type       = meta.get("type") as String,
                    id         = meta.get("id") as String,
                    action     = meta.get("action") as String,
                    origin     = meta.get("origin") as String,
                    tag        = meta.get("tag") as String,
                    tsMsg      = getDate("tsMsg", meta),
                    tsSent     = getDate("tsSent", meta),
                    args       = parseArgs("args", meta)
                ),
                sender = Sender(
                    id        = sender.get("id"    ) as String,
                    phone     = sender.get("phone" ) as String,
                    name      = sender.get("name"  ) as String,
                    device    = sender.get("device") as String,
                    platform  = parsePlatform(sender.get("platform") as String),
                    args      = parseArgs("args", sender)
                ),
                recipient = Recipient(
                    id        = recipient.get("id"    ) as String,
                    phone     = recipient.get("phone" ) as String,
                    name      = recipient.get("name"  ) as String,
                    device    = recipient.get("device") as String,
                    platform  = parsePlatform(recipient.get("platform") as String),
                    args      = parseArgs("args", recipient)
                ),
                content = Content(
                    title = content.get("title") as String,
                    desc  = content.get("desc") as String,
                    date  = getDate("date", content),
                    args  = parseArgs("args", content)
                )
        )
        return msg
    }


    fun parseArgs(name:String, json:JSONObject): Map<String,String>? {
        if(!json.containsKey(name)) return mapOf()
        val result = json.get(name) as? JSONObject
        return when(result) {
            null  -> null
            else  -> {
                val map = result.keys.map{ key -> Pair(key as String, result.get(key) as String) }.toMap()
                map
            }
        }
    }


    fun parseCategory(category:String?): Category {
        return when(category) {
            null, ""   -> OtherCategory("")
            Share.name -> Share
            Reg.name   -> Reg
            Alert.name -> Alert
            else       -> OtherCategory(category)
        }
    }


    fun parsePlatform(platform:String?): Platform {
        return when(platform) {
            null, ""          -> OtherPlatform("")
            PlatformIOS.name  -> PlatformIOS
            PlatformAnd.name  -> PlatformAnd
            PlatformWeb.name  -> PlatformWeb
            PlatformNone.name -> PlatformNone
            else              -> OtherPlatform(platform)
        }
    }
}
