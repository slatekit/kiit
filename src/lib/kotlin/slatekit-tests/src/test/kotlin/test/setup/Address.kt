package test.setup

import kiit.entities.Column

data class Address(

        @property:Column(required = true, length = 40)
        val addr   : String,


        @property:Column(required = true, length = 30)
        val city   : String,


        @property:Column(required = true, length = 20)
        val state  : String,


        @property:Column(required = true)
        val country: Int,


        @property:Column(required = true, length = 5)
        val zip    : String,


        @property:Column(required = true)
        val isPOBox: Boolean
)



