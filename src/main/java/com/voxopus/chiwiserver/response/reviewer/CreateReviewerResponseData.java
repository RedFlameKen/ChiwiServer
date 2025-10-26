package com.voxopus.chiwiserver.response.reviewer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateReviewerResponseData {

    Long reviewer_id;
    String reviewer_name;
    Long user_id;
    
}
