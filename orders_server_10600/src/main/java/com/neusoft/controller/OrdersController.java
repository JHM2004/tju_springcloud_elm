package com.neusoft.controller;
import java.math.BigDecimal;
import java.util.List;

import com.neusoft.po.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.neusoft.po.OrderDetailet;
import com.neusoft.po.Orders;
import com.neusoft.service.OrdersService;

@RestController
@RequestMapping("/OrdersController")
@CrossOrigin("*")
public class OrdersController {
 @Autowired
 private OrdersService ordersService;

 @PostMapping("/createOrders/{userId}/{businessId}/{daId}/{orderTotal}")
 public CommonResult<Integer> createOrders(
         @PathVariable("userId") String userId,
         @PathVariable("businessId") Integer businessId,
         @PathVariable("daId") Integer daId,
         @PathVariable("orderTotal") BigDecimal orderTotal
 ) throws Exception{
  Orders orders = new Orders();
  orders.setUserId(userId);
  orders.setBusinessId(businessId);
  orders.setDaId(daId);
 orders.setOrderTotal(orderTotal);
 int result = ordersService.createOrders(orders);
  return new CommonResult(200,"success",result);
 }

 @GetMapping("/getOrdersById/{orderId}")
 public CommonResult<Orders> getOrdersById(
         @PathVariable("orderId") Integer orderId

 ) throws Exception{
  Orders orders = ordersService.getOrdersById(orderId);
  return new CommonResult(200,"success",orders);
 }

 @GetMapping("/listOrdersByUserId/{userId}")
 public CommonResult<List> listOrdersByUserId(
         @PathVariable("userId") String userId
 ) throws Exception{
  List<Orders> orders = ordersService.listOrdersByUserId(userId);
  return new CommonResult(200,"success",orders);
 }

 //TODO 下面可以改成restful风格的api

 @RequestMapping("/payOk")
 public int payOk(@RequestBody Orders orders) throws Exception{
  return ordersService.payOk(orders);
 }
 
 @RequestMapping("/listOrderDetailetByOrderId")
 public List<OrderDetailet> listOrderDetailetByOrderId(@RequestBody Orders orders){
	 return ordersService.listOrderDetailetByOrderId(orders);
 }
 
 @RequestMapping("/listOdIdByOrderId")
 public List<Integer> listOdIdByOrderId(@RequestBody Orders orders){
	 return ordersService.listOdIdByOrderId(orders);
 }

}