package com.example.base_de_datos_clase

import android.provider.BaseColumns

object StudentContract {
    object  FeedEntry : BaseColumns {
        const val TABLE_NAME = "students"
        const val COLUMN_ID_CARD = "id_card"
        const val COLUMN_NAME = "name"
        const val COLUMN_SURNAME = "surname"
        const val COLUMN_CYCLE = "cycle"
        const val COLUMN_COURSE = "course"
    }
}