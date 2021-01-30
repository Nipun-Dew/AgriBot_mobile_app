package com.example.agribot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Objects;

import static com.example.agribot.LoginActivity.userTopic;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private static final String TAG = "MyActivity";
    String topic = userTopic;
    private MqttClient client;
    private TextView deviceStat;

    public void publishStartSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("start".getBytes());
            msg.setQos(1);
            String startSignalTopic = topic + "/Data";
            this.client.publish(startSignalTopic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishPauseSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("pause".getBytes());
            msg.setQos(2);
            String pauseSignalTopic = topic + "/Data";
            this.client.publish(pauseSignalTopic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishStopSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("stop".getBytes());
            msg.setQos(2);
            String stopSignalTopic = topic + "/Data";
            this.client.publish(stopSignalTopic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishResetSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("reset".getBytes());
            msg.setQos(2);
            String resetSignalTopic = topic + "/Data";
            this.client.publish(resetSignalTopic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void clearFields(EditText t1, EditText t2, EditText t3, EditText t4) {
        t1.setText("");
        t2.setText("");
        t3.setText("");
        t4.setText("");
    }

    public void publishMapDataToBroker(View view) {
        EditText text1 = findViewById(R.id.editText1);
        EditText text2 = findViewById(R.id.editText2);
        EditText text3 = findViewById(R.id.editText3);
        EditText text4 = findViewById(R.id.editText4);

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
                String mapDataTopic = topic + "/Data";
                this.client.publish(mapDataTopic, msg);
            } catch (MqttException e) {
                e.printStackTrace();
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
            extraOps.setConnectionTimeout(3);
            extraOps.setAutomaticReconnect(true);

            String subscriberTopic = topic + "/#";
            MqttTopic.validate(this.topic, true);

            this.client = new MqttClient("tcp://192.168.1.4:1883", "AndroidThingSub", new MemoryPersistence());
            this.client.setCallback((MqttCallback) this);
            this.client.connect(extraOps);
            this.client.subscribe(subscriberTopic);

            Log.d(TAG, "connectionLost");

        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Connection Timeout!!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void getProductID() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(Objects.requireNonNull(LoginActivity.loginID));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView device = findViewById(R.id.textViewDevice);
        deviceStat = findViewById(R.id.textViewDeviceStat);

        getProductID();

        subscribeToBroker();
    }

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

    @SuppressLint("SetTextI18n")
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
            TextView tempData = findViewById(R.id.textViewTempVal);
            tempData.setText(payload);
            Log.d(TAG, payload);
        }
        if (inputTopic.equals(topic + "/Sensor/Humidity")) {
            TextView humidData = findViewById(R.id.textViewHumVal);
            humidData.setText(payload);
            Log.d(TAG, payload);
        }
        if (inputTopic.equals(topic + "/willtest")) {
            TextView humidData = findViewById(R.id.textViewHumVal);
            humidData.setText(payload);
            Log.d(TAG, payload);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete");
    }
}
