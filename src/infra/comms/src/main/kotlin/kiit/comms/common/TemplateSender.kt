package kiit.comms.common

import kiit.common.values.Vars
import kiit.utils.templates.Templates
import kiit.results.Outcome
import kiit.results.builders.Outcomes

interface TemplateSender<T> : Sender<T> {
    val templates: Templates?


    suspend fun sendTemplate(template:String, variables: Vars, builder:(String) -> T): Outcome<String> {
        return templates?.let { t ->
            val message = t.resolveTemplateWithVars(template, variables.toMap())
            val model = builder(message ?: "")
            send(model)
        } ?: Outcomes.invalid("templates are not setup")
    }


    fun sendTemplateSync(template:String, variables: Vars, builder:(String) -> T):Outcome<String> {
        return templates?.let { t ->
            val message = t.resolveTemplateWithVars(template, variables.toMap())
            val model = builder(message ?: "")
            sendSync(model)
        } ?: Outcomes.invalid("templates are not setup")
    }
}
