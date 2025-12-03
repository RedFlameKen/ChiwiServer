package com.voxopus.chiwiserver.response.review_session;

import com.voxopus.chiwiserver.enums.QuizState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuizResponseData {

    String question;
    Integer flashcard_count;
    QuizState state;
    Long cur_flashcard;
    
}
