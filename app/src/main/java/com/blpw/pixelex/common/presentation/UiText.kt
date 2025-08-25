package com.blpw.pixelex.common.presentation


//sealed interface UiText {
//    data class DynamicString(val value: String): UiText
//    class StringResourceId(
//        //val id: StringResource,
//        val args: Array<Any> = arrayOf()
//    ): UiText
//
//    @Composable
//    fun asString(): String {
//        return when(this) {
//            is DynamicString -> value
//            is StringResourceId -> stringResource(resource = id, formatArgs = args)
//        }
//    }
//}