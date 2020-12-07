package slatekit.common

import slatekit.results.Status

/**
 * Used for diagnostics / alerts to represent some event that occurred.
 * This event is considered a "Structured Event" in the sense it has contextual
 * information about what happened ( see fields below ). This can then be used for:
 *
 * 1. logging     : Structured logging ( e.g. with specific fields / metadata )
 * 2. alerting    : To 3rd party service
 * 3. analytics   : To capture details
 * 4. diagnostics : Of component / state changes ( e.g of app/job etc )
 *
 * @param uuid   : unique id ( UUID ) identification
 * @param area   : logical group/project this alert is associated with
 * @param name   : name of the event        e.g. "SIGNUP"
 * @param agent  : application / service sending this alert
 * @param env    : environment of this alert e.g. dev | qat | pro
 * @param inst   : instance name e.g. of server or node id
 * @param action : action being performed ( e.g. "register" )
 * @param desc   : description of the alert e.g. "User registration via mobile"
 * @param status : status code for alert ( success/failure/etc )
 * @param source : name / id of the source/origin if applicable
 * @param target : name / id of the target/destination if applicable
 * @param time   : timestamp of this event
 * @param tag    : correlation id/tag for linking to other items
 * @param fields : Optional list of additional fields 1=key, 2=value, 3=tag
 *
 * @sample
 * Event(
 *      uuid   = "abc-123-xyz",
 *      area   = "accounts",
 *      name   = "signup",
 *      agent  = "api",
 *      env    = "pro",
 *      inst   = "server-123",
 *      action = "register",
 *      desc   = "User registration via mobile",
 *      status = Codes.SUCCESS,
 *      source = "android",
 *      target = "user-123",
 *      tag    = "a1b2c3",
 *      time   = DateTime.now()
 *      fields = listOf(
 *          Triple( "region" , "usa"     , "" ),
 *          Triple( "device" , "android" , "" )
 *      )
 *  )
 */
data class Event(
        @JvmField val uuid: String,
        @JvmField val area: String,
        @JvmField val name: String,
        @JvmField val agent: String,
        @JvmField val env: String,
        @JvmField val inst: String,
        @JvmField val action: String,
        @JvmField val desc: String,
        @JvmField val status: Status,
        @JvmField val source: String,
        @JvmField val target: String,
        @JvmField val time: DateTime,
        @JvmField val tag: String,
        @JvmField val fields: List<Triple<String, String, String>>?
)
