package com.neusoft.controller;

import java.util.List;

import com.neusoft.po.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.neusoft.po.DeliveryAddress;
import com.neusoft.service.DeliveryAddressService;

@RestController
@RequestMapping("/DeliveryAddressController")
@CrossOrigin("*")
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @GetMapping("/listDeliveryAddressByUserId/{userId}")
    public CommonResult<List> listDeliveryAddressByUserId(
            @PathVariable("userId") String userId
    ) throws Exception {
        List<DeliveryAddress> deliveryAddresses = deliveryAddressService.listDeliveryAddressByUserId(userId);
        return new CommonResult(200, "success", deliveryAddresses);
    }

    @GetMapping("/getDeliveryAddressById/{daId}")
    public CommonResult<DeliveryAddress> getDeliveryAddressById(
            @PathVariable("daId") Integer daId
    ) throws Exception {
        DeliveryAddress deliveryAddressById = deliveryAddressService.getDeliveryAddressById(daId);
        return new CommonResult(200, "success", deliveryAddressById);
    }

    @PostMapping("/saveDeliveryAddress/{contactName}/{contactSex}/{contactTel}/{address}/{userId}")
    public CommonResult<Integer> saveDeliveryAddress(
            @PathVariable("contactName") String contactName,
            @PathVariable("contactSex") Integer contactSex,
            @PathVariable("contactTel") String contactTel,
            @PathVariable("address") String address,
            @PathVariable("userId") String userId
    ) throws Exception {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setContactName(contactName);
        deliveryAddress.setContactSex(contactSex);
        deliveryAddress.setContactTel(contactTel);
        deliveryAddress.setAddress(address);
        deliveryAddress.setUserId(userId);
        int result = deliveryAddressService.saveDeliveryAddress(deliveryAddress);
        return new CommonResult(200, "success", result);
    }

    @PutMapping("/updateDeliveryAddress/{daId}/{contactName}/{contactSex}/{contactTel}/{address}")
    public CommonResult<Integer> updateDeliveryAddress(
            @PathVariable("daId") Integer daId,
            @PathVariable("contactName") String contactName,
            @PathVariable("contactSex") Integer contactSex,
            @PathVariable("contactTel") String contactTel,
            @PathVariable("address") String address
    ) throws Exception {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setDaId(daId);
        deliveryAddress.setContactName(contactName);
        deliveryAddress.setContactSex(contactSex);
        deliveryAddress.setContactTel(contactTel);
        deliveryAddress.setAddress(address);
        int result = deliveryAddressService.updateDeliveryAddress(deliveryAddress);
        return new CommonResult(200, "success", result);
    }

    @DeleteMapping("/removeDeliveryAddress/{daId}")
    public CommonResult<Integer> removeDeliveryAddress(
            @PathVariable("daId") Integer daId
    ) throws Exception {
        int result = deliveryAddressService.removeDeliveryAddress(daId);
        return new CommonResult(200, "success", result);

    }
}