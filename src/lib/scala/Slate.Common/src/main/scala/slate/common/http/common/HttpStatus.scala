/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.http.common

sealed class HttpStatus(val code:Int, val message:String) {
}


object HttpStatus {

  val sOk  = new HttpStatus(200, "OK")
  val sErr = new HttpStatus(400, "Bad Request")
  val s200 = new HttpStatus(200, "OK")
  val s201 = new HttpStatus(201, "Created")
  val s202 = new HttpStatus(202, "Accepted")
  val s203 = new HttpStatus(203, "Non-Authoritative Information (since HTTP/1.1)")
  val s204 = new HttpStatus(204, "No Content")
  val s205 = new HttpStatus(205, "Reset Content")
  val s206 = new HttpStatus(206, "Partial Content (RFC 7233)")
  val s207 = new HttpStatus(207, "Multi-Status (WebDAV RFC 4918)")
  val s208 = new HttpStatus(208, "Already Reported (WebDAV RFC 5842)")
  val s226 = new HttpStatus(226, "IM Used (RFC 3229)")

  val s300 = new HttpStatus(300, "Multiple Choices")
  val s301 = new HttpStatus(301, "Moved Permanently")
  val s302 = new HttpStatus(302, "Found")
  val s303 = new HttpStatus(303, "See Other (since HTTP/1.1)")
  val s304 = new HttpStatus(304, "Not Modified (RFC 7232)")
  val s305 = new HttpStatus(305, "Use Proxy (since HTTP/1.1)")
  val s306 = new HttpStatus(306, "Switch Proxy")
  val s307 = new HttpStatus(307, "Temporary Redirect (since HTTP/1.1)")
  val s308 = new HttpStatus(308, "Permanent Redirect (RFC 7538)")

  val s400 = new HttpStatus(400, "Bad Request")
  val s401 = new HttpStatus(401, "Unauthorized (RFC 7235)")
  val s402 = new HttpStatus(402, "Payment Required")
  val s403 = new HttpStatus(403, "Forbidden")
  val s404 = new HttpStatus(404, "Not Found")
  val s405 = new HttpStatus(405, "Method Not Allowed")
  val s406 = new HttpStatus(406, "Not Acceptable")
  val s407 = new HttpStatus(407, "Proxy Authentication Required (RFC 7235)")
  val s408 = new HttpStatus(408, "Request Timeout")
  val s409 = new HttpStatus(409, "Conflict")
  val s410 = new HttpStatus(410, "Gone")
  val s411 = new HttpStatus(411, "Length Required")
  val s412 = new HttpStatus(412, "Precondition Failed (RFC 7232)")
  val s413 = new HttpStatus(413, "Payload Too Large (RFC 7231)")
  val s414 = new HttpStatus(414, "URI Too Long (RFC 7231)")
  val s415 = new HttpStatus(415, "Unsupported Media Type")
  val s416 = new HttpStatus(416, "Range Not Satisfiable (RFC 7233)")
  val s417 = new HttpStatus(417, "Expectation Failed")
  val s418 = new HttpStatus(418, "I'm a teapot (RFC 2324)")
  val s421 = new HttpStatus(421, "Misdirected Request (RFC 7540)")
  val s422 = new HttpStatus(422, "Unprocessable Entity (WebDAV RFC 4918)")
  val s423 = new HttpStatus(423, "Locked (WebDAV RFC 4918)")
  val s424 = new HttpStatus(424, "Failed Dependency (WebDAV RFC 4918)")
  val s426 = new HttpStatus(426, "Upgrade Required")
  val s428 = new HttpStatus(428, "Precondition Required (RFC 6585)")
  val s429 = new HttpStatus(429, "Too Many Requests (RFC 6585)")
  val s431 = new HttpStatus(431, "Request Header Fields Too Large (RFC 6585)")
  val s451 = new HttpStatus(451, "Unavailable For Legal Reasons")
  val s500 = new HttpStatus(500, "Internal Server Error")
  val s501 = new HttpStatus(501, "Not Implemented")
  val s502 = new HttpStatus(502, "Bad Gateway")
  val s503 = new HttpStatus(503, "Service Unavailable")
  val s504 = new HttpStatus(504, "Gateway Timeout")
  val s505 = new HttpStatus(505, "HTTP Version Not Supported")
  val s506 = new HttpStatus(506, "Variant Also Negotiates (RFC 2295)")
  val s507 = new HttpStatus(507, "Insufficient Storage (WebDAV RFC 4918)")
  val s508 = new HttpStatus(508, "Loop Detected (WebDAV RFC 5842)")
  val s510 = new HttpStatus(510, "Not Extended (RFC 2774)")
  val s511 = new HttpStatus(511, "Network Authentication Required (RFC 6585)")
}

