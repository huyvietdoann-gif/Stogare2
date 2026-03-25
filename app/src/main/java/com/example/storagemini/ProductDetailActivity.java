package com.example.storagemini;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storagemini.database.AppDatabase;
import com.example.storagemini.model.Order;
import com.example.storagemini.model.OrderDetail;
import com.example.storagemini.model.Product;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvName, tvPrice, tvDesc;
    private Button btnAddToCart;
    private AppDatabase db;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = AppDatabase.getInstance(this);
        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDesc = findViewById(R.id.tvDetailDesc);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        int productId = getIntent().getIntExtra("productId", -1);
        product = db.productDao().getProductById(productId);

        if (product != null) {
            tvName.setText(product.productName);
            tvPrice.setText("$" + product.price);
            tvDesc.setText(product.description);
        }

        btnAddToCart.setOnClickListener(v -> {
            handleAddToCart();
        });
    }

    private void handleAddToCart() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sp.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        // Logic for adding to order
        Order pendingOrder = db.orderDao().getPendingOrderByUser(userId);
        if (pendingOrder == null) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            long orderId = db.orderDao().insert(new Order(userId, currentDate, "Pending"));
            pendingOrder = db.orderDao().getOrderById((int) orderId);
        }

        db.orderDetailDao().insert(new OrderDetail(pendingOrder.orderId, product.productId, 1, product.price));
        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();

        showPostAddDialog();
    }

    private void showPostAddDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Continue?")
                .setMessage("Do you want to continue shopping or checkout?")
                .setPositiveButton("Checkout", (dialog, which) -> {
                    startActivity(new Intent(this, CheckoutActivity.class));
                    finish();
                })
                .setNegativeButton("Continue Shopping", (dialog, which) -> {
                    finish();
                })
                .show();
    }
}
