package com.sinhvien.appdean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.sinhvien.appdean.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    public static Intent intent;
    ActivityLoginBinding binding; //liên kết các phần tử giao diện với các biến trong Activity
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean savelogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper(this);

        sharedPreferences = getSharedPreferences("loginref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        savelogin = sharedPreferences.getBoolean("savelogin", false);

        if (savelogin == true) {
            binding.loginEmail.setText(sharedPreferences.getString("username", ""));
            binding.loginPassword.setText(sharedPreferences.getString("password", ""));
            binding.checkBoxRemember.setChecked(true);
        }

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();
                // Kiểm tra xem chuỗi nhập vào có đúng định dạng email hay không
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập địa chỉ email đúng định dạng", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty() || password.isEmpty())
                    Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ", Toast.LENGTH_SHORT).show();
                else {
                    Boolean checkCredentials = databaseHelper.checkEmailPassword(email, password);
                    if (checkCredentials == true) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công với Email: " + email, Toast.LENGTH_LONG).show();
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("password", password);

                        if (binding.checkBoxRemember.isChecked()) {
                            editor.putBoolean("savelogin", true);
                            editor.putString("username", email);
                            editor.putString("password", password);
                            editor.apply();
                        } else {
                            editor.clear();
                            editor.apply();
                        }

                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        binding.tvforger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Tạo đối tượng SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Lưu giá trị của EditText vào SharedPreferences
        String text = binding.loginEmail.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("EDIT_TEXT_VALUE", text);
        editor.apply();

        // Nếu checkbox được chọn, lưu trạng thái và giá trị của EditText vào SharedPreferences
        if (binding.checkBoxRemember.isChecked()) {
            editor.putBoolean("savelogin", true);
            editor.putString("username", binding.loginEmail.getText().toString());
            editor.putString("password", binding.loginPassword.getText().toString());
        }
        // Nếu checkbox không được chọn, xóa giá trị lưu trữ trong SharedPreferences
        else {
            editor.remove("savelogin");
            editor.remove("username");
            editor.remove("password");

        }
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Đọc trạng thái của checkbox từ SharedPreferences
        savelogin = sharedPreferences.getBoolean("savelogin", false);
        binding.checkBoxRemember.setChecked(savelogin);

        // Đọc giá trị của EditText từ SharedPreferences
        binding.loginEmail.setText(sharedPreferences.getString("username", ""));
        binding.loginPassword.setText(sharedPreferences.getString("password", ""));
    }

}
