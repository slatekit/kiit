package slatekit.core.slatekit.core.cloud

import slatekit.results.Outcome

interface CloudDoc<TEntity, TPartition, TCluster> {
    val partition:TPartition

    val cluster:TCluster

    val fields:Map<String, Any?>

    val source:Any
}


interface CloudDocs<TEntity, TPartition, TCluster> {
    fun create(entity: TEntity)  : Outcome<TEntity>
    fun update(entity:TEntity)   : Outcome<TEntity>
    fun delete(entity:TEntity)   : Outcome<TEntity>
    fun get(partition:TPartition): Outcome<TEntity>
    fun get(partition:TPartition, cluster:TCluster): Outcome<TEntity>
}