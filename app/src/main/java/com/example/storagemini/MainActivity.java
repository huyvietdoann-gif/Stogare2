package com.example.storagemini;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storagemini.database.AppDatabase;
import com.example.storagemini.model.Category;
import com.example.storagemini.model.Product;
import com.example.storagemini.model.User;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnViewCategories, btnViewProducts;
    private TextView tvWelcome;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        seedData();

        btnLogin = findViewById(R.id.btnLogin);
        btnViewCategories = findViewById(R.id.btnViewCategories);
        btnViewProducts = findViewById(R.id.btnViewProducts);
        tvWelcome = findViewById(R.id.tvWelcome);

        updateLoginUI();

        btnLogin.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            if (sp.getInt("userId", -1) != -1) {
                // Logout
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                updateLoginUI();
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnViewCategories.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CategoryListActivity.class));
        });

        btnViewProducts.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProductListActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginUI();
    }

    private void updateLoginUI() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sp.getInt("userId", -1);
        String fullName = sp.getString("fullName", "");

        if (userId != -1) {
            tvWelcome.setText("Welcome, " + fullName + "!");
            btnLogin.setText("Logout");
        } else {
            tvWelcome.setText("Welcome! Please login.");
            btnLogin.setText("Login");
        }
    }

    private void seedData() {
        if (db.userDao().count() == 0) {
            db.userDao().insert(new User("admin", "123", "Administrator"));
            db.userDao().insert(new User("user1", "123", "User One"));
        }
        if (db.categoryDao().count() == 0) {
            db.categoryDao().insert(new Category("Electronics"));
            db.categoryDao().insert(new Category("Clothing"));
            db.categoryDao().insert(new Category("Home & Garden"));
        }
        if (db.productDao().count() == 0) {
            db.productDao().insert(new Product("Smartphone", 500, "Latest model smartphone", 1));
            db.productDao().insert(new Product("Laptop", 1000, "High performance laptop", 1));
            db.productDao().insert(new Product("T-shirt", 20, "Cotton t-shirt", 2));
            db.productDao().insert(new Product("Jeans", 50, "Blue denim jeans", 2));
            db.productDao().insert(new Product("Coffee Maker", 80, "Drip coffee maker", 3));
        }
    }
}
