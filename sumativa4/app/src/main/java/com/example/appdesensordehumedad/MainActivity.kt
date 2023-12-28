package com.example.appdesensordehumedad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var greenStatusRadio: RadioButton
    private lateinit var yellowStatusRadio: RadioButton
    private lateinit var redStatusRadio: RadioButton
    private lateinit var humidifyButton: Button
    private lateinit var dehumidifyButton: Button
    private lateinit var offButton: Button

    private lateinit var mqttClient: MQTTHELPER
    // 2 variables
    private var humidityValue: Int = 50  // Porcentaje de humedad actual
    private var deviceStatus: Int = 0    // Estado del humidificador (0 es apagado)

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        greenStatusRadio = findViewById(R.id.greenStatusRadio)
        yellowStatusRadio = findViewById(R.id.yellowStatusRadio)
        redStatusRadio = findViewById(R.id.redStatusRadio)
        val statusRadioButtonList = ArrayList<RadioButton>()
        statusRadioButtonList.add(greenStatusRadio)
        statusRadioButtonList.add(yellowStatusRadio)
        statusRadioButtonList.add(redStatusRadio)

        mqttClient = MQTTHELPER()
        mqttClient.subscribeToTopic(MQTTHELPER.SENSOR_TOPIC, statusRadioButtonList)

        humidifyButton = findViewById(R.id.humidifyButton)
        dehumidifyButton = findViewById(R.id.dehumidifyButton)
        offButton = findViewById(R.id.offButton)

        humidifyButton.setOnClickListener {
            mqttClient.publishMessage(MQTTHELPER.DEVICE_TOPIC, "HUMIDIFIER")
            deviceStatus = 1   // Cuando está humidificando (aumenta la humedad)
            // Cuando el dispotivo humidifica
            }

        dehumidifyButton.setOnClickListener {
            mqttClient.publishMessage(MQTTHELPER.DEVICE_TOPIC, "DEHUMIDIFIER")
            deviceStatus = -1  // Cuando está deshumificiando (desciende la humedad)
            // Cuando el dispotivo deshumidifica
        }

        offButton.setOnClickListener {
            mqttClient.publishMessage(MQTTHELPER.DEVICE_TOPIC, "OFF")
            // Cuando el dispotivo se apaga
            deviceStatus = 0   // Cuando está apagado
        }

        // GlobalScope...
        GlobalScope.launch(context = Dispatchers.Main) {
            deviceOperation(1000)
        }
    }
    private suspend fun deviceOperation(sleepTime: Long) {
        while(true) {
            humidityValue += 5 * deviceStatus
            if (humidityValue > 100) humidityValue = 100
            else if (humidityValue < 0) humidityValue = 0
            val humidityStatus: String = if (humidityValue < 15) "RED-"
            else if (humidityValue < 30) "YELLOW-"
            else if (humidityValue < 65) "GREEN"
            else if (humidityValue < 75) "YELLOW+"
            else "RED+"
            mqttClient.publishMessage(MQTTHELPER.SENSOR_TOPIC, humidityStatus)
            delay(sleepTime)
        }
    }

    // función deviceOperation()
}