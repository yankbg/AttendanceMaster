package com.example.attendance_master;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsername = findViewById(R.id.edt_username_login);
        edtPassword = findViewById(R.id.edt_password_login);
        btnLogIn = findViewById(R.id.btn_login);

        btnLogIn.setOnClickListener(view -> btnLogInClicked());
    }

    private void btnLogInClicked() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if(username.equals("Admin")){
            if(password.equals("123456")){
                Intent intent = new Intent(this,Dashboard.class);
                intent.putExtra("checkUser",username);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        }else{
            Intent intent = new Intent(this, Dashboard.class);
            intent.putExtra("checkUser",username);
            startActivity(intent);
        }
    }
}