package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.Address;
import shopco.backend.service.AddressService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @GetMapping
    public ResponseEntity<Page<Address>> getAllAddresses(Pageable pageable) {
        Page<Address> addresses = addressService.getAllAddresses(pageable);
        return ResponseEntity.ok(addresses);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Address>> getAllAddressesList() {
        List<Address> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable String id) {
        Optional<Address> address = addressService.getAddressById(id);
        return address.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Address>> getAddressesByUserId(@PathVariable String userId) {
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }
    
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<List<Address>> getDefaultAddressesByUserId(@PathVariable String userId) {
        List<Address> addresses = addressService.getDefaultAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }
    
    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address createdAddress = addressService.createAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable String id, @RequestBody Address address) {
        address.setId(id);
        Address updatedAddress = addressService.updateAddress(address);
        return ResponseEntity.ok(updatedAddress);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/set-default")
    public ResponseEntity<Address> setAsDefault(@PathVariable String id) {
        Address address = addressService.setAsDefault(id);
        return ResponseEntity.ok(address);
    }
}
