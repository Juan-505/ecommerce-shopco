package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.User;
import shopco.backend.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsersList() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/active/role/{role}")
    public ResponseEntity<List<User>> getActiveUsersByRole(@PathVariable String role) {
        List<User> users = userService.getActiveUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/banned")
    public ResponseEntity<List<User>> getBannedUsers() {
        List<User> users = userService.getBannedUsers();
        return ResponseEntity.ok(users);
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/ban")
    public ResponseEntity<User> banUser(@PathVariable String id, 
                                      @RequestParam String reason,
                                      @RequestParam(required = false) LocalDateTime banExpires) {
        User bannedUser = userService.banUser(id, reason, banExpires);
        return ResponseEntity.ok(bannedUser);
    }
    
    @PostMapping("/{id}/unban")
    public ResponseEntity<User> unbanUser(@PathVariable String id) {
        User unbannedUser = userService.unbanUser(id);
        return ResponseEntity.ok(unbannedUser);
    }
    
    @PutMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable String id, @RequestParam String role) {
        User updatedUser = userService.updateUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/stats/role/{role}")
    public ResponseEntity<Long> countUsersByRole(@PathVariable String role) {
        Long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/banned")
    public ResponseEntity<Long> countBannedUsers() {
        Long count = userService.countBannedUsers();
        return ResponseEntity.ok(count);
    }
}
