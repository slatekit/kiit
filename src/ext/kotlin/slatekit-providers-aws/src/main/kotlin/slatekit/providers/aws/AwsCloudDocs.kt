package slatekit.providers.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import slatekit.core.docs.CloudDocs

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import com.amazonaws.services.dynamodbv2.document.PrimaryKey
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec


/**
 * Conversion to/from no sql
 * @tparam TPartition
 * @tparam TCluster
 */
interface AwsDocMapper<TEntity, TPartition, TCluster> {
    fun keys(entity:TEntity):Pair<TPartition, TCluster>
    fun toDoc(entity:TEntity):Item
    fun ofDoc(doc:Item, partition:TPartition, cluster:TCluster):TEntity
}



class AwsCloudDocs<TEntity, TPartition, TCluster>(
        val region:String,
        val tableName: String,
        val partitionName:String,
        val clusterName:String,
        val mapper:AwsDocMapper<TEntity, TPartition, TCluster>,
        creds: AWSCredentials) : CloudDocs<TEntity, TPartition, TCluster>{


    private val client = AmazonDynamoDBClientBuilder.standard().withCredentials(AWSStaticCredentialsProvider(creds)).build()
    private val dynamoDB = DynamoDB(client)
    private val table = dynamoDB.getTable(tableName)


    override fun create(entity:TEntity): Outcome<TEntity> {
        return Outcomes.of {
            val item = mapper.toDoc(entity)
            val result = table.putItem(item)
            entity
        }
    }

    override fun update(entity:TEntity): Outcome<TEntity> {
        return Outcomes.errored(Exception("DynamoDB.update : Not implemented"))
    }


    override fun delete(entity:TEntity): Outcome<TEntity> {
        return Outcomes.of {
            val keys = mapper.keys(entity)
            val spec = DeleteItemSpec().withPrimaryKey(PrimaryKey(partitionName, keys.first, clusterName, keys.second))
            val item = table.deleteItem(spec)
            entity
        }
    }


    override fun get(partition: TPartition): Outcome<TEntity> {
        return Outcomes.invalid()
    }


    override fun get(partition: TPartition, cluster: TCluster): Outcome<TEntity> {
        return Outcomes.of {
            val spec = GetItemSpec().withPrimaryKey(partitionName, partition, clusterName, cluster)
            val item = table.getItem(spec)
            val entity = mapper.ofDoc(item, partition, cluster)
            entity
        }
    }
}

/*
open class AwsCloudDoc<TPartition, TCluster>(override val partition:TPartition,
                                             override val cluster:TCluster,
                                             override val fields:Map<String, Any?>,
                                             override val source:Any
) : CloudDoc<TPartition, TCluster> {

    constructor(partition:TPartition,
                cluster:TCluster,
                fields:Map<String, Any?>): this(partition, cluster, fields, fields)
}
 */