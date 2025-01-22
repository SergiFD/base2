package com.example.base_de_datos_clase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.widget.Toast
import com.example.base_de_datos_clase.StudentContract.FeedEntry
import com.example.base_de_datos_clase.StudentContract.FeedEntry.COLUMN_COURSE
import com.example.base_de_datos_clase.StudentContract.FeedEntry.COLUMN_CYCLE
import com.example.base_de_datos_clase.StudentContract.FeedEntry.COLUMN_ID_CARD
import com.example.base_de_datos_clase.StudentContract.FeedEntry.COLUMN_NAME
import com.example.base_de_datos_clase.StudentContract.FeedEntry.COLUMN_SURNAME
import com.example.base_de_datos_clase.StudentContract.FeedEntry.TABLE_NAME

class StudentHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        //Aqui creo la tabla a partir del script sql SQL_CREATE_ENTRIES
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        //Script sql de creación
        private const val SQL_CREATE_ENTRIES = """
    CREATE TABLE ${TABLE_NAME} (
        ${COLUMN_ID_CARD} TEXT PRIMARY KEY,
        ${COLUMN_NAME} TEXT NOT NULL,
        ${COLUMN_SURNAME} TEXT NOT NULL,
        ${COLUMN_CYCLE} TEXT CHECK(${COLUMN_CYCLE} IN ('ASIX', 'DAM', 'DAW')),
        ${COLUMN_COURSE} INTEGER NOT NULL
    )
"""

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"

        // Si cambias el esquema de la base de datos, debes incrementar la versión.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "students.db"
    }


//funciones
    fun addStudent(
        context: Context, idCard: String, name: String, surname: String, cycle: String, course: Int
    ): Boolean {
        if (cycle !in listOf("ASIX", "DAM", "DAW")) {
            Toast.makeText(context, "Cicle invàlid. Trieu ASIX, DAM o DAW.", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_CARD, idCard)
            put(COLUMN_NAME, name)
            put(COLUMN_SURNAME, surname)
            put(COLUMN_CYCLE, cycle)
            put(COLUMN_COURSE, course)
        }
        val result = db.insert(TABLE_NAME, null, values)
        if (result == -1L) {
            Toast.makeText(context, "El estudiante con DNI $idCard ya existe.", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        Toast.makeText(context, "Estudiante registrado correctamente.", Toast.LENGTH_SHORT).show()
        return true
    }

    fun getStudentsByCourse(context: Context, cycle: String, course: Int) {
        if (cycle !in listOf("ASIX", "DAM", "DAW")) {
            Toast.makeText(context, "Cicle invàlid. Trieu ASIX, DAM o DAW.", Toast.LENGTH_SHORT)
                .show()
        }

        var selection = "$COLUMN_CYCLE = ? AND $COLUMN_COURSE = ?"

        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            arrayOf(cycle, course.toString()),
            null,
            null,
            null
        )
        writeStudentsByCurs(cursor, context, cycle, course)
    }

    fun getStudentsByCicle(context: Context, cycle: String) {
        if (cycle !in listOf("ASIX", "DAM", "DAW")) {
            Toast.makeText(context, "Cicle invàlid. Trieu ASIX, DAM o DAW.", Toast.LENGTH_SHORT)
                .show()
        }

        var selection = "$COLUMN_CYCLE = ?"

        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, selection, arrayOf(cycle), null, null, null
        )
        writeStudentsByCicle(cursor, context, cycle)
    }

    fun writeStudentsByCurs(cursor: Cursor, context: Context, cycle: String, course: Int) {
        if (cursor.moveToFirst()) {
            val file =
                context.openFileOutput("students_${cycle}_course_$course.txt", Context.MODE_PRIVATE)
            file.use {
                do {
                    val idCard = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ID_CARD
                        )
                    )
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    val surname =
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME))
                    it.write("$idCard - $name $surname\n".toByteArray())
                } while (cursor.moveToNext())
            }

            cursor.close()
            Toast.makeText(
                context,
                "Fitxer creat per al cicle $cycle, curs $course.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            cursor.close()
            Toast.makeText(
                context,
                "No hi ha alumnes al cicle $cycle, curs $course.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    

    fun writeStudentsByCicle(cursor: Cursor, context: Context, cycle: String) {
        if (cursor.moveToFirst()) {
            val file = context.openFileOutput("students_${cycle}.txt", Context.MODE_PRIVATE)
            file.use {
                do {
                    val idCard = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ID_CARD
                        )
                    )
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    val surname =
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME))
                    it.write("$idCard - $name $surname\n".toByteArray())
                } while (cursor.moveToNext())
            }

            cursor.close()
            Toast.makeText(context, "Fitxer creat per al cicle $cycle", Toast.LENGTH_SHORT).show()
        } else {
            cursor.close()
            Toast.makeText(context, "No hi ha alumnes al cicle $cycle,", Toast.LENGTH_SHORT).show()
        }

    }

    fun deleteStudentById(context: Context, idCard: String): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete(

            TABLE_NAME, "$COLUMN_ID_CARD = ?", arrayOf(idCard)
        )
        return if (rowsDeleted > 0) {
            Toast.makeText(
                context,
                "L'alumne amb DNI $idCard ha estat eliminat.",
                Toast.LENGTH_SHORT
            ).show()
            true
        } else {
            Toast.makeText(context, "No existeix cap alumne amb DNI $idCard.", Toast.LENGTH_SHORT)
                .show()
            false
        }
    }

    fun findStudentsById(context: Context, idCard: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, // Queremos todos los campos
            "$COLUMN_ID_CARD = ?", // Filtro para buscar por ID
            arrayOf(idCard), // El valor del ID que estamos buscando
            null, null, null
        )

        return if (cursor.moveToFirst()) { // Si hay resultados
            //val studentId = cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_ID_CARD))
            val studentName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val studentSurname =
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME))
            val studentCycle =
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CYCLE))
            val studentCourse = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE))

            // Aquí puedes mostrar los detalles del estudiante en un Toast o hacer algo con los datos
            Toast.makeText(
                context,
                "Estudiante encontrado: $studentName $studentSurname, Ciclo: $studentCycle, Curso: $studentCourse",
                Toast.LENGTH_SHORT
            ).show()

            cursor.close()
            true // Indicamos que el estudiante fue encontrado
        } else {
            cursor.close()
            Toast.makeText(
                context,
                "No existe un estudiante con el DNI $idCard.",
                Toast.LENGTH_SHORT
            ).show()
            false // Indicamos que no se encontró el estudiante
        }
    }

    fun updateStudent(
        context: Context,
        idCard: String,
        name: String?,
        surname: String?,
        cycle: String?,
        course: Int?
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            if (!name.isNullOrEmpty()) put(COLUMN_NAME, name)
            if (!surname.isNullOrEmpty()) put(COLUMN_SURNAME, surname)
            if (!cycle.isNullOrEmpty() && cycle in listOf(
                    "ASIX",
                    "DAM",
                    "DAW"
                )
            ) put(COLUMN_CYCLE, cycle)
            if (course != null) put(COLUMN_COURSE, course)
        }

        val rowsUpdated = db.update(
            TABLE_NAME, values, "$COLUMN_ID_CARD = ?", arrayOf(idCard)
        )
        return if (rowsUpdated > 0) {
            Toast.makeText(
                context,
                "Dades de l'alumne actualitzades correctament.",
                Toast.LENGTH_SHORT
            ).show()
            true
        } else {
            Toast.makeText(context, "No existeix cap alumne amb DNI $idCard.", Toast.LENGTH_SHORT)
                .show()
            false
        }
    }
}
