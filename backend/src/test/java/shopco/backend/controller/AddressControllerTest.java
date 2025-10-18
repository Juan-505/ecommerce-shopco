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
import shopco.backend.entity.Address;
import shopco.backend.service.AddressService;

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
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AddressController()).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllAddresses_ShouldReturnPageOfAddresses() throws Exception {
        // Given
        Address address1 = createTestAddress("1", "user1", "123 Main St");
        Address address2 = createTestAddress("2", "user2", "456 Oak Ave");
        List<Address> addresses = Arrays.asList(address1, address2);
        Page<Address> page = new PageImpl<>(addresses);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(addressService.getAllAddresses(any(PageRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/addresses")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"));

        verify(addressService).getAllAddresses(any(PageRequest.class));
    }

    @Test
    void getAllAddressesList_ShouldReturnListOfAddresses() throws Exception {
        // Given
        Address address1 = createTestAddress("1", "user1", "123 Main St");
        Address address2 = createTestAddress("2", "user2", "456 Oak Ave");
        List<Address> addresses = Arrays.asList(address1, address2);

        when(addressService.getAllAddresses()).thenReturn(addresses);

        // When & Then
        mockMvc.perform(get("/api/addresses/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));

        verify(addressService).getAllAddresses();
    }

    @Test
    void getAddressById_WhenExists_ShouldReturnAddress() throws Exception {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        when(addressService.getAddressById("1")).thenReturn(Optional.of(address));

        // When & Then
        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.street").value("123 Main St"));

        verify(addressService).getAddressById("1");
    }

    @Test
    void getAddressById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(addressService.getAddressById("1")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isNotFound());

        verify(addressService).getAddressById("1");
    }

    @Test
    void getAddressesByUserId_ShouldReturnUserAddresses() throws Exception {
        // Given
        Address address1 = createTestAddress("1", "user1", "123 Main St");
        Address address2 = createTestAddress("2", "user1", "456 Oak Ave");
        List<Address> addresses = Arrays.asList(address1, address2);

        when(addressService.getAddressesByUserId("user1")).thenReturn(addresses);

        // When & Then
        mockMvc.perform(get("/api/addresses/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].userId").value("user1"));

        verify(addressService).getAddressesByUserId("user1");
    }

    @Test
    void getDefaultAddressesByUserId_ShouldReturnDefaultAddresses() throws Exception {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        address.setIsDefault(true);
        List<Address> addresses = Arrays.asList(address);

        when(addressService.getDefaultAddressesByUserId("user1")).thenReturn(addresses);

        // When & Then
        mockMvc.perform(get("/api/addresses/user/user1/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isDefault").value(true));

        verify(addressService).getDefaultAddressesByUserId("user1");
    }

    @Test
    void createAddress_ShouldReturnCreatedAddress() throws Exception {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        when(addressService.createAddress(any(Address.class))).thenReturn(address);

        // When & Then
        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.street").value("123 Main St"));

        verify(addressService).createAddress(any(Address.class));
    }

    @Test
    void updateAddress_ShouldReturnUpdatedAddress() throws Exception {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        when(addressService.updateAddress(any(Address.class))).thenReturn(address);

        // When & Then
        mockMvc.perform(put("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.street").value("123 Main St"));

        verify(addressService).updateAddress(any(Address.class));
    }

    @Test
    void deleteAddress_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(addressService).deleteAddress("1");

        // When & Then
        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isNoContent());

        verify(addressService).deleteAddress("1");
    }

    @Test
    void setAsDefault_ShouldReturnUpdatedAddress() throws Exception {
        // Given
        Address address = createTestAddress("1", "user1", "123 Main St");
        address.setIsDefault(true);
        when(addressService.setAsDefault("1")).thenReturn(address);

        // When & Then
        mockMvc.perform(post("/api/addresses/1/set-default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));

        verify(addressService).setAsDefault("1");
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
