package shopco.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopco.backend.entity.Payment;
import shopco.backend.enums.PaymentStatus;
import shopco.backend.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PaymentController()).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllPayments_ShouldReturnPageOfPayments() throws Exception {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PENDING);
        List<Payment> payments = Arrays.asList(payment1, payment2);
        Page<Payment> page = new PageImpl<>(payments);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(paymentService.getAllPayments(any(PageRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/payments")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"));

        verify(paymentService).getAllPayments(any(PageRequest.class));
    }

    @Test
    void getPaymentById_WhenExists_ShouldReturnPayment() throws Exception {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        when(paymentService.getPaymentById("1")).thenReturn(Optional.of(payment));

        // When & Then
        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.orderId").value("order1"))
                .andExpect(jsonPath("$.status").value("PAID"));

        verify(paymentService).getPaymentById("1");
    }

    @Test
    void getPaymentById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(paymentService.getPaymentById("1")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isNotFound());

        verify(paymentService).getPaymentById("1");
    }

    @Test
    void getPaymentByTransactionId_ShouldReturnPayment() throws Exception {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        payment.setTransactionId("txn_123");
        when(paymentService.getPaymentByTransactionId("txn_123")).thenReturn(Optional.of(payment));

        // When & Then
        mockMvc.perform(get("/api/payments/transaction/txn_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("txn_123"));

        verify(paymentService).getPaymentByTransactionId("txn_123");
    }

    @Test
    void getPaymentsByOrderId_ShouldReturnOrderPayments() throws Exception {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order1", PaymentStatus.REFUNDED);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentService.getPaymentsByOrderId("order1")).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/order/order1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value("order1"))
                .andExpect(jsonPath("$[1].orderId").value("order1"));

        verify(paymentService).getPaymentsByOrderId("order1");
    }

    @Test
    void getPaymentsByUserId_ShouldReturnUserPayments() throws Exception {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PENDING);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentService.getPaymentsByUserId("user1")).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(paymentService).getPaymentsByUserId("user1");
    }

    @Test
    void getPaymentsByStatus_ShouldReturnPaymentsByStatus() throws Exception {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PAID);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentService.getPaymentsByStatus(PaymentStatus.PAID)).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/status/PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(jsonPath("$[1].status").value("PAID"));

        verify(paymentService).getPaymentsByStatus(PaymentStatus.PAID);
    }

    @Test
    void createPayment_ShouldReturnCreatedPayment() throws Exception {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PENDING);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(payment);

        // When & Then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.orderId").value("order1"));

        verify(paymentService).createPayment(any(Payment.class));
    }

    @Test
    void updatePayment_ShouldReturnUpdatedPayment() throws Exception {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        when(paymentService.updatePayment(any(Payment.class))).thenReturn(payment);

        // When & Then
        mockMvc.perform(put("/api/payments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(paymentService).updatePayment(any(Payment.class));
    }

    @Test
    void deletePayment_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(paymentService).deletePayment("1");

        // When & Then
        mockMvc.perform(delete("/api/payments/1"))
                .andExpect(status().isNoContent());

        verify(paymentService).deletePayment("1");
    }

    @Test
    void updatePaymentStatus_ShouldReturnUpdatedPayment() throws Exception {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        when(paymentService.updatePaymentStatus("1", PaymentStatus.PAID)).thenReturn(payment);

        // When & Then
        mockMvc.perform(put("/api/payments/1/status")
                .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        verify(paymentService).updatePaymentStatus("1", PaymentStatus.PAID);
    }

    @Test
    void processRefund_ShouldReturnUpdatedPayment() throws Exception {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.REFUNDED);
        payment.setRefundAmount(new BigDecimal("50.00"));
        when(paymentService.processRefund("1", 50.0)).thenReturn(payment);

        // When & Then
        mockMvc.perform(post("/api/payments/1/refund")
                .param("refundAmount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        verify(paymentService).processRefund("1", 50.0);
    }

    @Test
    void countPaymentsByStatus_ShouldReturnCount() throws Exception {
        // Given
        when(paymentService.countPaymentsByStatus(PaymentStatus.PAID)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/payments/stats/count-by-status")
                .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(paymentService).countPaymentsByStatus(PaymentStatus.PAID);
    }

    @Test
    void getTotalRevenue_ShouldReturnRevenue() throws Exception {
        // Given
        when(paymentService.getTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(1000.0);

        // When & Then
        mockMvc.perform(get("/api/payments/stats/revenue")
                .param("startDate", "2024-01-01T00:00:00")
                .param("endDate", "2024-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1000.0));

        verify(paymentService).getTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    private Payment createTestPayment(String id, String orderId, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setOrderId(orderId);
        payment.setUserId("user1");
        payment.setAmount(new BigDecimal("100.00"));
        payment.setStatus(status);
        payment.setMethod("CREDIT_CARD");
        payment.setTransactionId("txn_" + id);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }
}
