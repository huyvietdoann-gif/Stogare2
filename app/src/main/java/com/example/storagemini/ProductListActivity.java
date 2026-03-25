package com.example.storagemini;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storagemini.database.AppDatabase;
import com.example.storagemini.model.Product;
import java.util.List;
import java.util.stream.Collectors;

public class ProductListActivity extends AppCompatActivity {

    private ListView lvProducts;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        db = AppDatabase.getInstance(this);
        lvProducts = findViewById(R.id.lvProducts);

        int categoryId = getIntent().getIntExtra("categoryId", -1);
        List<Product> products;
        if (categoryId != -1) {
            products = db.productDao().getProductsByCategory(categoryId);
        } else {
            products = db.productDao().getAllProducts();
        }

        List<String> productDisplay = products.stream()
                .map(p -> p.productName + " - $" + p.price)
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productDisplay);
        lvProducts.setAdapter(adapter);

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("productId", products.get(position).productId);
            startActivity(intent);
        });
    }
}
