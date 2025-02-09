package test.entities

interface EntityTestCases {
    fun setup()

    fun can_create_an_item()

    fun can_use_model_with_nullable_fields()

    fun can_update_an_item()

    fun can_count_any()

    fun can_count_size()

    fun can_get_first()

    fun can_get_last()

    fun can_get_recent()

    fun can_get_oldest()

    fun can_get_all()

    fun can_find_by_field()

    fun can_get_aggregates()

    fun can_find_by_query()

    fun can_patch_by_query()

    fun can_get_relation()

    fun can_get_relations()

    fun can_get_relation_with_object()
}