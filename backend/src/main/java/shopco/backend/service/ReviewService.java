package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Review;
import shopco.backend.enums.ReviewStatus;
import shopco.backend.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    public Page<Review> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }
    
    public Optional<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }
    
    public List<Review> getReviewsByProductId(String productId) {
        return reviewRepository.findByProductId(productId);
    }
    
    public Page<Review> getReviewsByProductId(String productId, Pageable pageable) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }
    
    public List<Review> getReviewsByUserId(String userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    public List<Review> getReviewsByStatus(ReviewStatus status) {
        return reviewRepository.findByStatus(status);
    }
    
    public List<Review> getApprovedReviewsByProductId(String productId) {
        return reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED);
    }
    
    public Double getAverageRatingByProductId(String productId) {
        Double average = reviewRepository.findAverageRatingByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        return average != null ? average : 0.0;
    }
    
    public Long getReviewCountByProductId(String productId) {
        return reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED);
    }
    
    public Review createReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        review.setStatus(ReviewStatus.PENDING);
        return reviewRepository.save(review);
    }
    
    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }
    
    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }
    
    public Review approveReview(String id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setStatus(ReviewStatus.APPROVED);
        return reviewRepository.save(review);
    }
    
    public Review rejectReview(String id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setStatus(ReviewStatus.REJECTED);
        return reviewRepository.save(review);
    }
    
    public Long countReviewsByStatus(ReviewStatus status) {
        return reviewRepository.countByStatus(status);
    }
}
