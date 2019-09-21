package slatekit.core.alerts

/**
 * @param area   : logical group/project this alert is associated with
 * @param agent  : application / service sending this alert
 * @param name   : name of the alert        e.g. "NEW_DEVICE_REGISTRATION"
 * @param env    : environment of this alert e.g. dev | qat | pro
 * @param uuid   : unique id ( UUID ) identification
 * @param desc   : description of the alert e.g. "User registration via mobile"
 * @param code   : describes the intent of the alert ( success / failure, progress, etc )
 * @param target : name / id of the target/destination, should match the @see [AlertTarget.target]
 * @param tag    : correlation id/tag for linking to other items
 * @param fields : Optional list of additional fields related to the alert
 *
 * event : {
 *     area: 'registration',
 *     agent:'reg-service-prod',
 *     name: 'NEW_ANDROID_REGISTRATION',
 *     uuid: 'abc-123-xyz',
 *     desc: 'User registration via mobile',
 *     code: AlertCodes.SUCCESS,
 *     target: "registration-alerts",
 *     tag : "a1b2c3",
 *     fields: [
 *         { title: 'region' , value:'usa'     , pii: false },
 *         { title: 'device' , value:'android', pii: false }
 *     ]
 * }}
 */
data class Alert(
                 val area: String,
                 val agent: String,
                 val name: String,
                 val env : String,
                 val uuid: String,
                 val desc: String,
                 val code: AlertCode,
                 val target: String,
                 val tag: String,
                 val fields: List<AlertField>?
)