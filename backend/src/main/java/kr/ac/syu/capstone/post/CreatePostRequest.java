package kr.ac.syu.capstone.post;

import kr.ac.syu.capstone.domain.PostType;
import java.time.OffsetDateTime;

/** Author, state and derived counts are deliberately absent from the writable DTO. */
public record CreatePostRequest(PostType type, String title, String content, Integer capacity,
        OffsetDateTime referenceTime, String communicationUrl, String departure, String destination,
        String restaurantName, String pickupLocation, String orderGuide) {}
