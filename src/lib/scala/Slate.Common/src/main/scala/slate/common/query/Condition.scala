package slate.common.query


class Condition(val field:Any, val comparison:String, val fieldValue:Any) extends ICondition {

  /**
    * string represention of condition
    * @return
    */
   def toStringQuery:String =
  {
    toStringQueryWithOptions(false, "[", "]")
  }


  /**
   * Returns a String representation of this instance.
   * @param surround : True to surround alias with text.
   * @param left     : Left surrounding text
   * @param right    : Right surrounding text
   * @return
   */
  def toStringQueryWithOptions(surround:Boolean=false, left:String="", right:String="") : String =
  {
    val fieldName = QueryEncoder.ensureField(this.field.toString)
    val col = if (surround) left + fieldName + right else fieldName
    val comp = QueryEncoder.ensureCompare(comparison)
    val fieldVal = this.fieldValue
    val result = QueryEncoder.convertVal(fieldVal)

    col + " " + comp + " " + result
  }
}
