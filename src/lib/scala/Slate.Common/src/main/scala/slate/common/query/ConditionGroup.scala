package slate.common.query


class ConditionGroup(val left:Any, val operator:String, val right:Any ) extends ICondition{

  override def toStringQuery:String =
  {
    getString(left) + " " + operator + " " + getString(right)
  }


  private def getString(item:Any):String =
  {
    if(item.isInstanceOf[ICondition])
      return item.asInstanceOf[ICondition].toStringQuery()
    return item.toString
  }
}
