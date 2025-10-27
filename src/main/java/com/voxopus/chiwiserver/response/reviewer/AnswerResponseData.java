package com.voxopus.chiwiserver.response.reviewer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerResponseData {

    Long id;
    String answer;
    
}
