package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Address;
import shopco.backend.repository.AddressRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AddressService {
    
    @Autowired
    private AddressRepository addressRepository;
    
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }
    
    public Page<Address> getAllAddresses(Pageable pageable) {
        return addressRepository.findAll(pageable);
    }
    
    public Optional<Address> getAddressById(String id) {
        return addressRepository.findById(id);
    }
    
    public List<Address> getAddressesByUserId(String userId) {
        return addressRepository.findByUserId(userId);
    }
    
    public List<Address> getDefaultAddressesByUserId(String userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId);
    }
    
    public Address createAddress(Address address) {
        // If this is set as default, unset other default addresses for this user
        if (address.getIsDefault()) {
            List<Address> userAddresses = addressRepository.findByUserId(address.getUserId());
            userAddresses.forEach(addr -> {
                if (addr.getIsDefault()) {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                }
            });
        }
        
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        return addressRepository.save(address);
    }
    
    public Address updateAddress(Address address) {
        // If this is set as default, unset other default addresses for this user
        if (address.getIsDefault()) {
            List<Address> userAddresses = addressRepository.findByUserId(address.getUserId());
            userAddresses.forEach(addr -> {
                if (addr.getIsDefault() && !addr.getId().equals(address.getId())) {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                }
            });
        }
        
        address.setUpdatedAt(LocalDateTime.now());
        return addressRepository.save(address);
    }
    
    public void deleteAddress(String id) {
        addressRepository.deleteById(id);
    }
    
    public Address setAsDefault(String id) {
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Unset other default addresses for this user
        List<Address> userAddresses = addressRepository.findByUserId(address.getUserId());
        userAddresses.forEach(addr -> {
            if (addr.getIsDefault() && !addr.getId().equals(id)) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        });
        
        address.setIsDefault(true);
        address.setUpdatedAt(LocalDateTime.now());
        return addressRepository.save(address);
    }
}
