package slatekit.functions.features

import slatekit.results.Outcome


interface Feature<TReq, TRes> {
    suspend fun handle(req:TReq, res:Outcome<TRes>): Outcome<TRes>
}