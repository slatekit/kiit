package test.entities

import kiit.entities.Schema
import org.junit.Assert
import org.junit.Test
import test.setup.UserTypes

class Entity_Schema_Tests {


    @Test
    fun can_load_model_field_name() {
        val model = Schema.load(UserTypes::class, UserTypes::id.name)

        // Case 1: Type is required
        val email = model.fields.first { it.name == "email" }
        Assert.assertEquals("email", email.storedName)

        // Case 2: Class property name is different from the stored name
        val createdAt = model.fields.first { it.name == "createdat" }
        Assert.assertEquals("createdAt", createdAt.prop!!.name)
    }


    @Test
    fun can_load_model_field_required_from_type() {
        val model = Schema.load(UserTypes::class, UserTypes::id.name)

        // Case 1: Type is required
        val email = model.fields.first { it.name == "email" }
        Assert.assertEquals(true, email.isRequired)

        // Case 2: Type is nullable ( Optional )
        val website = model.fields.first { it.name == "website" }
        Assert.assertEquals(false, website.isRequired)

        // Case 3: Type is nullable ( Optional ), but annotation marked as required
        val link = model.fields.first { it.name == "link" }
        Assert.assertEquals(true, link.isRequired)
        Assert.assertEquals(1, link.tags.size)
        Assert.assertEquals("secondary_url", link.tags[0])
    }



    @Test
    fun can_load_model_field_tags() {
        val model = Schema.load(UserTypes::class, UserTypes::id.name)
        // Check that the column.tags is transferred to ModelField.tags
        val link = model.fields.first { it.name == "link" }
        Assert.assertEquals(1, link.tags.size)
        Assert.assertEquals("secondary_url", link.tags[0])
    }



    @Test
    fun can_load_model_field_string_lengths() {
        val model = Schema.load(UserTypes::class, UserTypes::id.name)

        // Case 1: Explicit length supplied
        val email = model.fields.first { it.name == "email" }
        Assert.assertEquals(100, email.maxLength)

        // Case 2: String length marked as -1 explicitly
        val website = model.fields.first { it.name == "website" }
        Assert.assertEquals(-1, website.maxLength)

        // Case 3: Type is nullable ( Optional ), but annotation marked as required
        val link = model.fields.first { it.name == "link" }
        Assert.assertEquals(-1, link.maxLength)
    }
}