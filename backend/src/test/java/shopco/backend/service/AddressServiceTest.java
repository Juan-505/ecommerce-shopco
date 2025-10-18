package shopco.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import shopco.backend.entity.Address;
import shopco.backend.repository.AddressRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    private AddressService addressService;

    @BeforeEach
    void setUp() {
        addressService = new AddressService();
        // Use reflection to inject the mock repository
        try {
            var field = AddressService.class.getDeclaredField("addressRepository");
            field.setAccessible(true);
            field.set(addressService, addressRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock repository", e);
        }
    }

    @Test
    void getAllAddresses_ShouldReturnAllAddresses() {
        // Given
        Address address1 = createTestAddress("1", "user1", "123 Main St");
        Address address2 = createTestAddress("2", "user2", "456 Oak Ave");
        List<Address> addresses = Arrays.asList(address1, address2);

        when(addressRepository.findAll()).thenReturn(addresses);

        // When
        List<Address> result = addressService.getAllAddresses();

        // Then
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());
        verify(addressRepository).findAll();
    }

    @Test
    void getAllAddresses_WithPageable_ShouldReturnPageOfAddresses() {
        // Given
        Address address1 = createTestAddress("1", "user1", "123 Main St");
        Address address2 = createTestAddress("2", "user2", "456 Oak Ave");
        List<Address> addresses = Arrays.asList(address1, address2);
        Page<Address> page = new PageImpl<>(addresses);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(addressRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        Page<Address> result = addressService.getAllAddresses(pageRequest);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals("1", result.getContent().get(0).getId());
        assertEquals("2", result.getContent().get(1).getId());
        verify(addressRepository).findAll(pageRequest);
    }

    @Test
    void getAddressById_WhenExists_ShouldReturnAddress() {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        when(addressRepository.findById("1")).thenReturn(Optional.of(address));

        // When
        Optional<Address> result = addressService.getAddressById("1");

        // Then
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
        assertEquals("user1", result.get().getUserId());
        verify(addressRepository).findById("1");
    }

    @Test
    void getAddressById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(addressRepository.findById("1")).thenReturn(Optional.empty());

        // When
        Optional<Address> result = addressService.getAddressById("1");

        // Then
        assertFalse(result.isPresent());
        verify(addressRepository).findById("1");
    }

    @Test
    void getAddressesByUserId_ShouldReturnUserAddresses() {
        // Given
        Address address1 = createTestAddress("1", "user1", "123 Main St");
        Address address2 = createTestAddress("2", "user1", "456 Oak Ave");
        List<Address> addresses = Arrays.asList(address1, address2);

        when(addressRepository.findByUserId("user1")).thenReturn(addresses);

        // When
        List<Address> result = addressService.getAddressesByUserId("user1");

        // Then
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUserId());
        assertEquals("user1", result.get(1).getUserId());
        verify(addressRepository).findByUserId("user1");
    }

    @Test
    void getDefaultAddressesByUserId_ShouldReturnDefaultAddresses() {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        address.setIsDefault(true);
        List<Address> addresses = Arrays.asList(address);

        when(addressRepository.findByUserIdAndIsDefaultTrue("user1")).thenReturn(addresses);

        // When
        List<Address> result = addressService.getDefaultAddressesByUserId("user1");

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsDefault());
        verify(addressRepository).findByUserIdAndIsDefaultTrue("user1");
    }

    @Test
    void createAddress_WhenNotDefault_ShouldSaveAddress() {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        address.setIsDefault(false);

        when(addressRepository.findByUserId("user1")).thenReturn(Arrays.asList());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // When
        Address result = addressService.createAddress(address);

        // Then
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(addressRepository).save(address);
    }

    @Test
    void createAddress_WhenDefault_ShouldUnsetOtherDefaults() {
        // Given
        Address existingAddress = createTestAddress("2", "user1", "456 Oak Ave");
        existingAddress.setIsDefault(true);
        Address newAddress = createTestAddress("1", "user1", "123 Main St");
        newAddress.setIsDefault(true);

        when(addressRepository.findByUserId("user1")).thenReturn(Arrays.asList(existingAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(newAddress);

        // When
        Address result = addressService.createAddress(newAddress);

        // Then
        assertTrue(result.getIsDefault());
        verify(addressRepository).save(existingAddress); // Should unset existing default
        verify(addressRepository).save(newAddress); // Should save new address
    }

    @Test
    void updateAddress_WhenNotDefault_ShouldSaveAddress() {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        address.setIsDefault(false);

        when(addressRepository.findByUserId("user1")).thenReturn(Arrays.asList());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // When
        Address result = addressService.updateAddress(address);

        // Then
        assertNotNull(result.getUpdatedAt());
        verify(addressRepository).save(address);
    }

    @Test
    void updateAddress_WhenDefault_ShouldUnsetOtherDefaults() {
        // Given
        Address existingAddress = createTestAddress("2", "user1", "456 Oak Ave");
        existingAddress.setIsDefault(true);
        Address updatedAddress = createTestAddress("1", "user1", "123 Main St");
        updatedAddress.setIsDefault(true);

        when(addressRepository.findByUserId("user1")).thenReturn(Arrays.asList(existingAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);

        // When
        Address result = addressService.updateAddress(updatedAddress);

        // Then
        assertTrue(result.getIsDefault());
        verify(addressRepository).save(existingAddress); // Should unset existing default
        verify(addressRepository).save(updatedAddress); // Should save updated address
    }

    @Test
    void deleteAddress_ShouldCallRepositoryDelete() {
        // Given
        doNothing().when(addressRepository).deleteById("1");

        // When
        addressService.deleteAddress("1");

        // Then
        verify(addressRepository).deleteById("1");
    }

    @Test
    void setAsDefault_ShouldSetAddressAsDefaultAndUnsetOthers() {
        // Given
        Address existingAddress = createTestAddress("2", "user1", "456 Oak Ave");
        existingAddress.setIsDefault(true);
        Address targetAddress = createTestAddress("1", "user1", "123 Main St");
        targetAddress.setIsDefault(false);

        when(addressRepository.findById("1")).thenReturn(Optional.of(targetAddress));
        when(addressRepository.findByUserId("user1")).thenReturn(Arrays.asList(existingAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(targetAddress);

        // When
        Address result = addressService.setAsDefault("1");

        // Then
        assertTrue(result.getIsDefault());
        verify(addressRepository).save(existingAddress); // Should unset existing default
        verify(addressRepository).save(targetAddress); // Should save target address
    }

    private Address createTestAddress(String id, String userId, String street) {
        Address address = new Address();
        address.setId(id);
        address.setUserId(userId);
        address.setStreet(street);
        address.setCity("Test City");
        address.setState("Test State");
        address.setZipCode("12345");
        address.setCountry("Test Country");
        address.setIsDefault(false);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        return address;
    }
}
