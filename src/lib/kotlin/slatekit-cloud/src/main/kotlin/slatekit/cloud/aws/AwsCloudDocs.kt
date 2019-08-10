package slatekit.cloud.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import slatekit.core.slatekit.core.cloud.CloudDoc
import slatekit.core.slatekit.core.cloud.CloudDocs

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import com.amazonaws.services.dynamodbv2.document.PrimaryKey
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try


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


open class AwsCloudDoc<TPartition, TCluster>(override val partition:TPartition,
                                             override val cluster:TCluster,
                                             override val fields:Map<String, Any?>,
                                             override val source:Any
) : CloudDoc<TPartition, TCluster> {

    constructor(partition:TPartition,
                cluster:TCluster,
                fields:Map<String, Any?>): this(partition, cluster, fields, fields)
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


    override fun create(entity:TEntity): Try<TEntity> {
        return Try.attemptWithStatus {
            val item = mapper.toDoc(entity)
            val result = table.putItem(item)
            entity
            Success(entity)
        }
    }

    override fun update(entity:TEntity): Try<TEntity> {
        Failure(Exception("DynamoDB.update : Not implemented"))
    }


    override fun delete(entity:TEntity): Try<TEntity> {
        Try {
            val keys = mapper.keys(entity)
            val spec = new DeleteItemSpec().withPrimaryKey(new PrimaryKey(partitionName, keys._1, clusterName, keys._2))
            val item = table.deleteItem(spec)
            entity
        }
    }



    override fun get(partition: TPartition): Outcome<CloudDoc<TPartition, TCluster>> {
        return Outcomes.invalid()
    }

    override fun get(partition: TPartition, cluster: TCluster): Outcome<CloudDoc<TPartition, TCluster>> {
        val spec = GetItemSpec().withPrimaryKey(partitionName, partition, clusterName, cluster)

        return try {
            println("Attempting to read the item...")
            val item = table.getItem(spec)
            println("GetItem succeeded: $item")
            val doc = AwsCloudDoc(partition, cluster, item.asMap(), item)
            Outcomes.success(doc)
        } catch (ex: Exception) {
            System.err.println("Unable to read item: $partition $cluster")
            System.err.println(ex.message)
            Outcomes.unexpected(ex)
        }
    }

    override fun update(doc: CloudDoc<TPartition, TCluster>)  {

    }

    override fun delete(doc: CloudDoc<TPartition, TCluster>) {
        val deleteItemSpec = DeleteItemSpec()
                .withPrimaryKey(PrimaryKey(partitionName, doc.partition, clusterName, doc.cluster))

        // Conditional delete (we expect this to fail)

        try {
            println("Attempting a conditional delete...")
            table.deleteItem(deleteItemSpec)
            println("DeleteItem succeeded")
        } catch (e: Exception) {
            System.err.println("Unable to delete item: ${doc.partition} ${doc.cluster}")
            System.err.println(e.message)
        }

    }
}