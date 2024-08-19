package com.sinhvien.appdean;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {
    EditText username;
    Button reset;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_password);
        username =(EditText) findViewById(R.id.email_reset);
        reset =(Button) findViewById(R.id.btn_reset);
        databaseHelper = new DatabaseHelper(this);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();

                Boolean checkuser = databaseHelper.checkEmail(user);
                if(checkuser == true)
                {
                    Intent intent = new Intent(getApplicationContext(), ResetActivity.class);
                    intent.putExtra("username", user);
                    startActivity(intent);
                }else
                {
                    Toast.makeText(PasswordActivity.this, "Email không tồn tại hoặc rỗng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}