package luissantiago.co.mqqt

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

/**
 * Created by Luis Santiago on 3/2/19.
 */


class MQTTService : Service (), MqttCallback, IMqttActionListener {



    lateinit var mqttClient: MqttAndroidClient
    lateinit var mqttClientId : String


    companion object {
        val MQTT_CONNECT = "mqtt_connect"
        val MQTT_DISCONNECT = "mqtt_disconnect"

        val MQTT_SERVER_URL = "tcp://broker.mqttdashboard.com:1883"

        // connection state filters
        val CONNECTION_SUCCESS = "CONNECTION_SUCCESS"
        val CONNECTION_FAILURE = "CONNECTION_FAILURE"
        val CONNECTION_LOST = "CONNECTION_LOST"
        val DISCONNECT_SUCCESS = "DISCONNECT_SUCCESS"

        val MQTT_MESSAGE_TYPE = "type"
        val MQTT_MESSAGE_PAYLOAD = "payload"

        val TOPICS = arrayOf("home_sensors_info", "home_lights")
    }

    override fun onBind(p0: Intent?): IBinder? {
      return null
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(0 , Notification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }


    private fun loggedMessage(message : String){
        Log.e("MQQTSERVICE" , message)
    }


    fun connectToServer(){
        mqttClient = MqttAndroidClient(this , MQTTService.MQTT_SERVER_URL , this.mqttClientId)
        mqttClient.setCallback(this)
        mqttClient.setTraceEnabled(true)


        val options = MqttConnectOptions()
        options.apply {
            connectionTimeout = 30
            isAutomaticReconnect = true
            isCleanSession = true
            keepAliveInterval = 120
        }


        mqttClient.connect(options , this)
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {

    }

    override fun connectionLost(cause: Throwable?) {

    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }

    override fun onSuccess(asyncActionToken: IMqttToken?) {

    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {

    }

}