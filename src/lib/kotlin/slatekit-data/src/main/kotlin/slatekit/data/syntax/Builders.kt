package slatekit.data.syntax

class Builders {
    class Where(converter: ((String) -> String)? = null,
                encoder:((String) -> String)? = null) : slatekit.query.Where()


    class Select(converter: ((String) -> String)? = null,
                 encoder:((String) -> String)? = null) : slatekit.query.Select()


    class Update(converter: ((String) -> String)? = null,
                 encoder:((String) -> String)? = null) : slatekit.query.Update()
}
