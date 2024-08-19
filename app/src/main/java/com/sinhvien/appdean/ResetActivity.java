package com.sinhvien.appdean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResetActivity extends AppCompatActivity {
    TextView email;
    EditText pass,repass;
    Button btnConfirm;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset);
        anhXa();
        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        email.setText(intent.getStringExtra("username"));

        btnConfirm.setOnClickListener(getForgotPassWord());
    }
    public void anhXa(){
        email =(TextView) findViewById(R.id.username_reset_text);
        btnConfirm =(Button) findViewById(R.id.btnconfirm);
        pass =(EditText) findViewById(R.id.password_reset);
        repass =(EditText) findViewById(R.id.repassword_reset) ;
    }
    @NonNull
    private View.OnClickListener getForgotPassWord() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = email.getText().toString();
                String password = pass.getText().toString();
                String rePassword = repass.getText().toString();
                if (password.isEmpty() || rePassword.isEmpty()) {
                    Toast.makeText(ResetActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (password.equals(rePassword)) {
                    Boolean checkPasswordUpdate = databaseHelper.updatepassword(user, password);
                    if (checkPasswordUpdate == true) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(ResetActivity.this, "Mật khẩu đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ResetActivity.this, "Mật khẩu cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetActivity.this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

}