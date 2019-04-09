package slatekit.core.alerts

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import slatekit.common.HttpRPC
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success

class AlertServiceSlack(override val settings: AlertSettings) : AlertService() {

    private val baseUrl = "https://hooks.slack.com/services"

    override fun send(alert: Alert, target: AlertTarget): Notice<Boolean> {

        // Notify insights channel ( if enabled ).
        return this.notify(target) {
            val json = build(alert, target)
            val jsonText = json.toJSONString()
            send(target, jsonText)
        }
    }


    fun build(alert:Alert, target:AlertTarget):JSONObject {
        // Convert the code to a color
        // 1. code 0 = pending = yellow
        // 2. code 1 = success = green
        // 3. code 2 = failure = red
        val color = when (alert.status) {
            1 -> "#2ecc71"
            2 -> "#e74c3c"
            else -> "#F0E68C"
        }

        // Convert all the fields supplied to a slack field definition
        val json = JSONObject()
        json.put("channel", "#${target.name}")
        json.put("username", target.sender)
        json.put("icon_emoji", ":slack:")

        val attachment = JSONObject()
        val fields = JSONArray()
        attachment.put("fallback", alert.name)
        attachment.put("pretext", alert.desc)
        attachment.put("color", color)
        attachment.put("title", alert.name)
        attachment.put("fields", fields)

        // 1 attachment only
        val attachments = JSONArray()
        json.put("attachments", attachments)
        attachments.add(attachment)

        // Fields
        alert.fields?.forEach {

            val field = JSONObject()
            field.put("title", it.name)
            field.put("value", it.value ?: "")
            field.put("short", true)
            fields.add(field)
        }
        return json
    }


    private fun notify(target: AlertTarget, call: () -> Notice<Boolean>): Notice<Boolean> {
        if (target.enabled) {
            return call()
        }
        return Failure("Target ${target.target} is disabled")
    }


    /**
     *
     * curl -X POST --data-urlencode
     * channel : #wellow-insights
     * hook    : https://hooks.slack.com/services/T00000000/B00000000/t00000000000000000000000
     * payload : { ... }
     */
    private fun send(target: AlertTarget, json: String):Notice<Boolean> {
        val url = "$baseUrl/${target.account}/${target.channel}/${target.key}"
        val result = HttpRPC().sendSync(
                method = HttpRPC.Method.Post,
                url = url,
                headers = mapOf("Content-type" to "application/json"),
                body = HttpRPC.Body.JsonContent(json)
            )
        return result.fold({ Success(true) }, { Failure(it.message ?: "") })
    }
}