package test

import org.junit.Test
import slatekit.common.DateTime
import slatekit.core.push.*


class MessageTests {

    private fun sampleWithoutArgs():Message {
        return  Message(
            meta = Meta.of(Share, "event", "create"),
            sender = Sender("123", "superman", PlatformIOS, "123-456-7890", "abc123"),
            recipient = Recipient("xyz", "batman", PlatformAnd, "000-000-0000", "xyz123"),
            content = Content("justice league meeting", "darkseid", DateTime.of(2018, 4, 1))
        )
    }


    private fun sampleWithArgs():Message {
        return  Message(
            meta = Meta.of(Share, "event", "create", mapOf(
                "vStr"  to "abc",
                "vInt"  to "123",
                "vBool" to "true"
            )),
            sender = Sender("123", "superman", PlatformIOS, "123-456-7890", "abc123", mapOf(
                "vStr"  to "abc",
                "vInt"  to "123",
                "vBool" to "true"
            )),
            recipient = Recipient("xyz", "batman", PlatformAnd, "000-000-0000", "xyz123", mapOf(
                    "vStr"  to "abc",
                "vInt"  to "123",
                "vBool" to "true"
            )),
            content = Content("justice league meeting", "darkseid", DateTime.of(2018, 4, 1), mapOf(
                "vStr"  to "abc",
                "vInt"  to "123",
                "vBool" to "true"
            ))
        )
    }


    @Test fun can_build_message_empty(){
        assert(Message.empty.meta.category.name == "")
        assert(Message.empty.meta.type == "")
        assert(Message.empty.meta.id == "")
        assert(Message.empty.meta.action == "")
        assert(Message.empty.meta.origin == "")
        assert(Message.empty.meta.args == null)

        assert(Message.empty.sender.device == "")
        assert(Message.empty.sender.id == "")
        assert(Message.empty.sender.name == "")
        assert(Message.empty.sender.phone == "")
        assert(Message.empty.sender.phone == "")
        assert(Message.empty.sender.args == null)

        assert(Message.empty.recipient.device == "")
        assert(Message.empty.recipient.id == "")
        assert(Message.empty.recipient.name == "")
        assert(Message.empty.recipient.phone == "")
        assert(Message.empty.recipient.phone == "")
        assert(Message.empty.recipient.args == null)
    }


    @Test fun can_build_message(){

        val msg = sampleWithoutArgs()

        assert(msg.meta.category == Share)
        assert(msg.meta.type == "event")
        assert(msg.meta.action == "create")
        assert(msg.meta.args == null)

        assert(msg.sender.device == "abc123")
        assert(msg.sender.id == "123")
        assert(msg.sender.name == "superman")
        assert(msg.sender.phone == "123-456-7890")
        assert(msg.sender.platform == PlatformIOS)
        assert(msg.sender.args == null)

        assert(msg.recipient.device == "xyz123")
        assert(msg.recipient.id == "xyz")
        assert(msg.recipient.name == "batman")
        assert(msg.recipient.phone == "000-000-0000")
        assert(msg.recipient.platform == PlatformAnd)
        assert(msg.recipient.args == null)

        assert(msg.content.title == "justice league meeting")
        assert(msg.content.args == null)
    }


    @Test fun can_serialize_message_to_json(){

        val msg = sampleWithoutArgs()
        val json = msg.toJson()
        val expected = """{"data":"","sender":{"args":null,"phone":"123-456-7890","name":"superman","id":"123","device":"abc123","platform":"ios"},"meta":{"args":null,"origin":"","action":"create","tsMsg":null,"tsSent":null,"id":"","tag":"","category":"share","type":"event"},"recipient":{"args":null,"phone":"000-000-0000","name":"batman","id":"xyz","device":"xyz123","platform":"and"},"content":{"date":"2018-04-01-00-00-00","args":null,"title":"justice league meeting","desc":"darkseid"}}"""
        assert(json == expected)
    }


    @Test fun can_serialize_message_from_json(){
        val expected = """{"data":"","sender":{"args":null,"phone":"123-456-7890","name":"superman","id":"123","device":"abc123","platform":"ios"},"meta":{"args":null,"origin":"","action":"create","tsMsg":null,"tsSent":null,"id":"","tag":"","category":"share","type":"event"},"recipient":{"args":null,"phone":"000-000-0000","name":"batman","id":"xyz","device":"xyz123","platform":"and"},"content":{"date":"2018-04-01-00-00-00","args":null,"title":"justice league meeting","desc":"darkseid"}}"""
        val msg = MessageBuilder.fromJson(expected)

        assert(msg.meta.category == Share)
        assert(msg.meta.type == "event")
        assert(msg.meta.action == "create")
        assert(msg.meta.args == null)

        assert(msg.sender.device == "abc123")
        assert(msg.sender.id == "123")
        assert(msg.sender.name == "superman")
        assert(msg.sender.phone == "123-456-7890")
        assert(msg.sender.platform == PlatformIOS)
        assert(msg.sender.args == null)

        assert(msg.recipient.device == "xyz123")
        assert(msg.recipient.id == "xyz")
        assert(msg.recipient.name == "batman")
        assert(msg.recipient.phone == "000-000-0000")
        assert(msg.recipient.platform == PlatformAnd)
        assert(msg.recipient.args == null)

        assert(msg.content.title == "justice league meeting")
        assert(msg.content.args == null)
    }




    @Test fun can_serialize_message_to_json_with_args(){

        val msg = sampleWithArgs()
        val json = msg.toJson()
        val expected = """{"data":"","sender":{"args":{"vStr":"abc","vInt":"123","vBool":"true"},"phone":"123-456-7890","name":"superman","id":"123","device":"abc123","platform":"ios"},"meta":{"args":{"vStr":"abc","vInt":"123","vBool":"true"},"origin":"","action":"create","tsMsg":null,"tsSent":null,"id":"","tag":"","category":"share","type":"event"},"recipient":{"args":{"vStr":"abc","vInt":"123","vBool":"true"},"phone":"000-000-0000","name":"batman","id":"xyz","device":"xyz123","platform":"and"},"content":{"date":"2018-04-01-00-00-00","args":{"vStr":"abc","vInt":"123","vBool":"true"},"title":"justice league meeting","desc":"darkseid"}}"""
        assert(json == expected)
    }


    @Test fun can_serialize_message_from_json_with_args(){
        val expected = """{"data":"","sender":{"args":{"vStr":"abc","vInt":"123","vBool":"true"},"phone":"123-456-7890","name":"superman","id":"123","device":"abc123","platform":"ios"},"meta":{"args":{"vStr":"abc","vInt":"123","vBool":"true"},"origin":"","action":"create","tsMsg":null,"tsSent":null,"id":"","tag":"","category":"share","type":"event"},"recipient":{"args":{"vStr":"abc","vInt":"123","vBool":"true"},"phone":"000-000-0000","name":"batman","id":"xyz","device":"xyz123","platform":"and"},"content":{"date":"2018-04-01-00-00-00","args":{"vStr":"abc","vInt":"123","vBool":"true"},"title":"justice league meeting","desc":"darkseid"}}"""
        val msg = MessageBuilder.fromJson(expected)

        // "vStr"  to "abc",
        // "vInt"  to "123",
        // "vBool" to "true"
        fun ensureArgs(args:Map<String,String>?){
            assert(args?.get("vStr") == "abc")
            assert(args?.get("vInt") == "123")
            assert(args?.get("vBool") == "true")
        }

        assert(msg.meta.category == Share)
        assert(msg.meta.type == "event")
        assert(msg.meta.action == "create")
        ensureArgs(msg.meta.args)

        assert(msg.sender.device == "abc123")
        assert(msg.sender.id == "123")
        assert(msg.sender.name == "superman")
        assert(msg.sender.phone == "123-456-7890")
        assert(msg.sender.platform == PlatformIOS)
        ensureArgs(msg.sender.args)

        assert(msg.recipient.device == "xyz123")
        assert(msg.recipient.id == "xyz")
        assert(msg.recipient.name == "batman")
        assert(msg.recipient.phone == "000-000-0000")
        assert(msg.recipient.platform == PlatformAnd)
        ensureArgs(msg.recipient.args)

        assert(msg.content.title == "justice league meeting")
        ensureArgs(msg.meta.args)
    }
}
