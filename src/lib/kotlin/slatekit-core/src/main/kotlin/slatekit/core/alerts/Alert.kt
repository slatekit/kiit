package slatekit.core.alerts

/**
 * @param area   : logical area of an application
 * @param name   : name of the alert        e.g. "NEW_DEVICE_REGISTRATION"
 * @param uuid   : unique id ( UUID ) identification
 * @param desc   : description of the alert e.g. "User registration via mobile"
 * @param success: whether or not this is a success or failure
 * @param status : integer code for more specific status
 * @param target : name / id of the target/destination, should match the @see [AlertTarget.target]
 * @param tag    : correlation id/tag for linking to other items
 * @param fields : Optional list of additional fields related to the alert
 *
 * event : {
 *     area: 'app.registration.new',
 *     name: 'NEW_DEVICE_REGISTRATION',
 *     uuid: 'abc-123-xyz',
 *     desc: 'User registration via mobile',
 *     success: true,
 *     code: 200,
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
                 val name: String,
                 val uuid: String,
                 val desc: String,
                 val success: Boolean,
                 val status: Int,
                 val target: String,
                 val tag: String,
                 val fields: List<AlertField>?
)