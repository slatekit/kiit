package kiit.core.docs

import kiit.results.Outcome


interface CloudDocs<TEntity, TPartition, TCluster> {
    fun create(entity: TEntity)  : Outcome<TEntity>
    fun update(entity:TEntity)   : Outcome<TEntity>
    fun delete(entity:TEntity)   : Outcome<TEntity>
    fun get(partition:TPartition): Outcome<TEntity>
    fun get(partition:TPartition, cluster:TCluster): Outcome<TEntity>
}