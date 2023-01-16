package kiit.core.docs

interface CloudDoc<TEntity, TPartition, TCluster> {
    val partition:TPartition

    val cluster:TCluster

    val fields:Map<String, Any?>

    val source:Any
}