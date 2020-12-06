package slatekit.jobs


/**
 * @param name   : "ALL" or job name or worker name ( see below )
 * @param seconds: Optional number of seconds for pause commands
 * @param desc   : Optional description when running a command
 * 1. "ALL"      : To represent all jobs or all workers
 * 2. Job name   : "{Identity.area}.{Identity.service}"                     e.g. "signup.emails"
 * 3. Worker name: "{Identity.area}.{Identity.service}.{Identity.instance}" e.g. "signup.emails.worker_1"
 */
data class Request(val name:String, val seconds:Int? = null, val desc:String = "") {
}
