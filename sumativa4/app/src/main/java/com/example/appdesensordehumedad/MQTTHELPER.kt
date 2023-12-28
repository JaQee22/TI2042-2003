package com.example.appdesensordehumedad

import android.widget.RadioButton
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MQTTHELPER {
    private val SERVER_URI = "tcp://mqtt.eclipseprojects.io:1883"
    private val CLIENT_ID = "Jacqueline_mqtt"

    // Aquí se definen los tópico para el intercambio de mensajes
    companion object {
        const val SENSOR_TOPIC = "sensorTopic"  // Tópico para el sensor
        const val DEVICE_TOPIC = "deviceTopic"  // Tópico para el dispositivo
    }

    // Declaramos e inicializamos la instancia con el servidor MQTT
    private lateinit var mqttClient: MqttClient
    init { connectToMqttBroker() }
    private fun connectToMqttBroker() {
        try {
            val persistence = MemoryPersistence()
            mqttClient = MqttClient(SERVER_URI, CLIENT_ID, persistence)
            val options = MqttConnectOptions()
            options.isCleanSession = true
            mqttClient.connect(options)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // Acción de suscribirse a un tópico
    fun subscribeToTopic(topic: String, radioButtons: List<RadioButton>) {
        try {
            mqttClient.subscribe(topic) { _, message ->
                val payload = String(message.payload)
                if (payload.indexOf("RED") == 0) {
                    radioButtons[0].isChecked = false
                    radioButtons[1].isChecked = false
                    radioButtons[2].isChecked = true
                }
                else if (payload.indexOf("YELLOW") == 0) {
                    radioButtons[0].isChecked = false
                    radioButtons[1].isChecked = true
                    radioButtons[2].isChecked = false
                }
                else {
                    radioButtons[0].isChecked = true
                    radioButtons[1].isChecked = false
                    radioButtons[2].isChecked = false
                }
                println("[$topic] Mensaje recibido: $payload")
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // Acción que envía un mensaje a un tópico
    fun publishMessage(topic: String, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttClient.publish(topic, mqttMessage)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // Cerramos la conexión con el servicio MQTT
    fun disconnect() {
        mqttClient.disconnect()
    }
}
