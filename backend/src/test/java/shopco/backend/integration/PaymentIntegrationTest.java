package shopco.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Payment;
import shopco.backend.enums.PaymentStatus;
import shopco.backend.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    void createPayment_ShouldSaveToDatabase() throws Exception {
        // Given
        Payment payment = createTestPayment("order1", PaymentStatus.PENDING);

        // When
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("order1"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Then
        assertEquals(1, paymentRepository.count());
        Payment savedPayment = paymentRepository.findAll().get(0);
        assertEquals("order1", savedPayment.getOrderId());
        assertEquals(PaymentStatus.PENDING, savedPayment.getStatus());
        assertNotNull(savedPayment.getCreatedAt());
        assertNotNull(savedPayment.getUpdatedAt());
    }

    @Test
    void getPaymentById_ShouldReturnPayment() throws Exception {
        // Given
        Payment payment = createTestPayment("order1", PaymentStatus.PAID);
        payment = paymentRepository.save(payment);

        // When & Then
        mockMvc.perform(get("/api/payments/{id}", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId()))
                .andExpect(jsonPath("$.orderId").value("order1"))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void getPaymentById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPaymentByTransactionId_ShouldReturnPayment() throws Exception {
        // Given
        Payment payment = createTestPayment("order1", PaymentStatus.PAID);
        payment.setTransactionId("txn_123456");
        payment = paymentRepository.save(payment);

        // When & Then
        mockMvc.perform(get("/api/payments/transaction/txn_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("txn_123456"))
                .andExpect(jsonPath("$.orderId").value("order1"));
    }

    @Test
    void getPaymentsByOrderId_ShouldReturnOrderPayments() throws Exception {
        // Given
        Payment payment1 = createTestPayment("order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("order1", PaymentStatus.REFUNDED);
        Payment payment3 = createTestPayment("order2", PaymentStatus.PAID);
        
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        // When & Then
        mockMvc.perform(get("/api/payments/order/order1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value("order1"))
                .andExpect(jsonPath("$[1].orderId").value("order1"));
    }

    @Test
    void getPaymentsByUserId_ShouldReturnUserPayments() throws Exception {
        // Given
        Payment payment1 = createTestPayment("order1", PaymentStatus.PAID);
        payment1.setUserId("user1");
        Payment payment2 = createTestPayment("order2", PaymentStatus.PENDING);
        payment2.setUserId("user1");
        Payment payment3 = createTestPayment("order3", PaymentStatus.PAID);
        payment3.setUserId("user2");
        
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        // When & Then
        mockMvc.perform(get("/api/payments/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].userId").value("user1"));
    }

    @Test
    void getPaymentsByStatus_ShouldReturnPaymentsByStatus() throws Exception {
        // Given
        Payment payment1 = createTestPayment("order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("order2", PaymentStatus.PAID);
        Payment payment3 = createTestPayment("order3", PaymentStatus.PENDING);
        
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        // When & Then
        mockMvc.perform(get("/api/payments/status/PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(jsonPath("$[1].status").value("PAID"));
    }

    @Test
    void updatePayment_ShouldUpdateInDatabase() throws Exception {
        // Given
        Payment payment = createTestPayment("order1", PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);
        
        payment.setStatus(PaymentStatus.PAID);
        payment.setAmount(new BigDecimal("150.00"));

        // When
        mockMvc.perform(put("/api/payments/{id}", payment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.amount").value(150.00));

        // Then
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals(PaymentStatus.PAID, updatedPayment.getStatus());
        assertEquals(new BigDecimal("150.00"), updatedPayment.getAmount());
        assertNotNull(updatedPayment.getUpdatedAt());
    }

    @Test
    void deletePayment_ShouldRemoveFromDatabase() throws Exception {
        // Given
        Payment payment = createTestPayment("order1", PaymentStatus.PAID);
        payment = paymentRepository.save(payment);
        assertEquals(1, paymentRepository.count());

        // When
        mockMvc.perform(delete("/api/payments/{id}", payment.getId()))
                .andExpect(status().isNoContent());

        // Then
        assertEquals(0, paymentRepository.count());
        assertFalse(paymentRepository.findById(payment.getId()).isPresent());
    }

    @Test
    void updatePaymentStatus_ShouldUpdateStatus() throws Exception {
        // Given
        Payment payment = createTestPayment("order1", PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        // When
        mockMvc.perform(put("/api/payments/{id}/status", payment.getId())
                .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        // Then
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals(PaymentStatus.PAID, updatedPayment.getStatus());
        assertNotNull(updatedPayment.getUpdatedAt());
    }

    @Test
    void processRefund_WithValidPayment_ShouldUpdateStatus() throws Exception {
        // Given
        Payment payment = createTestPayment("order1", PaymentStatus.PAID);
        payment.setAmount(new BigDecimal("100.00"));
        payment = paymentRepository.save(payment);

        // When
        mockMvc.perform(post("/api/payments/{id}/refund", payment.getId())
                .param("refundAmount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PARTIALLY_REFUNDED"))
                .andExpect(jsonPath("$.refundAmount").value(50.0));

        // Then
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals(PaymentStatus.PARTIALLY_REFUNDED, updatedPayment.getStatus());
        assertEquals(new BigDecimal("50.0"), updatedPayment.getRefundAmount());
    }

    @Test
    void processRefund_WithFullRefund_ShouldSetToRefunded() throws Exception {
        // Given
        Payment payment = createTestPayment("order1", PaymentStatus.PAID);
        payment.setAmount(new BigDecimal("100.00"));
        payment = paymentRepository.save(payment);

        // When
        mockMvc.perform(post("/api/payments/{id}/refund", payment.getId())
                .param("refundAmount", "100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        // Then
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals(PaymentStatus.REFUNDED, updatedPayment.getStatus());
    }

    @Test
    void getAllPayments_WithPagination_ShouldReturnPage() throws Exception {
        // Given
        for (int i = 0; i < 15; i++) {
            Payment payment = createTestPayment("order" + i, PaymentStatus.PAID);
            paymentRepository.save(payment);
        }

        // When & Then
        mockMvc.perform(get("/api/payments")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getTotalRevenue_ShouldReturnCorrectRevenue() throws Exception {
        // Given
        Payment payment1 = createTestPayment("order1", PaymentStatus.PAID);
        payment1.setAmount(new BigDecimal("100.00"));
        payment1.setCreatedAt(LocalDateTime.now().minusDays(1));
        paymentRepository.save(payment1);
        
        Payment payment2 = createTestPayment("order2", PaymentStatus.PAID);
        payment2.setAmount(new BigDecimal("200.00"));
        payment2.setCreatedAt(LocalDateTime.now().minusDays(2));
        paymentRepository.save(payment2);
        
        Payment payment3 = createTestPayment("order3", PaymentStatus.PENDING);
        payment3.setAmount(new BigDecimal("300.00"));
        payment3.setCreatedAt(LocalDateTime.now().minusDays(3));
        paymentRepository.save(payment3);

        // When & Then
        mockMvc.perform(get("/api/payments/stats/revenue")
                .param("startDate", LocalDateTime.now().minusDays(5).toString())
                .param("endDate", LocalDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(300.0)); // Only PAID payments
    }

    private Payment createTestPayment(String orderId, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId("user1");
        payment.setAmount(new BigDecimal("100.00"));
        payment.setStatus(status);
        payment.setMethod("CREDIT_CARD");
        payment.setTransactionId("txn_" + orderId);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }
}
