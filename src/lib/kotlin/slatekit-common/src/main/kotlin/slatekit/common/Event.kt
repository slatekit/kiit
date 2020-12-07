package slatekit.common

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
 * @sample
 * Event(
 *      area   = "signup",
 *      name   = "registration",
 *      action = "created",
 *      agent  = "api",
 *      env    = "pro",
 *      uuid   = "abc-123-xyz",
 *      desc   = "User registration via mobile",
 *      status = Codes.SUCCESS,
 *      source = "android",
 *      target = "2.1",
 *      tag    = "a1b2c3",
 *      fields = listOf(
 *          Triple( "region" , "usa"     , "" ),
 *          Triple( "device" , "android" , "" )
 *      )
 *  )
 */
data class Event(
        @JvmField val area: String,
        @JvmField val name: String,
        @JvmField val action: String,
        @JvmField val agent: String,
        @JvmField val env : String,
        @JvmField val uuid: String,
        @JvmField val desc: String,
        @JvmField val status: Status,
        @JvmField val source: String,
        @JvmField val target: String,
        @JvmField val tag: String,
        @JvmField val fields: List<Triple<String, String, String>>?
)
