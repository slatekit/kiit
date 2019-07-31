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
import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.document.PrimaryKey
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec




open class AwsCloudDoc<TPartition, TCluster>(override val partition:TPartition,
                                             override val cluster:TCluster,
                                             override val fields:Map<String, Any?>,
                                             override val source:Any
) : CloudDoc<TPartition, TCluster> {

    constructor(partition:TPartition,
                cluster:TCluster,
                fields:Map<String, Any?>): this(partition, cluster, fields, fields)
}


class AwsCloudDocs<TPartition, TCluster>(
        val region:String,
        val tableName: String,
        val partitionName:String,
        val clusterName:String,
        creds: AWSCredentials) : CloudDocs<TPartition, TCluster>{


    private val client = AmazonDynamoDBClientBuilder.standard().withCredentials(AWSStaticCredentialsProvider(creds)).build()
    private val dynamoDB = DynamoDB(client)
    private val table = dynamoDB.getTable(tableName)


    override fun create(doc: CloudDoc<TPartition, TCluster>) {
        try {
            val item = Item().withPrimaryKey(partitionName, doc.partition, clusterName, doc.cluster)
            doc.fields.forEach { name, value ->
                when(value) {
                    null       -> item.withNull(name)
                    is Int     -> item.withInt(name, value )
                    is String  -> item.withString(name, value )
                    is Boolean -> item.withBoolean(name, value )
                    else       -> item.withString(name, value.toString())
                }
            }
            val outcome = table.putItem(item)
            println("PutItem succeeded:\n" + outcome.putItemResult)

        } catch (e: Exception) {
            System.err.println("Unable to add item: ${doc.partition} ${doc.cluster}")
            System.err.println(e.message)
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