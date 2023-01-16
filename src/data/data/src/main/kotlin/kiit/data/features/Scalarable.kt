package kiit.data.features

import kiit.common.data.Values
import kiit.data.features.Inspectable
import kiit.query.Const
import kiit.query.Select

interface Scalarable<TId, T>: Inspectable<TId, T> where TId : Comparable<TId>, T: Any {

    fun count(builder: Select.() -> Unit): Double = scalar { agg(dialect.aggr.count, Const.All); builder(this) }
    fun sum  (field:String, builder: Select.() -> Unit): Double = scalar { agg(dialect.aggr.sum, field); builder(this) }
    fun avg  (field:String, builder: Select.() -> Unit): Double = scalar { agg(dialect.aggr.avg, field); builder(this) }
    fun min  (field:String, builder: Select.() -> Unit): Double = scalar { agg(dialect.aggr.min, field); builder(this) }
    fun max  (field:String, builder: Select.() -> Unit): Double = scalar { agg(dialect.aggr.max, field); builder(this) }

    fun scalar(sql:String, args:Values): Double
    fun scalar(builder: Select.() -> Unit): Double
}
