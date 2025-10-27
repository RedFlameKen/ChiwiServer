package com.voxopus.chiwiserver.response.reviewer;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashcardResponseData {

    Long flashcard_id;
    String question;
    Long reviewer_id;
    String flashcard_type;
    Date date_created;
    Date date_modified;
    List<AnswerResponseData> answers;

}
