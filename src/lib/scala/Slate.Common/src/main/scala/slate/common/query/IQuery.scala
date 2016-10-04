package slate.common.query

/**
  * Created by kreddy on 12/24/2015.
  */
trait IQuery {
  def getUpdates():List[FieldValue]


  def getUpdatesText():String


  def getFilter(): String


  /// <summary>
  /// Adds a WHERE clause to the query.
  /// </summary>
  /// <param name="exp">Expression to retrieve property name.</param>
  /// <returns>This instance.</returns>
  def set(field:String, fieldValue:Any) : IQuery


  /// <summary>
  /// Adds a WHERE clause to the query.
  /// </summary>
  /// <param name="exp">Expression to retrieve property name.</param>
  /// <returns>This instance.</returns>
  def where(field:String, compare:String, fieldValue:Any) : IQuery


  def where(field:String, compare:Op, fieldValue:Any) : IQuery


  /// <summary>
  /// Adds an AND condition to the query.
  /// </summary>
  /// <param name="exp">Expression to retrieve property name.</param>
  /// <returns>This instance.</returns>
  def and(field:String, compare:String, fieldValue:Any) : IQuery


  def and(field:String, compare:Op, fieldValue:Any) : IQuery


  /// <summary>
  /// Adds an OR condition to the query.
  /// </summary>
  /// <param name="exp">Expression to retrieve property name.</param>
  /// <returns>This instance.</returns>
  def or(field:String, compare:String, fieldValue:Any) : IQuery


  def or(field:String, compare:Op, fieldValue:Any) : IQuery

  def orderBy ( field:String ): IQuery

  def asc(): IQuery


  def desc(): IQuery


  def limit(max:Int) : IQuery
}
