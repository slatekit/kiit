package test.common

import org.junit.Assert
import org.junit.Test
import kiit.meta.Reflector
import test.setup.StatusEnum
import test.setup.StatusEnum2
import test.setup.StatusEnum3


class EnumLikeJavaBasedTests {

    @Test fun can_get_all_values() {
        val statuses = StatusEnum.all()
        Assert.assertEquals(statuses[0], StatusEnum.Pending)
        Assert.assertEquals(statuses[1], StatusEnum.Active)
        Assert.assertEquals(statuses[2], StatusEnum.Blocked)
    }


    @Test fun can_parse_by_name() {
        val status = StatusEnum.parse(StatusEnum.Active.name)
        Assert.assertEquals(status, StatusEnum.Active)
    }


    @Test fun can_parse_by_name_by_reflection() {
        val status = Reflector.getEnumValue(StatusEnum::class, StatusEnum.Blocked.name)
        Assert.assertEquals(status, StatusEnum.Blocked)
    }


    @Test fun can_parse_by_value() {
        val status = StatusEnum.convert(StatusEnum.Active.value)
        Assert.assertEquals(status, StatusEnum.Active)
    }


    @Test fun can_parse_by_value_by_reflection() {
        val status = Reflector.getEnumValue(StatusEnum::class, StatusEnum.Blocked.value)
        Assert.assertEquals(status, StatusEnum.Blocked)
    }
}


class EnumLikeKotlinBasedTests {

    @Test fun can_get_all_values() {
        val statuses = StatusEnum2.all()
        Assert.assertEquals(statuses[0], StatusEnum2.Pending)
        Assert.assertEquals(statuses[1], StatusEnum2.Active)
        Assert.assertEquals(statuses[2], StatusEnum2.Blocked)
    }


    @Test fun can_parse_by_name() {
        val status = StatusEnum2.parse(StatusEnum2.Active.name)
        Assert.assertEquals(status, StatusEnum2.Active)
    }


    @Test fun can_parse_by_name_by_reflection() {
        val status = Reflector.getEnumValue(StatusEnum2::class, StatusEnum2.Blocked.name)
        Assert.assertEquals(status, StatusEnum2.Blocked)
    }


    @Test fun can_parse_by_value() {
        val status = StatusEnum2.convert(StatusEnum2.Active.value)
        Assert.assertEquals(status, StatusEnum2.Active)
    }


    @Test fun can_parse_by_value_by_reflection() {
        val status = Reflector.getEnumValue(StatusEnum2::class, StatusEnum2.Blocked.value)
        Assert.assertEquals(status, StatusEnum2.Blocked)
    }
}


class EnumLikeKotlinSealedClassBasedTests {

    @Test fun can_get_all_values() {
        val statuses = StatusEnum3.all()
        Assert.assertEquals(statuses[0], StatusEnum3.Pending)
        Assert.assertEquals(statuses[1], StatusEnum3.Active)
        Assert.assertEquals(statuses[2], StatusEnum3.Blocked)
    }


    @Test fun can_parse_by_name() {
        val status = StatusEnum3.parse(StatusEnum3.Active.name)
        Assert.assertEquals(status, StatusEnum3.Active)
    }


    @Test fun can_parse_by_name_by_reflection() {
        val status = Reflector.getEnumValue(StatusEnum3::class, StatusEnum3.Blocked.name)
        Assert.assertEquals(status, StatusEnum3.Blocked)
    }


    @Test fun can_parse_by_value() {
        val status = StatusEnum3.convert(StatusEnum3.Active.value)
        Assert.assertEquals(status, StatusEnum3.Active)
    }


    @Test fun can_parse_by_value_by_reflection() {
        val status = Reflector.getEnumValue(StatusEnum3::class, StatusEnum3.Blocked.value)
        Assert.assertEquals(status, StatusEnum3.Blocked)
    }
}