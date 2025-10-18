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
import shopco.backend.entity.Address;
import shopco.backend.repository.AddressRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AddressIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
    }

    @Test
    void createAddress_ShouldSaveToDatabase() throws Exception {
        // Given
        Address address = createTestAddress("user1", "123 Main St");

        // When
        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.street").value("123 Main St"));

        // Then
        assertEquals(1, addressRepository.count());
        Address savedAddress = addressRepository.findAll().get(0);
        assertEquals("user1", savedAddress.getUserId());
        assertEquals("123 Main St", savedAddress.getStreet());
        assertNotNull(savedAddress.getCreatedAt());
        assertNotNull(savedAddress.getUpdatedAt());
    }

    @Test
    void getAddressById_ShouldReturnAddress() throws Exception {
        // Given
        Address address = createTestAddress("user1", "123 Main St");
        address = addressRepository.save(address);

        // When & Then
        mockMvc.perform(get("/api/addresses/{id}", address.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(address.getId()))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.street").value("123 Main St"));
    }

    @Test
    void getAddressById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/addresses/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAddressesByUserId_ShouldReturnUserAddresses() throws Exception {
        // Given
        Address address1 = createTestAddress("user1", "123 Main St");
        Address address2 = createTestAddress("user1", "456 Oak Ave");
        Address address3 = createTestAddress("user2", "789 Pine St");
        
        addressRepository.save(address1);
        addressRepository.save(address2);
        addressRepository.save(address3);

        // When & Then
        mockMvc.perform(get("/api/addresses/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].userId").value("user1"));
    }

    @Test
    void updateAddress_ShouldUpdateInDatabase() throws Exception {
        // Given
        Address address = createTestAddress("user1", "123 Main St");
        address = addressRepository.save(address);
        
        address.setStreet("456 Updated St");
        address.setCity("Updated City");

        // When
        mockMvc.perform(put("/api/addresses/{id}", address.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("456 Updated St"))
                .andExpect(jsonPath("$.city").value("Updated City"));

        // Then
        Address updatedAddress = addressRepository.findById(address.getId()).orElseThrow();
        assertEquals("456 Updated St", updatedAddress.getStreet());
        assertEquals("Updated City", updatedAddress.getCity());
        assertNotNull(updatedAddress.getUpdatedAt());
    }

    @Test
    void deleteAddress_ShouldRemoveFromDatabase() throws Exception {
        // Given
        Address address = createTestAddress("user1", "123 Main St");
        address = addressRepository.save(address);
        assertEquals(1, addressRepository.count());

        // When
        mockMvc.perform(delete("/api/addresses/{id}", address.getId()))
                .andExpect(status().isNoContent());

        // Then
        assertEquals(0, addressRepository.count());
        assertFalse(addressRepository.findById(address.getId()).isPresent());
    }

    @Test
    void setAsDefault_ShouldSetOnlyOneDefaultPerUser() throws Exception {
        // Given
        Address address1 = createTestAddress("user1", "123 Main St");
        address1.setIsDefault(true);
        address1 = addressRepository.save(address1);
        
        Address address2 = createTestAddress("user1", "456 Oak Ave");
        address2 = addressRepository.save(address2);

        // When
        mockMvc.perform(post("/api/addresses/{id}/set-default", address2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));

        // Then
        Address updatedAddress1 = addressRepository.findById(address1.getId()).orElseThrow();
        Address updatedAddress2 = addressRepository.findById(address2.getId()).orElseThrow();
        
        assertFalse(updatedAddress1.getIsDefault());
        assertTrue(updatedAddress2.getIsDefault());
    }

    @Test
    void getAllAddresses_WithPagination_ShouldReturnPage() throws Exception {
        // Given
        for (int i = 0; i < 15; i++) {
            Address address = createTestAddress("user" + i, "Street " + i);
            addressRepository.save(address);
        }

        // When & Then
        mockMvc.perform(get("/api/addresses")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getDefaultAddressesByUserId_ShouldReturnOnlyDefaultAddresses() throws Exception {
        // Given
        Address address1 = createTestAddress("user1", "123 Main St");
        address1.setIsDefault(true);
        addressRepository.save(address1);
        
        Address address2 = createTestAddress("user1", "456 Oak Ave");
        address2.setIsDefault(false);
        addressRepository.save(address2);

        // When & Then
        mockMvc.perform(get("/api/addresses/user/user1/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isDefault").value(true))
                .andExpect(jsonPath("$[0].street").value("123 Main St"));
    }

    private Address createTestAddress(String userId, String street) {
        Address address = new Address();
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
