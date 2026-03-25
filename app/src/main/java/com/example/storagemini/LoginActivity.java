package com.example.storagemini;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storagemini.database.AppDatabase;
import com.example.storagemini.model.User;

public class LoginActivity extends AppCompatActivity {

    // Khai báo các thành phần giao diện
    private EditText etUsername, etPassword;
    private Button btnLoginSubmit;

    // Database
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo database
        db = AppDatabase.getInstance(this);

        // Ánh xạ view từ XML
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);

        // Xử lý sự kiện khi bấm nút Login
        btnLoginSubmit.setOnClickListener(v -> {

            // Lấy dữ liệu người dùng nhập
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // Kiểm tra thông tin đăng nhập trong database
            User user = db.userDao().login(username, password);

            if (user != null) {
                // Nếu đăng nhập thành công

                // Lưu thông tin user vào SharedPreferences
                SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("userId", user.userId);
                editor.putString("fullName", user.fullName);
                editor.apply();

                // Hiển thị thông báo
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Đóng màn hình login để quay về MainActivity
                finish();
            } else {
                // Nếu sai tài khoản hoặc mật khẩu
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
