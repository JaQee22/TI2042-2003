package com.example.myfirebaseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.myfirebaseexample.api.FirebaseApiAdapter
import com.example.myfirebaseexample.api.response.RopaResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    // Referenciar campos de las interfaz
    private lateinit var idSpinner: Spinner
    private lateinit var nameField: EditText
    private lateinit var sizeField: EditText
    private lateinit var costField: EditText
    private lateinit var brandField: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonLoad: Button

    // Referenciar la API
    private var firebaseApi = FirebaseApiAdapter()

    // Mantener los nombres e IDs de las armas
    private var ropaList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        idSpinner = findViewById(R.id.idSpinner)
        nameField = findViewById(R.id.nameField)
        sizeField = findViewById(R.id.sizesField)
        costField = findViewById(R.id.costField)
        brandField = findViewById(R.id.brandField)

        buttonLoad = findViewById(R.id.buttonLoad)
        buttonLoad.setOnClickListener {
            Toast.makeText(this, "Cargando información", Toast.LENGTH_SHORT).show()
            runBlocking {
                getRopaFromApi()
            }
        }

        buttonSave = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            Toast.makeText(this, "Guardando información", Toast.LENGTH_SHORT).show()
            runBlocking {
                sendRopaToApi()
            }
        }

        runBlocking {
            populateIdSpinner()
        }
    }

    private suspend fun populateIdSpinner() {
        val response = GlobalScope.async(Dispatchers.IO) {
            firebaseApi.getRopas()
        }
        val ropas = response.await()
        ropas?.forEach { entry ->
            ropaList.add("${entry.key}: ${entry.value.name}")
        }
        val ropaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ropaList)
        with(idSpinner) {
            adapter = ropaAdapter
            setSelection(0, false)
            gravity = Gravity.CENTER
        }
    }

    private suspend fun getRopaFromApi() {
        val selectedItem = idSpinner.selectedItem.toString()
        val ropaId = selectedItem.subSequence(0, selectedItem.indexOf(":")).toString()
        println("Loading ${ropaId}... ")
        val ropaResponse = GlobalScope.async(Dispatchers.IO) {
            firebaseApi.getRopa(ropaId)
        }
        val ropa = ropaResponse.await()
        nameField.setText(ropa?.name)
        brandField.setText(ropa?.brand)
        sizeField.setText("${ropa?.size}")
        costField.setText("${ropa?.cost}")
    }

    private suspend fun sendRopaToApi() {
        val ropaName = nameField.text.toString()
        val brandName = brandField.text.toString()
        val size = sizeField.text.toString()
        val cost = costField.text.toString().toLong()
        val ropa = RopaResponse("", ropaName, size, cost, brandName)
        val RopaResponse = GlobalScope.async(Dispatchers.IO) {
            firebaseApi.setRopa(ropa)
        }
        val response = RopaResponse.await()
        nameField.setText(ropa?.name)
        brandField.setText(ropa?.brand)
        sizeField.setText("${ropa?.size}")
        costField.setText("${ropa?.cost}")

        ropaList= arrayListOf<String>()
        populateIdSpinner()
    }
}
