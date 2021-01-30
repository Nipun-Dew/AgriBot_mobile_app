package com.example.agribot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText userProductId;
    private EditText userPassword;
    private Button loginBtn;
    private ProgressDialog progressDialog;

    public static String loginID;
    public static String userTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUIComp();

        progressDialog = new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = userProductId.getText().toString().trim();
                String user_password = userPassword.getText().toString().trim();
                if (user_id.isEmpty() || user_password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                } else {
                    validate(user_id, user_password);
                }
            }
        });
    }


    private void validate(final String product_ID, final String user_password) {

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myDBRef = firebaseDatabase.getReference(product_ID);
        myDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                System.out.println(user);
                progressDialog.dismiss();
                if (user != null) {
                    if (user.getPassword().equals(user_password)) {
                        finish();
                        loginID = product_ID;
                        userTopic = user.getTopic();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        userProductId.setText("");
                        userPassword.setText("");
                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    userProductId.setText("");
                    userPassword.setText("");
                    Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getCode());
                userProductId.setText("");
                userPassword.setText("");
                Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupUIComp() {
        userProductId = (EditText) findViewById(R.id.editTextLoginMail);
        userPassword = (EditText) findViewById(R.id.editTextLoginPasswrd);
        loginBtn = (Button) findViewById(R.id.Loginbutton);
        TextView productIDInfo = (TextView) findViewById(R.id.textViewProdID);
        TextView productPasswordInfo = (TextView) findViewById(R.id.textViewProdPassword);
    }
}
