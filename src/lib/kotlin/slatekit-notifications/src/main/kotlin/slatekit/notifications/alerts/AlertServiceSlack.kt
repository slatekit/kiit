package slatekit.notifications.alerts

import okhttp3.Request
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import slatekit.common.HttpRPC
import slatekit.common.ids.Identity
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

/**
 * @param settings: The list of slack channels
 */
class AlertServiceSlack(override val identity: Identity,
                        override val settings: AlertSettings) : AlertService() {

    private val baseUrl = "https://hooks.slack.com/services"

    /**
     * Whether or not sending is enabled
     */
    override fun isEnabled(model:Alert):Boolean {
        val target = this.settings.targets.firstOrNull { it.target == model.target }
        return target?.enabled ?: false
    }


    /**
     * Validates the model supplied
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    override fun validate(model: Alert): Outcome<Alert> {
        val target = this.settings.targets.firstOrNull { it.target == model.target }
        return when {
            model.target.isNullOrEmpty() -> Outcomes.invalid("target not provided")
            model.name.isNullOrEmpty()   -> Outcomes.invalid("name not provided")
            target == null               -> Outcomes.invalid("target invalid")
            else -> Outcomes.success(model)
        }
    }


    /**
     * Builds the HttpRequest for the model
     * @param model: The data model to send ( e.g. Alert )
     */
    override fun build(model: Alert): Outcome<Request> {
        // Parameters
        val target = settings.targets.first { it.target == model.target }
        val url = "$baseUrl/${target.account}/${target.channel}/${target.key}"
        val json = build(model, target)
        val jsonString = json.toString()
        val request = HttpRPC().build(
                method = HttpRPC.Method.Post,
                urlRaw = url,
                headerParams = mapOf("Content-type" to "application/json"),
                body  = HttpRPC.Body.JsonContent(jsonString))
        return Success(request)
    }


    fun build(alert: Alert, target: AlertTarget):JSONObject {
        // Convert the code to a color
        // 1. code 0 = pending = yellow
        // 2. code 1 = success = green
        // 3. code 2 = failure = red
        val color = alert.code.color

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


//    /**
//     *
//     * curl -X POST --data-urlencode
//     * channel : #service1-alerts
//     * hook    : https://hooks.slack.com/services/T00000000/B00000000/t00000000000000000000000
//     * payload : { ... }
//     */
//    private fun send(target: AlertTarget, json: String):Notice<Boolean> {
//        val url = "$baseUrl/${target.account}/${target.channel}/${target.key}"
//        val result = HttpRPC().sendSync(
//                method = HttpRPC.Method.Post,
//                url = url,
//                headers = mapOf("Content-type" to "application/json"),
//                body = HttpRPC.Body.JsonContent(json)
//        )
//        return result.fold({ Success(true) }, { Failure(it.message ?: "") })
//    }
}