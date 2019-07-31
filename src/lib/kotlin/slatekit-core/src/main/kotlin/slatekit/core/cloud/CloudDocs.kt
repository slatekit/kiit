package slatekit.core.slatekit.core.cloud

import slatekit.results.Outcome

interface CloudDoc<TPartition, TCluster> {
    val partition:TPartition

    val cluster:TCluster

    val fields:Map<String, Any?>

    val source:Any
}


interface CloudDocs<TPartition, TCluster> {
    fun create(doc:CloudDoc<TPartition, TCluster>)
    fun get(partition:TPartition): Outcome<CloudDoc<TPartition, TCluster>>
    fun get(partition:TPartition, cluster:TCluster): Outcome<CloudDoc<TPartition, TCluster>>
    fun update(doc:CloudDoc<TPartition, TCluster>)
    fun delete(doc:CloudDoc<TPartition, TCluster>)
}