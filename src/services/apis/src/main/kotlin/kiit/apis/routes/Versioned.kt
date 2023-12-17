package kiit.apis.routes

import kiit.apis.ApiConstants

data class Versioned(val name:String, val version:String = ApiConstants.zero)
