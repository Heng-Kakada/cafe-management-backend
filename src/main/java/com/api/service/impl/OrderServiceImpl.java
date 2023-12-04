package com.api.service.impl;


import com.api.dto.request.OrderRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.exception.BaseException;
import com.api.model.Order;
import com.api.model.OrderDetail;
import com.api.model.Product;
import com.api.repository.OrderDetailsRepository;
import com.api.repository.OrderRepository;
import com.api.service.OrderService;
import com.api.service.ProductService;
import com.api.utils.ConstantUtils;
import com.api.utils.PageUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    @Override
    public ResponseErrorTemplate saveOrder(OrderRequest request) {
        //save order
        Order order = new Order();
        order.setOrderDate(request.getOrderDate());
        order.setActive(true);

        orderRepository.save(order);
        //save order detail
        request.getOrderProducts().forEach(
                op -> {
                    Product p = productService.getProductById( op.getId() );
                    OrderDetail od = new OrderDetail();
                    od.setUnit(op.getUnit());
                    od.setAmount(p.getPrice());
                    od.setOrder(order);
                    od.setProduct(p);

                    orderDetailsRepository.save(od);
                }
        );

        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("success order")
                .data(order)
                .build();
    }

    @Override
    public ResponseErrorTemplate cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (!order.getActive()){
            throw new BaseException(ConstantUtils.SC_NF, "This Order is already cancel");
        }
        order.setActive(false);

        order = orderRepository.save(order);

        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("success cancel")
                .data(order)
                .build();
    }

    @Override
    public PageResponse getAllOrders(Map<String, String> params) {

        int pageLimit = PageUtils.DEFAULT_PAGE_LIMIT;
        if (params.containsKey(PageUtils.PAGE_LIMIT)){
            pageLimit = Integer.parseInt(params.get(PageUtils.PAGE_LIMIT));
        }

        int pageNumber = PageUtils.DEFAULT_PAGE_NUMBER;
        if (params.containsKey(PageUtils.PAGE_NUMBER)){
            pageNumber = Integer.parseInt(params.get(PageUtils.PAGE_NUMBER));
        }

        Pageable pageable = PageUtils.getPageable(pageNumber, pageLimit);
        Page<Order> pages = orderRepository.findAll(pageable);

        return new PageResponse(pages);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new BaseException(ConstantUtils.SC_NF, "Order id = %d not found!".formatted(id)));
    }


}
