package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.Review;
import shopco.backend.enums.ReviewStatus;
import shopco.backend.service.ReviewService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping
    public ResponseEntity<Page<Review>> getAllReviews(Pageable pageable) {
        Page<Review> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviewsList() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable String id) {
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable String productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/product/{productId}/paged")
    public ResponseEntity<Page<Review>> getReviewsByProductIdPaged(@PathVariable String productId, Pageable pageable) {
        Page<Review> reviews = reviewService.getReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/product/{productId}/approved")
    public ResponseEntity<List<Review>> getApprovedReviewsByProductId(@PathVariable String productId) {
        List<Review> reviews = reviewService.getApprovedReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUserId(@PathVariable String userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Review>> getReviewsByStatus(@PathVariable ReviewStatus status) {
        List<Review> reviews = reviewService.getReviewsByStatus(status);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByProductId(@PathVariable String productId) {
        Double averageRating = reviewService.getAverageRatingByProductId(productId);
        return ResponseEntity.ok(averageRating);
    }
    
    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> getReviewCountByProductId(@PathVariable String productId) {
        Long count = reviewService.getReviewCountByProductId(productId);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable String id, @RequestBody Review review) {
        review.setId(id);
        Review updatedReview = reviewService.updateReview(review);
        return ResponseEntity.ok(updatedReview);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable String id) {
        Review review = reviewService.approveReview(id);
        return ResponseEntity.ok(review);
    }
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<Review> rejectReview(@PathVariable String id) {
        Review review = reviewService.rejectReview(id);
        return ResponseEntity.ok(review);
    }
    
    @GetMapping("/stats/count-by-status")
    public ResponseEntity<Long> countReviewsByStatus(@RequestParam ReviewStatus status) {
        Long count = reviewService.countReviewsByStatus(status);
        return ResponseEntity.ok(count);
    }
}
