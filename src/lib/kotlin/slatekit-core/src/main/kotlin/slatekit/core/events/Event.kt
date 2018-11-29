package slatekit.core.events

import slatekit.common.Inputs
import slatekit.common.Meta

/**
@param path     : app.share.shareNew
@param batch    : batch or aggregate group id
@param verb     : create
@param type     : blend.core.actions.ShareCreate
@param source   : app
@param origin   : controller.BillController
@param meta     : { type:'blend.core.models.Bill' },
@param data     : { id:12, uuid: 'abc123', type:'blend.core.models.Bill', data:"" }
@param tag      : "qwebj3341883bmdasd",
@param version  : "1.0",
@param timestamp: 2018-11-27T16:30:30Z
 **/
data class Event(
        val uuid     : String,  // 9bb40eea-02e9-447b-9a35-12d5a7541302
        val name     : String,  // app.share.shareNew
        val batch    : String,  // batch or aggregate id
        val type     : String,  // blend.core.actions.ShareCreate
        val source   : String,  // app
        val origin   : String,  // controller.BillController
        val verb     : String,  // create
        val meta     : Meta  ,  // { type:'blend.core.models.Bill' },
        val data     : Inputs,  // { id:12, uuid: 'abc123', type:'blend.core.models.Bill', data:"" }
        val tag      : String,  // "qwebj3341883bmdasd",
        val version  : String,  // "1.0",
        val timestamp: Long     // 20181127163030
)