package com.neusoft.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.neusoft.po.OrderDetailet;
import com.neusoft.po.OrderDetailet_Food;
import com.neusoft.po.Orders;
@Mapper
public interface OrderDetailetMapper {
	
 public int saveOrderDetailetBatch(@Param("list") List<OrderDetailet> list);
 
 
 public List<OrderDetailet> listOrderDetailetByOrderId(Integer orderId);
 
 @Select("select * from orderdetailet where orderId=#{orderId}")
 public List<OrderDetailet> listorderDetailetByOrderId(Orders orders);
}