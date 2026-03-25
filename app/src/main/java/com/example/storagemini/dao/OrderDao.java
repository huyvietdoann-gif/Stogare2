package com.example.storagemini.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.storagemini.model.Order;

@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = 'Pending' LIMIT 1")
    Order getPendingOrderByUser(int userId);

    @Update
    void update(Order order);

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    Order getOrderById(int orderId);
}
