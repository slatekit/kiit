	/**
    @{methodDesc}
	The tag parameter is used as a "correlation id"
    */
    fun @{methodName}(
        @{methodParams}
		tag:String,
        callback: (Outcome<@{methodReturnType}>) -> Unit
    )
    {
        // headers
        val headers = mapOf<String,String>()

        // query string
        val queryParams = mapOf<String,String>(
            @{queryParams}
        )

        // data
        val postData = mapOf<String, Any>(
            @{postDataVars}
        )
        val json = Conversions.convertMapToJson(postData)

        // convert
        val converter = Converter@{converterClass}<@{converterTypes}>(@{converterTypes}::class.java)

        // execute
        http.@{verb}(
            "@{route}",
            headers = headers,
            queryParams = queryParams,
            body = HttpRPC.Body.JsonContent(json),
            callback = { respond(it, converter, callback ) }
        )
    }