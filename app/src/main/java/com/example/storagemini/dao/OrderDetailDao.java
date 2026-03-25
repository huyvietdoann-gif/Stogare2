package com.example.storagemini.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.storagemini.model.OrderDetail;
import java.util.List;

@Dao
public interface OrderDetailDao {
    @Insert
    void insert(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getDetailsByOrder(int orderId);
}
