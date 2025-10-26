package com.voxopus.chiwiserver.response.reviewer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListReviewersResponseData {

    Long id;
    String name;
    int flashcards_count;
    
}
