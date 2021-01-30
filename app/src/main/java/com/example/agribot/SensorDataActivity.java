package com.example.agribot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.agribot.MainActivity.tempDataVar;
import static com.example.agribot.MainActivity.humDataVar;

public class SensorDataActivity extends AppCompatActivity {

    String tmp = tempDataVar;
    String hum = humDataVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        TextView var1 = findViewById(R.id.textViewTempShow);
        TextView var2 = findViewById(R.id.textViewHumShow);
        var1.setText(tmp);
        var2.setText(hum);
        //Toast.makeText(SensorDataActivity.this, tmp, Toast.LENGTH_SHORT).show();
    }
}
