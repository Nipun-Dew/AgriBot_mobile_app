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
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private static final String TAG = "MyActivity";
    private final String topic = "test1";
    private MqttClient client;
    private FirebaseDatabase firebaseDatabase;
    private TextView robotStat;
    private Button reconnect;

    public void publishStartSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("start".getBytes());
            msg.setQos(1);
            this.client.publish(this.topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishPauseSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("pause".getBytes());
            msg.setQos(2);
            this.client.publish(this.topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishStopSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("stop".getBytes());
            msg.setQos(2);
            if(this.client.isConnected()) {
                this.client.publish(this.topic, msg);
            }else{
                Log.d(TAG, "connectionLost");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishResetSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("reset".getBytes());
            msg.setQos(2);
            this.client.publish(this.topic, msg);
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
                this.client.publish(this.topic, msg);
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

            this.client = new MqttClient("tcp://192.168.1.4:1883", "AndroidThingSub", new MemoryPersistence());
            this.client.setCallback((MqttCallback) this);
<<<<<<< HEAD

            MqttConnectOptions authen = new MqttConnectOptions();
           // authen.setUserName(userName);
            //authen.setPassword(password.toCharArray());
            authen.setKeepAliveInterval(30);
            authen.setConnectionTimeout(10);

            this.client.connect(authen);
=======
            this.client.connect(extraOps);
            robotStat.setText(R.string.connectBroker);
            robotStat.setBackgroundResource(R.drawable.ic_connected);
            reconnect.setVisibility(View.INVISIBLE);
>>>>>>> 169614943585822efc1f9ea8ae73d7a9d7ebb2e0
            this.client.subscribe(this.topic);

            Log.d(TAG, "connectionLost");

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

    private void getProductID() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(Objects.requireNonNull(LoginActivity.loginID));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LoginActivity.User user = snapshot.getValue(LoginActivity.User.class);
                TextView productIDMsg = findViewById(R.id.textViewMsg);
                assert user != null;
                productIDMsg.setText(user.getOwner());
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

        robotStat = findViewById(R.id.textViewBrokerStat);
        reconnect = findViewById(R.id.buttonReconnect);
        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribeToBroker();
            }
        });
        //robotStat.setText(R.string.notConnect);
        //robotStat.setBackgroundResource(R.);

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
        robotStat.setText(R.string.notConnect);
        robotStat.setBackgroundResource(R.drawable.ic_disconnected);
        Toast.makeText(MainActivity.this, "Connection Lost!!!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        TextView publishedData = findViewById(R.id.textViewMsg);
        publishedData.setText(payload);
        Log.d(TAG, payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete");
    }
}
