package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Order;
import shopco.backend.enums.OrderStatus;
import shopco.backend.enums.PaymentStatus;
import shopco.backend.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }
    
    public Optional<Order> getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }
    
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public Page<Order> getOrdersByUserId(String userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }
    
    public List<Order> getOrdersByPaymentStatus(PaymentStatus paymentStatus) {
        return orderRepository.findByPayStatus(paymentStatus);
    }
    
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    public Order createOrder(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    public Order updateOrder(Order order) {
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }
    
    public Order updateOrderStatus(String id, OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setOrderStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    public Order updatePaymentStatus(String id, PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setPayStatus(paymentStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    public Long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByOrderStatus(status);
    }
    
    public Long countOrdersByPaymentStatus(PaymentStatus paymentStatus) {
        return orderRepository.countByPayStatus(paymentStatus);
    }
    
    public Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        Double total = orderRepository.sumFinalAmountByPayStatusAndDateRange(startDate, endDate);
        return total != null ? total : 0.0;
    }
}
