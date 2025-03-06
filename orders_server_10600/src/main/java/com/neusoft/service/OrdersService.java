package com.neusoft.service;
import java.util.List;

import com.neusoft.po.OrderDetailet;
import com.neusoft.po.Orders;
public interface OrdersService {
 public int createOrders(Orders orders);
 public Orders getOrdersById(Integer orderId);
 public List<Orders> listOrdersByUserId(String userId);

 public int payOk(Orders orders);
 
 public List<OrderDetailet> listOrderDetailetByOrderId(Orders orders);
 
 public List<Integer> listOdIdByOrderId(Orders orders);
}