	/**
    @{methodDesc}
	The tag parameter is used as a "correlation id"
    */
    public void @{methodName}(
        @{methodParams}
		String tag,
        Callback<@{methodReturnType}> callback
    )
    {
        // headers
        HashMap<String, String> headers = new HashMap<>();

        // query string
        HashMap<String, String> queryParams = new HashMap<>();
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
			new Converter.Converter@{converterClass}<>(@{converterTypes}),
            callback
        );
    }