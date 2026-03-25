package com.example.storagemini;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storagemini.database.AppDatabase;
import com.example.storagemini.model.Order;
import com.example.storagemini.model.OrderDetail;
import com.example.storagemini.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private ListView lvCartItems;
    private TextView tvTotal;
    private Button btnPay;
    private AppDatabase db;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = AppDatabase.getInstance(this);
        lvCartItems = findViewById(R.id.lvCartItems);
        tvTotal = findViewById(R.id.tvTotal);
        btnPay = findViewById(R.id.btnPay);

        loadCart();

        btnPay.setOnClickListener(v -> {
            if (currentOrder != null) {
                currentOrder.status = "Paid";
                db.orderDao().update(currentOrder);
                showInvoice();
            }
        });
    }

    private void loadCart() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sp.getInt("userId", -1);

        currentOrder = db.orderDao().getPendingOrderByUser(userId);
        if (currentOrder != null) {
            List<OrderDetail> details = db.orderDetailDao().getDetailsByOrder(currentOrder.orderId);
            List<String> displayList = new ArrayList<>();
            double total = 0;

            for (OrderDetail detail : details) {
                Product p = db.productDao().getProductById(detail.productId);
                String name = (p != null) ? p.productName : "Unknown";
                displayList.add(name + " x" + detail.quantity + " - $" + (detail.unitPrice * detail.quantity));
                total += detail.unitPrice * detail.quantity;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
            lvCartItems.setAdapter(adapter);
            tvTotal.setText("Total: $" + total);
        } else {
            tvTotal.setText("Total: $0");
            btnPay.setEnabled(false);
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void showInvoice() {
        StringBuilder invoice = new StringBuilder();
        invoice.append("Order ID: ").append(currentOrder.orderId).append("\n");
        invoice.append("Date: ").append(currentOrder.orderDate).append("\n");
        invoice.append("Status: ").append(currentOrder.status).append("\n\n");
        invoice.append("Items:\n");

        List<OrderDetail> details = db.orderDetailDao().getDetailsByOrder(currentOrder.orderId);
        double total = 0;
        for (OrderDetail detail : details) {
            Product p = db.productDao().getProductById(detail.productId);
            String name = (p != null) ? p.productName : "Unknown";
            invoice.append("- ").append(name).append(" x").append(detail.quantity).append(": $").append(detail.unitPrice * detail.quantity).append("\n");
            total += detail.unitPrice * detail.quantity;
        }
        invoice.append("\nTotal: $").append(total);

        new AlertDialog.Builder(this)
                .setTitle("Invoice")
                .setMessage(invoice.toString())
                .setPositiveButton("OK", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
