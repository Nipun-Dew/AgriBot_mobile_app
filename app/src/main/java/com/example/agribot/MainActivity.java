package com.example.agribot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static com.example.agribot.LoginActivity.userTopic;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private static final String TAG = "MyActivity";
    String topic = userTopic;
    private MqttClient client;
    private FirebaseDatabase firebaseDatabase;
    private TextView deviceStat;
    private TextView temp;
    public static String tempDataVar = "";
    public static String humDataVar = "";
    private String publishTopic = topic + "/Data";

    public void publishStartSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("start".getBytes());
            msg.setQos(2);
            if (this.client.isConnected()) {
                this.client.publish(publishTopic, msg);
                Toast.makeText(getApplicationContext(), "start signal sent", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "connection Lost");
                Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
            }
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void publishPauseSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("pause".getBytes());
            msg.setQos(2);
            if (this.client.isConnected()) {
                this.client.publish(publishTopic, msg);
                Toast.makeText(getApplicationContext(), "pause signal sent", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "connection Lost");
                Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
            }
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void publishStopSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("stop".getBytes());
            msg.setQos(2);
            if (this.client.isConnected()) {
                this.client.publish(publishTopic, msg);
                Toast.makeText(getApplicationContext(), "stop signal sent", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "connection Lost");
                Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
            }
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void publishResetSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("reset".getBytes());
            msg.setQos(2);
            if (this.client.isConnected()) {
                this.client.publish(publishTopic, msg);
                Toast.makeText(getApplicationContext(), "reset signal sent", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "connection Lost");
                Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
            }
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearFields(EditText t1, EditText t2, EditText t3, EditText t4) {
        t1.setText("");
        t2.setText("");
        t3.setText("");
        t4.setText("");
    }

    public void publishMapDataToBroker(View view) {
        EditText text1 = findViewById(R.id.editText5);
        EditText text2 = findViewById(R.id.editText8);
        EditText text3 = findViewById(R.id.editText6);
        EditText text4 = findViewById(R.id.editText7);

        String rowLength = text1.getText().toString();
        String seedGap = text2.getText().toString();
        String numberOfRows = text3.getText().toString();
        String rowGap = text4.getText().toString();

        if (ValidateMapData.isDataEmpty(rowLength, seedGap, numberOfRows, rowGap)) {
            Toast.makeText(MainActivity.this, "Empty Fields!", Toast.LENGTH_SHORT).show();
            clearFields(text1, text2, text3, text4);
        } else if (ValidateMapData.validate(rowLength, seedGap, numberOfRows, rowGap)) {
            String mapData = "{1:" + rowLength + ", 2:" + seedGap + ", 3:" + numberOfRows + ", 4:" + rowGap + "}";
            try {
                MqttMessage msg = new MqttMessage(mapData.getBytes());
                msg.setQos(2);
                if (this.client.isConnected()) {
                    this.client.publish(publishTopic, msg);
                    Toast.makeText(getApplicationContext(), "data published!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "connection Lost");
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
            clearFields(text1, text2, text3, text4);
        } else {
            Toast.makeText(MainActivity.this, "Invalid Data!", Toast.LENGTH_SHORT).show();
            clearFields(text1, text2, text3, text4);
        }
    }

    @SuppressLint("WrongConstant")
    private void subscribeToBroker() {
        try {
            MqttConnectOptions extraOps = new MqttConnectOptions();
            extraOps.setConnectionTimeout(30);
            extraOps.setAutomaticReconnect(true);
            // extraOps.setKeepAliveInterval(15);
            //extraOps.setUserName("metana username eka dapan");
            //extraOps.setPassword(metana password eka dapan);
            extraOps.setCleanSession(true);

            String subscriberTopic1 = topic + "/State";
            String subscriberTopic2 = topic + "/Sensor/Temperature";
            String subscriberTopic3 = topic + "/Sensor/Humidity";
            String[] subscriberTopics = {subscriberTopic1, subscriberTopic2, subscriberTopic3};

            this.client = new MqttClient("tcp://52.201.221.111:1883", LoginActivity.loginID, new MemoryPersistence());
            Log.d(TAG, LoginActivity.loginID);
            this.client.setCallback((MqttCallback) this);
            this.client.connect(extraOps);
            this.client.subscribe(subscriberTopics);

            Log.d(TAG, "connectionLost");
            Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Connection Timeout!!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            //robotStat.setText(R.string.notConnect);
            //robotStat.setBackgroundResource(R.drawable.ic_disconnected);
            //Toast.makeText(MainActivity.this, "Connection Timeout!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceStat = findViewById(R.id.textViewDeviceStat);
        temp = findViewById(R.id.txtTemperature);

        //getProductID();

        subscribeToBroker();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                    new Fragment_Configuration()).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_config:
                            selectedFragment = new Fragment_Configuration();
                            break;
                        case R.id.nav_sensor_data:
                            Bundle data = new Bundle();//create bundle instance
                            data.putString("temperature", tempDataVar);//put string to pass with a key value
                            data.putString("humidity", humDataVar);//put string to pass with a key value
                            selectedFragment = new Fragment_SensorData();
                            selectedFragment.setArguments(data);
                            break;
                        case R.id.nav_logout:
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                            selectedFragment).commit();
                    return true;
                }
            };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenu: {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            }
            case R.id.settingsMenu: {
                break;
            }
            case R.id.deviceInfo: {
                startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Toast.makeText(MainActivity.this, "Connection Lost!!!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "connectionLost");
    }

    @Override
    public void messageArrived(String inputTopic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        if (inputTopic.equals(topic + "/State") && payload.equals("connect")) {
            deviceStat.setText("connected");
            deviceStat.setBackgroundResource(R.drawable.ic_connected);
        }
        if (inputTopic.equals(topic + "/State") && payload.equals("disconnect")) {
            deviceStat.setText("disconnect");
            deviceStat.setBackgroundResource(R.drawable.ic_disconnected);
        }
        if (inputTopic.equals(topic + "/Sensor/Temperature")) {
            //TextView tempData = findViewById(R.id.textView6);
            //tempData.setText(payload);
            tempDataVar = payload;
           // temp.setText(tempDataVar);
            Log.d(TAG, payload);
        }
        if (inputTopic.equals(topic + "/Sensor/Humidity")) {
           // TextView humidData = findViewById(R.id.textView6);
          //  humidData.setText(payload);
            humDataVar = payload;
           // deviceStat.setText(humDataVar);
            Log.d(TAG, payload);
        }
        //TextView publishedData = findViewById(R.id.textViewMsg);
        //publishedData.setText(payload);
        //Log.d(TAG, payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete");
    }
    /*
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("Re-Connection Attempt " + reconnect);
        if(reconnect) {
            try {
                String subscriberTopics = topic + "/#";
                MqttTopic.validate(this.topic, true);
                this.client.subscribe(subscriberTopics);
            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }*/
}
