package shopco.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shopco.backend.infrastructure.model.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {

}
