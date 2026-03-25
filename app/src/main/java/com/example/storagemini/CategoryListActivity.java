package com.example.storagemini;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storagemini.database.AppDatabase;
import com.example.storagemini.model.Category;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryListActivity extends AppCompatActivity {

    private ListView lvCategories;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        db = AppDatabase.getInstance(this);
        lvCategories = findViewById(R.id.lvCategories);

        List<Category> categories = db.categoryDao().getAllCategories();
        List<String> categoryNames = categories.stream().map(c -> c.categoryName).collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryNames);
        lvCategories.setAdapter(adapter);

        lvCategories.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ProductListActivity.class);
            intent.putExtra("categoryId", categories.get(position).categoryId);
            startActivity(intent);
        });
    }
}
