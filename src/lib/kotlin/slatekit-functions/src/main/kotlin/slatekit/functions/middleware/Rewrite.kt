package slatekit.functions.middleware


/**
 * A "Rewriter" based middle-ware allows allows for rewriting the API path/call
 *
 */
interface Rewrite<TReq> : Middleware {

    /**
     * Rewrites the calls and returns a new request
     * @param req : The request ( e.g. call )
     */
    suspend fun onRewrite(req: TReq): TReq
}
