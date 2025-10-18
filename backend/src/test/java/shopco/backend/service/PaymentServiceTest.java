package shopco.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import shopco.backend.entity.Payment;
import shopco.backend.enums.PaymentStatus;
import shopco.backend.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
        // Use reflection to inject the mock repository
        try {
            var field = PaymentService.class.getDeclaredField("paymentRepository");
            field.setAccessible(true);
            field.set(paymentService, paymentRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock repository", e);
        }
    }

    @Test
    void getAllPayments_ShouldReturnAllPayments() {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PENDING);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentRepository.findAll()).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getAllPayments();

        // Then
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());
        verify(paymentRepository).findAll();
    }

    @Test
    void getAllPayments_WithPageable_ShouldReturnPageOfPayments() {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PENDING);
        List<Payment> payments = Arrays.asList(payment1, payment2);
        Page<Payment> page = new PageImpl<>(payments);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(paymentRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        Page<Payment> result = paymentService.getAllPayments(pageRequest);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals("1", result.getContent().get(0).getId());
        assertEquals("2", result.getContent().get(1).getId());
        verify(paymentRepository).findAll(pageRequest);
    }

    @Test
    void getPaymentById_WhenExists_ShouldReturnPayment() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        when(paymentRepository.findById("1")).thenReturn(Optional.of(payment));

        // When
        Optional<Payment> result = paymentService.getPaymentById("1");

        // Then
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
        assertEquals("order1", result.get().getOrderId());
        verify(paymentRepository).findById("1");
    }

    @Test
    void getPaymentById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(paymentRepository.findById("1")).thenReturn(Optional.empty());

        // When
        Optional<Payment> result = paymentService.getPaymentById("1");

        // Then
        assertFalse(result.isPresent());
        verify(paymentRepository).findById("1");
    }

    @Test
    void getPaymentByTransactionId_ShouldReturnPayment() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        payment.setTransactionId("txn_123");
        when(paymentRepository.findByTransactionId("txn_123")).thenReturn(Optional.of(payment));

        // When
        Optional<Payment> result = paymentService.getPaymentByTransactionId("txn_123");

        // Then
        assertTrue(result.isPresent());
        assertEquals("txn_123", result.get().getTransactionId());
        verify(paymentRepository).findByTransactionId("txn_123");
    }

    @Test
    void getPaymentsByOrderId_ShouldReturnOrderPayments() {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order1", PaymentStatus.REFUNDED);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentRepository.findByOrderId("order1")).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByOrderId("order1");

        // Then
        assertEquals(2, result.size());
        assertEquals("order1", result.get(0).getOrderId());
        assertEquals("order1", result.get(1).getOrderId());
        verify(paymentRepository).findByOrderId("order1");
    }

    @Test
    void getPaymentsByUserId_ShouldReturnUserPayments() {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PENDING);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentRepository.findByUserId("user1")).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByUserId("user1");

        // Then
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUserId());
        assertEquals("user1", result.get(1).getUserId());
        verify(paymentRepository).findByUserId("user1");
    }

    @Test
    void getPaymentsByStatus_ShouldReturnPaymentsByStatus() {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PAID);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentRepository.findByStatus(PaymentStatus.PAID)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByStatus(PaymentStatus.PAID);

        // Then
        assertEquals(2, result.size());
        assertEquals(PaymentStatus.PAID, result.get(0).getStatus());
        assertEquals(PaymentStatus.PAID, result.get(1).getStatus());
        verify(paymentRepository).findByStatus(PaymentStatus.PAID);
    }

    @Test
    void getPaymentsByMethod_ShouldReturnPaymentsByMethod() {
        // Given
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PAID);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentRepository.findByMethod("CREDIT_CARD")).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByMethod("CREDIT_CARD");

        // Then
        assertEquals(2, result.size());
        assertEquals("CREDIT_CARD", result.get(0).getMethod());
        assertEquals("CREDIT_CARD", result.get(1).getMethod());
        verify(paymentRepository).findByMethod("CREDIT_CARD");
    }

    @Test
    void getPaymentsByDateRange_ShouldReturnPaymentsInRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Payment payment1 = createTestPayment("1", "order1", PaymentStatus.PAID);
        Payment payment2 = createTestPayment("2", "order2", PaymentStatus.PAID);
        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByDateRange(startDate, endDate);

        // Then
        assertEquals(2, result.size());
        verify(paymentRepository).findByCreatedAtBetween(startDate, endDate);
    }

    @Test
    void createPayment_ShouldSavePaymentWithTimestamps() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PENDING);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        Payment result = paymentService.createPayment(payment);

        // Then
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(paymentRepository).save(payment);
    }

    @Test
    void updatePayment_ShouldSavePaymentWithUpdatedTimestamp() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        Payment result = paymentService.updatePayment(payment);

        // Then
        assertNotNull(result.getUpdatedAt());
        verify(paymentRepository).save(payment);
    }

    @Test
    void deletePayment_ShouldCallRepositoryDelete() {
        // Given
        doNothing().when(paymentRepository).deleteById("1");

        // When
        paymentService.deletePayment("1");

        // Then
        verify(paymentRepository).deleteById("1");
    }

    @Test
    void updatePaymentStatus_ShouldUpdateStatus() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PENDING);
        when(paymentRepository.findById("1")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        Payment result = paymentService.updatePaymentStatus("1", PaymentStatus.PAID);

        // Then
        assertEquals(PaymentStatus.PAID, result.getStatus());
        assertNotNull(result.getUpdatedAt());
        verify(paymentRepository).save(payment);
    }

    @Test
    void processRefund_WithFullRefund_ShouldSetStatusToRefunded() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        payment.setAmount(new BigDecimal("100.00"));
        when(paymentRepository.findById("1")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        Payment result = paymentService.processRefund("1", 100.0);

        // Then
        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getRefundAmount());
        verify(paymentRepository).save(payment);
    }

    @Test
    void processRefund_WithPartialRefund_ShouldSetStatusToPartiallyRefunded() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        payment.setAmount(new BigDecimal("100.00"));
        when(paymentRepository.findById("1")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        Payment result = paymentService.processRefund("1", 50.0);

        // Then
        assertEquals(PaymentStatus.PARTIALLY_REFUNDED, result.getStatus());
        assertEquals(new BigDecimal("50.00"), result.getRefundAmount());
        verify(paymentRepository).save(payment);
    }

    @Test
    void processRefund_WithInvalidStatus_ShouldThrowException() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PENDING);
        when(paymentRepository.findById("1")).thenReturn(Optional.of(payment));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            paymentService.processRefund("1", 50.0));
    }

    @Test
    void processRefund_WithExcessiveAmount_ShouldThrowException() {
        // Given
        Payment payment = createTestPayment("1", "order1", PaymentStatus.PAID);
        payment.setAmount(new BigDecimal("100.00"));
        when(paymentRepository.findById("1")).thenReturn(Optional.of(payment));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            paymentService.processRefund("1", 150.0));
    }

    @Test
    void countPaymentsByStatus_ShouldReturnCount() {
        // Given
        when(paymentRepository.countByStatus(PaymentStatus.PAID)).thenReturn(5L);

        // When
        Long result = paymentService.countPaymentsByStatus(PaymentStatus.PAID);

        // Then
        assertEquals(5L, result);
        verify(paymentRepository).countByStatus(PaymentStatus.PAID);
    }

    @Test
    void getTotalRevenue_ShouldReturnRevenue() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(paymentRepository.sumAmountByStatusAndDateRange(startDate, endDate)).thenReturn(1000.0);

        // When
        Double result = paymentService.getTotalRevenue(startDate, endDate);

        // Then
        assertEquals(1000.0, result);
        verify(paymentRepository).sumAmountByStatusAndDateRange(startDate, endDate);
    }

    @Test
    void getTotalRevenue_WhenNull_ShouldReturnZero() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(paymentRepository.sumAmountByStatusAndDateRange(startDate, endDate)).thenReturn(null);

        // When
        Double result = paymentService.getTotalRevenue(startDate, endDate);

        // Then
        assertEquals(0.0, result);
        verify(paymentRepository).sumAmountByStatusAndDateRange(startDate, endDate);
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
