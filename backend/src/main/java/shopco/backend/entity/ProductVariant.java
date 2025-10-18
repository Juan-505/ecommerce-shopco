package shopco.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_variant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    
    @Id
    private String id;
    
    @Column(name = "product_id", nullable = false)
    private String productId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    @Column(unique = true)
    private String sku;
    
    @Column(columnDefinition = "JSON")
    private String attributes; // JSON string for variant attributes
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;
    
    private Float weight;
    private String barcode;
    
    // Relationships
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images;
}
