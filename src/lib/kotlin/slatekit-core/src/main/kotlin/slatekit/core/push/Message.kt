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
 * @param meta      : Metadata about the message
 * @param sender    : Information about the sender
 * @param recipient : Information about the recipient
 * @param content   : Metadata about the content / hints
 * @param data      : The actual dynamic payload of the message
 */
data class Message(
    val meta: Meta,
    val sender: Sender,
    val recipient: Recipient,
    val content: Content,
    val data: String = ""
) {

    /**
     * Update the source
     */
    fun source(origin: String): Message {
        return this.copy(meta = meta.copy(origin = origin))
    }


    /**
     * Update the sender info
     */
    fun from(name: String, phone: String, id: String): Message {
        return this.copy(sender = sender.copy(name = name, phone = phone, id = id))
    }


    /**
     * Update the recipient
     */
    fun to(id: String, phone: String, name: String, platform: Platform, deviceId: String): Message {
        return this.copy(
            recipient = recipient.copy(
                id = id,
                phone = phone,
                name = name,
                platform = platform,
                device = deviceId
            )
        )
    }


    fun message(title: String, desc: String, payload: String, date: DateTime): Message {
        return this.copy(
            content = content.copy(
                title = title,
                desc = desc,
                date = date
            ),
            data = payload
        )
    }


    fun isAlert(): Boolean {
        return isCategory(Alert)
    }


    fun isShare(): Boolean {
        return isCategory(Share)
    }


    fun isReg(): Boolean {
        return isCategory(Reg)
    }


    fun isCategory(category: Category): Boolean {
        return meta.category == category
    }


    fun toJson(): String {
        // NOTE: Using the object builder here for
        // quick/simple serialization without 3rd party dependencies

        val jsonMeta = JSONObject()
        jsonMeta.put("category", meta.category.name)
        jsonMeta.put("type", meta.type)
        jsonMeta.put("id", meta.id)
        jsonMeta.put("action", meta.action)
        jsonMeta.put("origin", meta.origin)
        jsonMeta.put("tag", meta.tag)
        jsonMeta.put("tsMsg", meta.tsMsg)
        jsonMeta.put("tsSent", meta.tsSent)
        jsonMeta.put("args", meta.args)

        val jsonSender = JSONObject()
        jsonSender.put("id", sender.id)
        jsonSender.put("phone", sender.phone)
        jsonSender.put("name", sender.name)
        jsonSender.put("device", sender.device)
        jsonSender.put("platform", sender.platform.name)
        jsonSender.put("args", sender.args)

        val jsonRecipient = JSONObject()
        jsonRecipient.put("id", recipient.id)
        jsonRecipient.put("phone", recipient.phone)
        jsonRecipient.put("name", recipient.name)
        jsonRecipient.put("device", recipient.device)
        jsonRecipient.put("platform", recipient.platform.name)
        jsonRecipient.put("args", recipient.args)

        val jsonContent = JSONObject()
        jsonContent.put("title", content.title)
        jsonContent.put("desc", content.desc)
        jsonContent.put("date", content.date?.toStringNumeric() ?: "")
        jsonContent.put("args", content.args)

        val json = JSONObject()
        json.put("meta", jsonMeta)
        json.put("sender", jsonSender)
        json.put("recipient", jsonRecipient)
        json.put("content", jsonContent)
        json.put("data", data)

        val text = json.toString()
        return text
    }


    companion object {

        val empty = of(Meta.empty)


        @JvmStatic
        fun of(meta: Meta): Message {
            return Message(
                meta = meta,
                sender = Sender.empty,
                recipient = Recipient.empty,
                content = Content.empty,
                data = ""
            )
        }
    }
}

