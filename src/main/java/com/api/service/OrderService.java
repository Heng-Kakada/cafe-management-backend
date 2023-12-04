package com.api.service;

import com.api.dto.request.OrderRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.model.Order;

import java.util.Map;

public interface OrderService {
    ResponseErrorTemplate saveOrder(OrderRequest request);
    ResponseErrorTemplate cancelOrder(Long id);
    PageResponse getAllOrders(Map<String, String> params);
    Order getOrderById(Long id);

}
