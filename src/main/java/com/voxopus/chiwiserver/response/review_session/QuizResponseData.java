package com.voxopus.chiwiserver.response.review_session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuizResponseData {

    String question;
    
}
