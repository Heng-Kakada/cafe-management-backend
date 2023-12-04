package com.api.controller;

import com.api.dto.request.OrderRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    @PostMapping("order")
    public ResponseEntity<?> order(@RequestBody OrderRequest orderRequest){
        ResponseErrorTemplate response = orderService.saveOrder(orderRequest);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable("id") Long id){
        ResponseErrorTemplate response = orderService.cancelOrder(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping()
    public ResponseEntity<?> getAllOrders(@RequestParam Map<String, String> params){
        PageResponse response = orderService.getAllOrders(params);
        return ResponseEntity.ok().body(response);
    }


}
