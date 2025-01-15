package com.example.base_de_datos_clase

import android.provider.BaseColumns

object StudentContract {
    object  FeedEntry : BaseColumns {
        private const val DATABASE_NAME = "students.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "students"
        const val COLUMN_ID_CARD = "id_card"
        const val COLUMN_NAME = "name"
        const val COLUMN_SURNAME = "surname"
        const val COLUMN_CYCLE = "cycle"
        const val COLUMN_COURSE = "course"
    }
}