package com.voxopus.chiwiserver.response.reviewer;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListReviewersResponseData {

    Long id;
    String name;
    String subject;
    Date date_created;
    Date date_modified;
    int flashcards_count;
    
}
