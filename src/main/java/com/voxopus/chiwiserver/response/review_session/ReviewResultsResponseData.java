package com.voxopus.chiwiserver.response.review_session;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResultsResponseData {

    String message;
    Integer score;
    Integer total_items;
    List<FlashcardResultResponseData> flashcards;
}
