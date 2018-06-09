/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.http


class HttpStatusCode(val code: Int, val message: String) {
}


object HttpStatusCodes {

    val sOk  = HttpStatusCode(200, "OK")
    val sErr = HttpStatusCode(400, "Bad Request")
    val s200 = HttpStatusCode(200, "OK")
    val s201 = HttpStatusCode(201, "Created")
    val s202 = HttpStatusCode(202, "Accepted")
    val s203 = HttpStatusCode(203, "Non-Authoritative Information (since HTTP/1.1)")
    val s204 = HttpStatusCode(204, "No Content")
    val s205 = HttpStatusCode(205, "Reset Content")
    val s206 = HttpStatusCode(206, "Partial Content (RFC 7233)")
    val s207 = HttpStatusCode(207, "Multi-Status (WebDAV RFC 4918)")
    val s208 = HttpStatusCode(208, "Already Reported (WebDAV RFC 5842)")
    val s226 = HttpStatusCode(226, "IM Used (RFC 3229)")

    val s300 = HttpStatusCode(300, "Multiple Choices")
    val s301 = HttpStatusCode(301, "Moved Permanently")
    val s302 = HttpStatusCode(302, "Found")
    val s303 = HttpStatusCode(303, "See Other (since HTTP/1.1)")
    val s304 = HttpStatusCode(304, "Not Modified (RFC 7232)")
    val s305 = HttpStatusCode(305, "Use Proxy (since HTTP/1.1)")
    val s306 = HttpStatusCode(306, "Switch Proxy")
    val s307 = HttpStatusCode(307, "Temporary Redirect (since HTTP/1.1)")
    val s308 = HttpStatusCode(308, "Permanent Redirect (RFC 7538)")

    val s400 = HttpStatusCode(400, "Bad Request")
    val s401 = HttpStatusCode(401, "Unauthorized (RFC 7235)")
    val s402 = HttpStatusCode(402, "Payment Required")
    val s403 = HttpStatusCode(403, "Forbidden")
    val s404 = HttpStatusCode(404, "Not Found")
    val s405 = HttpStatusCode(405, "Method Not Allowed")
    val s406 = HttpStatusCode(406, "Not Acceptable")
    val s407 = HttpStatusCode(407, "Proxy Authentication Required (RFC 7235)")
    val s408 = HttpStatusCode(408, "Request Timeout")
    val s409 = HttpStatusCode(409, "Conflict")
    val s410 = HttpStatusCode(410, "Gone")
    val s411 = HttpStatusCode(411, "Length Required")
    val s412 = HttpStatusCode(412, "Precondition Failed (RFC 7232)")
    val s413 = HttpStatusCode(413, "Payload Too Large (RFC 7231)")
    val s414 = HttpStatusCode(414, "URI Too Long (RFC 7231)")
    val s415 = HttpStatusCode(415, "Unsupported Media Type")
    val s416 = HttpStatusCode(416, "Range Not Satisfiable (RFC 7233)")
    val s417 = HttpStatusCode(417, "Expectation Failed")
    val s418 = HttpStatusCode(418, "I'm a teapot (RFC 2324)")
    val s421 = HttpStatusCode(421, "Misdirected Request (RFC 7540)")
    val s422 = HttpStatusCode(422, "Unprocessable Entity (WebDAV RFC 4918)")
    val s423 = HttpStatusCode(423, "Locked (WebDAV RFC 4918)")
    val s424 = HttpStatusCode(424, "Failed Dependency (WebDAV RFC 4918)")
    val s426 = HttpStatusCode(426, "Upgrade Required")
    val s428 = HttpStatusCode(428, "Precondition Required (RFC 6585)")
    val s429 = HttpStatusCode(429, "Too Many Requests (RFC 6585)")
    val s431 = HttpStatusCode(431, "Request Header Fields Too Large (RFC 6585)")
    val s451 = HttpStatusCode(451, "Unavailable For Legal Reasons")
    val s500 = HttpStatusCode(500, "Internal Server Error")
    val s501 = HttpStatusCode(501, "Not Implemented")
    val s502 = HttpStatusCode(502, "Bad Gateway")
    val s503 = HttpStatusCode(503, "Service Unavailable")
    val s504 = HttpStatusCode(504, "Gateway Timeout")
    val s505 = HttpStatusCode(505, "HTTP Version Not Supported")
    val s506 = HttpStatusCode(506, "Variant Also Negotiates (RFC 2295)")
    val s507 = HttpStatusCode(507, "Insufficient Storage (WebDAV RFC 4918)")
    val s508 = HttpStatusCode(508, "Loop Detected (WebDAV RFC 5842)")
    val s510 = HttpStatusCode(510, "Not Extended (RFC 2774)")
    val s511 = HttpStatusCode(511, "Network Authentication Required (RFC 6585)")
}

