package com.example.base_de_datos_clase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class SQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME="students.db"
        private const val DATABASE_VERSION=1
        const val TABLE_NAME="students"
        const val COLUMN_ID_CARD="id_card"
        const val COLUMN_NAME="name"
        const val COLUMN_SURNAME="surname"
        const val COLUMN_CYCLE="cycle"
        const val COLUMN_COURSE="course"
    }

    override fun onCreate(db: SQLiteDatabase){
        val createTableQuery="""
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID_CARD TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_SURNAME TEXT NOT NULL,
                $COLUMN_CYCLE TEXT CHECK($COLUMN_CYCLE IN ('ASIX', 'DAM', 'DAW')),
                $COLUMN_COURSE INTEGER NOT NULL
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db:SQLiteDatabase,oldVersion:Int,newVersion:Int){
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    fun addStudent(
        context: Context,
        idCard: String,
        name: String,
        surname: String,
        cycle: String,
        course: Int
    ): Boolean {
        if (cycle !in listOf("ASIX", "DAM", "DAW")) {
            Toast.makeText(context, "Cicle invàlid. Trieu ASIX, DAM o DAW.", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "El estudiante con DNI $idCard ya existe.", Toast.LENGTH_SHORT).show()
            return false
        }
        Toast.makeText(context, "Estudiante registrado correctamente.", Toast.LENGTH_SHORT).show()
        return true
    }

    fun getStudentsByCourse(context: Context, cycle: String, course: Int): Boolean {
        if (cycle !in listOf("ASIX", "DAM", "DAW")) {
            Toast.makeText(context, "Cicle invàlid. Trieu ASIX, DAM o DAW.", Toast.LENGTH_SHORT).show()
            return false
        }

        val db = readableDatabase
        val cursor = db.query(
            SQLite.TABLE_NAME,
            null,
            "${SQLite.COLUMN_CYCLE} = ? AND ${SQLite.COLUMN_COURSE} = ?",
            arrayOf(cycle, course.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val file = context.openFileOutput("students_${cycle}_course_$course.txt", Context.MODE_PRIVATE)
            file.use {
                do {
                    val idCard = cursor.getString(cursor.getColumnIndexOrThrow(
                        SQLite.COLUMN_ID_CARD))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_NAME))
                    val surname = cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_SURNAME))
                    it.write("$idCard - $name $surname\n".toByteArray())
                } while (cursor.moveToNext())
            }

            cursor.close()
            Toast.makeText(context, "Fitxer creat per al cicle $cycle, curs $course.", Toast.LENGTH_SHORT).show()
            return true
        } else {
            cursor.close()
            Toast.makeText(context, "No hi ha alumnes al cicle $cycle, curs $course.", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun deleteStudentById(context: Context, idCard: String): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete(

            SQLite.TABLE_NAME,
            "${SQLite.COLUMN_ID_CARD} = ?",
            arrayOf(idCard)
        )
        return if (rowsDeleted > 0) {
            Toast.makeText(context, "L'alumne amb DNI $idCard ha estat eliminat.", Toast.LENGTH_SHORT).show()
            true
        } else {
            Toast.makeText(context, "No existeix cap alumne amb DNI $idCard.", Toast.LENGTH_SHORT).show()
            false
        }
    }
    fun findStudentsById(context: Context, idCard: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            SQLite.TABLE_NAME,
            null, // Queremos todos los campos
            "${SQLite.COLUMN_ID_CARD} = ?", // Filtro para buscar por ID
            arrayOf(idCard), // El valor del ID que estamos buscando
            null, null, null
        )

        return if (cursor.moveToFirst()) { // Si hay resultados
            val studentId = cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_ID_CARD))
            val studentName = cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_NAME))
            val studentSurname = cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_SURNAME))
            val studentCycle = cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_CYCLE))
            val studentCourse = cursor.getInt(cursor.getColumnIndexOrThrow(SQLite.COLUMN_COURSE))

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
            Toast.makeText(context, "No existe un estudiante con el DNI $idCard.", Toast.LENGTH_SHORT).show()
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
            if (!name.isNullOrEmpty()) put(SQLite.COLUMN_NAME, name)
            if (!surname.isNullOrEmpty()) put(SQLite.COLUMN_SURNAME, surname)
            if (!cycle.isNullOrEmpty() && cycle in listOf("ASIX", "DAM", "DAW")) put(SQLite.COLUMN_CYCLE, cycle)
            if (course != null) put(SQLite.COLUMN_COURSE, course)
        }

        val rowsUpdated = db.update(
            SQLite.TABLE_NAME,
            values,
            "${SQLite.COLUMN_ID_CARD} = ?",
            arrayOf(idCard)
        )
        return if (rowsUpdated > 0) {
            Toast.makeText(context, "Dades de l'alumne actualitzades correctament.", Toast.LENGTH_SHORT).show()
            true
        } else {
            Toast.makeText(context, "No existeix cap alumne amb DNI $idCard.", Toast.LENGTH_SHORT).show()
            false
        }
    }


}
