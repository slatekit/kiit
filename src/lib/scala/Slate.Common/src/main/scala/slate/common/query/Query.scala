package slate.common.query

import slate.common.Strings
import slate.common.Strings._
import slate.common.databases.{DbConstants}
import scala.collection.mutable.ListBuffer

/**
  * Created by kreddy on 12/24/2015.
  */
class Query extends IQuery {

  class QueryData(val conditions:ListBuffer[ICondition],
                  val updates:ListBuffer[FieldValue])
  {
  }


  protected var _limit = 0
  protected val EmptyString = "''"
  protected val _data = new QueryData(new ListBuffer[ICondition](), new ListBuffer[FieldValue]())


  def toUpdates():List[FieldValue] =
  {
    _data.updates.toList
  }


  def toUpdatesText(): String =
  {
    // No updates ?
    if(_data.updates.size > 0 ) {
      // Build up the sql
      val text = ListBuffer("SET ")

      // Each update
      val updates = mkString[FieldValue]( _data.updates, f => {
        f.fieldValue match {
          case DbConstants.EmptyString => f.field + "=" + "''"
          case _                       => f.field + "=" + QueryEncoder.convertVal(f.fieldValue)
        }
      }, ", ")
      text += updates

      // Filters
      if (anyConditions) text += " WHERE " + toFilter()
      if (anyLimit) text += " LIMIT " + _limit

      val sql = text.reduce((a, b) => a + b)
      sql
    }
    else
      Strings.empty
  }


  def toFilter(): String =
  {
    val filter = mkString[ICondition](_data.conditions, d => d.toStringQuery())
    filter
  }



  /**
    * builds up a set field clause
    *
    * @param field
    * @param fieldValue
    * @return
    */
  def set(field:String, fieldValue:Any) : IQuery = {
    val col = QueryEncoder.ensureField(field)
    _data.updates.append(new FieldValue(col, fieldValue))
    this
  }


  /**
    * builds up a where clause with the supplied arguments
    *
    * @param field:  The field name
    * @param compare: The comparison operator ( =, >, >=, <, <=, != )
    * @param fieldValue: The field value
    * @return this instance
    */
  def where(field:String, compare:String, fieldValue:Any) : IQuery =
  {
    val condition = buildCondition(field, compare, fieldValue)
    _data.conditions.append(condition)
    this
  }


  /**
   * builds up a where clause with the supplied arguments
    *
    * @param field:  The field name
   * @param compare: The comparison operator ( =, >, >=, <, <=, != )
   * @param fieldValue: The field value
   * @return this instance
   */
  def where(field:String, compare:Op, fieldValue:Any) : IQuery =
  {
    where(field, compare.value, fieldValue)
  }


  /**
    * adds an and clause with the supplied arguments
    *
    * @param field:  The field name
    * @param compare: The comparison operator ( =, >, >=, <, <=, != )
    * @param fieldValue: The field value
    * @return this instance
    */
  def and(field:String, compare:String, fieldValue:Any) : IQuery =
  {
    val cond = buildCondition(field, compare, fieldValue);
    group("and", cond)
    this
  }


  def and(field:String, compare:Op, fieldValue:Any) : IQuery =
  {
    and(field, compare.value, fieldValue)
  }


  /**
    * adds an or clause with the supplied arguments
    *
    * @param field:  The field name
    * @param compare: The comparison operator ( =, >, >=, <, <=, != )
    * @param fieldValue: The field value
    * @return this instance
    */
  def or(field:String, compare:String, fieldValue:Any) : IQuery = {
    val cond = buildCondition(field, compare, fieldValue)
    group("or", cond)
    this
  }


  def or(field:String, compare:Op, fieldValue:Any) : IQuery =
  {
    or(field, compare.value, fieldValue)
  }


  def limit(max: Int): IQuery =
  {
    this._limit = max
    this
  }


  def orderBy ( field:String ): IQuery = this


  def asc (  ): IQuery = this


  def desc (  ): IQuery = this


  protected  def buildCondition(field:String, compare:String, fieldValue:Any): Condition =
  {
    val col = QueryEncoder.ensureField(field)
    val comp = QueryEncoder.ensureCompare(compare)
    val con = new Condition(col, comp, fieldValue)
    con
  }


  protected def group(op:String, condition:Condition):Unit =
  {
    // Pop the last one
    val last = _data.conditions.size - 1
    val left = _data.conditions(last)
    _data.conditions.remove(last)

    // Build a binary condition from left and right
    val group = new ConditionGroup(left, op, condition)

    // Push back on condition list
    _data.conditions.append(group)
  }


  protected def anyLimit : Boolean = _limit > 0

  protected def anyConditions:Boolean = _data.conditions.size > 0
}
