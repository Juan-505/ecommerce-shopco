package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Cart;
import shopco.backend.entity.CartItem;
import shopco.backend.repository.CartRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }
    
    public Optional<Cart> getCartById(String id) {
        return cartRepository.findById(id);
    }
    
    public Optional<Cart> getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }
    
    public Cart createCart(Cart cart) {
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    public Cart updateCart(Cart cart) {
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    public void deleteCart(String id) {
        cartRepository.deleteById(id);
    }
    
    public void deleteCartByUserId(String userId) {
        cartRepository.deleteByUserId(userId);
    }
    
    public Cart addItemToCart(String userId, CartItem cartItem) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                newCart.setCreatedAt(LocalDateTime.now());
                newCart.setUpdatedAt(LocalDateTime.now());
                return newCart;
            });
        
        // Check if item already exists in cart
        boolean itemExists = cart.getItems().stream()
            .anyMatch(item -> item.getProductId().equals(cartItem.getProductId()));
        
        if (itemExists) {
            // Update quantity of existing item
            cart.getItems().stream()
                .filter(item -> item.getProductId().equals(cartItem.getProductId()))
                .findFirst()
                .ifPresent(item -> item.setQuantity(item.getQuantity() + cartItem.getQuantity()));
        } else {
            // Add new item to cart
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
        }
        
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    public Cart removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.setUpdatedAt(LocalDateTime.now());
        
        return cartRepository.save(cart);
    }
    
    public Cart updateItemQuantity(String userId, String productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.getItems().stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .ifPresent(item -> {
                if (quantity <= 0) {
                    cart.getItems().remove(item);
                } else {
                    item.setQuantity(quantity);
                }
            });
        
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    public Cart clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        
        return cartRepository.save(cart);
    }
}
