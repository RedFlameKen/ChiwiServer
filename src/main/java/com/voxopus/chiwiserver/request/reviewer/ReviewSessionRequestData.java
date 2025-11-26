package com.voxopus.chiwiserver.request.reviewer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewSessionRequestData {

    Long reviewer_id;
    
}
