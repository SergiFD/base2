package com.example.base_de_datos_clase

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


    class MainActivity : AppCompatActivity() {


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val dbHelper = SQLite(this)

            // Referencias a los elementos de la interfaz
            val etIdCard = findViewById<EditText>(R.id.et_id_card)
            val etName = findViewById<EditText>(R.id.et_name)
            val etSurname = findViewById<EditText>(R.id.et_surname)
            val radioGroup = findViewById<RadioGroup>(R.id.radiogroup_course)
            val spinner = findViewById<Spinner>(R.id.spinner)
            val btnAdd = findViewById<Button>(R.id.btn_add_student)
            val btnDelete = findViewById<Button>(R.id.btn_add_student6)
            val btnUpdate = findViewById<Button>(R.id.btn_update_student)
            val btnFetch = findViewById<Button>(R.id.btn_findID_student)
            val btnFetchCycle = findViewById<Button>(R.id.btn_findCycle_student)
            val btnFetchCourse = findViewById<Button>(R.id.btn_findCourse_student)

            // Configuración del Spinner
            val cycles = listOf("ASIX", "DAM", "DAW")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cycles)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            // Acción del botón para agregar estudiante
            btnAdd.setOnClickListener {
                val idCard = etIdCard.text.toString()
                val name = etName.text.toString()
                val surname = etSurname.text.toString()
                val cycle = spinner.selectedItem.toString()
                val course = when (radioGroup.checkedRadioButtonId) {
                    R.id.First -> 1
                    R.id.Second -> 2
                    else -> null
                }

                if (idCard.isEmpty() || name.isEmpty() || surname.isEmpty() || course == null) {
                    Toast.makeText(
                        this,
                        "Por favor, complete todos los campos.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                dbHelper.addStudent(this, idCard, name, surname, cycle, course)
            }

            // Acción del botón para eliminar estudiante
            btnDelete.setOnClickListener {
                val idCard = etIdCard.text.toString()
                if (idCard.isEmpty()) {
                    Toast.makeText(this, "Ingrese un DNI para eliminar.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                dbHelper.deleteStudentById(this, idCard)
            }

            // Acción del botón para actualizar estudiante
            btnUpdate.setOnClickListener {
                val idCard = etIdCard.text.toString()
                val name = etName.text.toString()
                val surname = etSurname.text.toString()
                val cycle = spinner.selectedItem.toString()
                val course = when (radioGroup.checkedRadioButtonId) {
                    R.id.First -> 1
                    R.id.Second -> 2
                    else -> null
                }

                if (idCard.isEmpty()) {
                    Toast.makeText(this, "Ingrese un DNI para actualizar.", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                // Use null values for fields that are not being updated
                dbHelper.updateStudent(
                    this,
                    idCard,
                    name.takeIf { it.isNotEmpty() },
                    surname.takeIf { it.isNotEmpty() },
                    cycle.takeIf { it.isNotEmpty() },
                    course
                )
            }

            // Acción del botón para obtener estudiantes por ciclo y curso
            btnFetchCycle.setOnClickListener {
                val cycle = spinner.selectedItem.toString()
                val course = when (radioGroup.checkedRadioButtonId) {
                    R.id.First -> 1
                    R.id.Second -> 2
                    else -> null
                }

                if (course == null) {
                    Toast.makeText(this, "Por favor, seleccione un curso.", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                // Call method to fetch and save the students' data to a file
                dbHelper.getStudentsByCourse(this, cycle, course)
            }

            btnFetch.setOnClickListener {
                val idCard = etIdCard.text.toString()
                if (idCard.isEmpty()) {
                    Toast.makeText(this, "Ingrese un DNI para eliminar.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                dbHelper.findStudentsById(this, idCard)

            }
        }
    }

