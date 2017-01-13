package slate.common.query


class ConditionGroup(val left:Any, val operator:String, val right:Any ) extends ICondition{

  override def toStringQuery:String =
  {
    getString(left) + " " + operator + " " + getString(right)
  }


  private def getString(item:Any):String =
  {
    item match {
      case null         => ""
      case s:ICondition => s.toStringQuery()
      case _            => item.toString
    }
  }
}
