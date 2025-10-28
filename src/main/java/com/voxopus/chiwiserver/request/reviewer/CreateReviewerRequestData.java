package com.voxopus.chiwiserver.request.reviewer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateReviewerRequestData {

    String reviewer_name;
    String subject;
    Long user_id;
    
}
