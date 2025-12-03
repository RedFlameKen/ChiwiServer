package com.voxopus.chiwiserver.response.review_session;

import java.util.List;

import com.voxopus.chiwiserver.enums.AnswerState;
import com.voxopus.chiwiserver.response.reviewer.AnswerResponseData;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashcardResultResponseData {

    String question;
    String submitted_answer;
    List<AnswerResponseData> answers;
    AnswerState answer_state;
    
}
