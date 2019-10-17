package slatekit.common.metrics

import slatekit.common.Identity
import slatekit.common.log.Logger
import slatekit.results.Status

/**
 * Used for diagnostics / alerts to represent some event that occurred.
 * This event is considered a "Structured Event" in the sense it has contextual
 * information about what happened ( see fields below ). This can then be used for:
 *
 * 1. structured logging      ( e.g. with specific fields / metadata )
 * 2. alerting of the event   ( e.g. via slack )
 * 3. analytics for the event
 *
 * @param area   : logical group/project this alert is associated with
 * @param agent  : application / service sending this alert
 * @param name   : name of the alert        e.g. "NEW_DEVICE_REGISTRATION"
 * @param env    : environment of this alert e.g. dev | qat | pro
 * @param uuid   : unique id ( UUID ) identification
 * @param desc   : description of the alert e.g. "User registration via mobile"
 * @param status : status code for alert ( success/failure/etc )
 * @param target : name / id of the target/destination if applicable
 * @param tag    : correlation id/tag for linking to other items
 * @param fields : Optional list of additional fields 1=key, 2=value, 3=tag
 *
 * event : {
 *     area: 'registration',
 *     agent:'reg-service-prod',
 *     name: 'NEW_ANDROID_REGISTRATION',
 *     uuid: 'abc-123-xyz',
 *     desc: 'User registration via mobile',
 *     status: Codes.SUCCESS,
 *     target: "registration-alerts",
 *     tag : "a1b2c3",
 *     fields: [
 *         { 'region' , 'usa'     , '' },
 *         { 'device' , 'android' , '' }
 *     ]
 * }
 */
data class Event(
        val area: String,
        val agent: String,
        val name: String,
        val env : String,
        val uuid: String,
        val desc: String,
        val status: Status,
        val target: String,
        val tag: String,
        val fields: List<Triple<String, String, String>>?
) {

    companion object {

        fun log(logger:Logger, id: Identity, event:Event){
            val extra = event.fields?.fold("") { acc, info -> acc + ", ${info.first}=${info.second}" }
            when(event.status) {
                is Status.Succeeded  -> logger.info ("id=${id.id}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=true , code=${event.status.code}, desc=${event.desc} $extra")
                is Status.Pending    -> logger.info ("id=${id.id}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=true , code=${event.status.code}, desc=${event.desc} $extra")
                is Status.Ignored    -> logger.info ("id=${id.id}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${event.status.code}, desc=${event.desc} $extra")
                is Status.Invalid    -> logger.error("id=${id.id}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${event.status.code}, desc=${event.desc} $extra")
                is Status.Denied     -> logger.error("id=${id.id}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${event.status.code}, desc=${event.desc} $extra")
                is Status.Errored    -> logger.error("id=${id.id}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${event.status.code}, desc=${event.desc} $extra")
                is Status.Unexpected -> logger.error("id=${id.id}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${event.status.code}, desc=${event.desc} $extra")
                else                 -> logger.error("id=${id.id}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${event.status.code}, desc=${event.desc} $extra")

            }
        }


        fun logger(logger: Logger, id: Identity): (Event) -> Unit {
            return { event:Event ->
              Event.log(logger, id, event)
            }
        }
    }
}