	/**
    @{methodDesc}
	The tag parameter is used as a "correlation id"
    */
    fun @{methodName}(
        @{methodParams}
		tag:String,
        callback: (@{methodReturnType}) -> Unit
    )
    {
        // headers
        val headers = mutableMapOf<String,String>()

        // query string
        val queryParams = mutableMapOf<String,String>()
        @{queryParams}

        // data
        @{postDataDecl}
        @{postDataVars}

        // execute
        _http.@{verb}(
            "@{route}",
            headers,
            queryParams,
            @{postDataParam}
			tag,
			Converter.Converter@{converterClass}<>(@{converterTypes}),
            callback
        );
    }