package com.voxopus.chiwiserver.response.reviewer;

import java.util.Calendar;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewerSetupResponseData {

    Long id;
    Long reviewer_id;
    Calendar date_started;
    Calendar date_used;
    
}
