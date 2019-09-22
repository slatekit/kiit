package slatekit.common.metrics

import slatekit.common.ids.Identity
import slatekit.common.log.Logger
import slatekit.results.Status

/**
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
 * }}
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

        fun log(logger:Logger, id:Identity, status:Status, event:Event){
            val extra = event.fields?.fold("") { acc, info -> acc + ", ${info.first}=${info.second}" }
            when(status) {
                is Status.Succeeded  -> logger.info ("id=${id.fullName}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=true , code=${status.code}, desc=${event.desc} $extra")
                is Status.Pending    -> logger.info ("id=${id.fullName}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=true , code=${status.code}, desc=${event.desc} $extra")
                is Status.Ignored    -> logger.info ("id=${id.fullName}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${status.code}, desc=${event.desc} $extra")
                is Status.Invalid    -> logger.error("id=${id.fullName}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${status.code}, desc=${event.desc} $extra")
                is Status.Denied     -> logger.error("id=${id.fullName}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${status.code}, desc=${event.desc} $extra")
                is Status.Errored    -> logger.error("id=${id.fullName}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${status.code}, desc=${event.desc} $extra")
                is Status.Unexpected -> logger.error("id=${id.fullName}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${status.code}, desc=${event.desc} $extra")
                else                 -> logger.error("id=${id.fullName}, area=${event.area}, name=${event.name}, uuid=${event.uuid}, success=false, code=${status.code}, desc=${event.desc} $extra")

            }
        }
    }
}