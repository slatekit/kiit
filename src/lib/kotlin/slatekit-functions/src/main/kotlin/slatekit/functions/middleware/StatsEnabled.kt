package slatekit.functions.middleware

import slatekit.common.metrics.Recorder


interface StatsEnabled<TReq, TRes> {
    fun getRecorder(req:TReq): Recorder<TReq, TRes>
}