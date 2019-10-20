package slatekit
/*
import com.amazonaws.services.dynamodbv2.document.Item
import slatekit.cloud.aws.AwsCloudDocs
import slatekit.cloud.aws.AwsDocMapper
import slatekit.cloud.aws.AwsFuncs
import slatekit.common.conf.Config
import slatekit.common.Identity
import slatekit.common.SimpleIdentity


//typealias SimpleDoc = AwsCloudDoc<String, String>

object TestDocs {


    fun test(){
        val config = Config()
        val key = config.apiLogin("aws")
        val creds = AwsFuncs.credsWithKeySecret(key.key, key.pass)
        val mapper = SampleDocMapper(SimpleIdentity("usa", "all"), "name", "id")
        val docs = AwsCloudDocs<SampleDoc, String, String>("us-east-1", "test1", "name", "id", mapper, creds)


        val doc = SampleDoc("blend-server", "worker1", true, 200, "paused")
        //docs.create(doc)
        val item = docs.get("blend-server", "worker2")
        println("done")
    }
}


data class SampleDoc(val name:String, val id:String, val active:Boolean, val processed:Int, val status:String )

/**
 * Conversion to/from no sql
 * @tparam TPartition
 * @tparam TCluster
 */
class SampleDocMapper(
        val id:Identity,
        val partitionName:String,
        val clusterName:String) : AwsDocMapper<SampleDoc, String, String> {

    override fun keys(entity:SampleDoc):Pair<String, String> {
        return Pair(entity.name, id.name)
    }


    override fun toDoc(entity: SampleDoc): Item  {
        val item = Item().withPrimaryKey(partitionName, entity.name, clusterName, entity.name)
        item.withBoolean("active", entity.active)
        item.withInt("processed", entity.processed)
        item.withString("status", entity.status)
        return item
    }


    override fun ofDoc(doc: Item, partition:String, cluster:String): SampleDoc {
        return SampleDoc(
                partition,
                cluster,
                doc.getBoolean("active"),
                doc.getInt("processed"),
                doc.getString("status")
        )
    }
}
*/