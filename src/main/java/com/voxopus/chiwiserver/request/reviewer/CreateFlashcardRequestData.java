package com.voxopus.chiwiserver.request.reviewer;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateFlashcardRequestData {

    String question;
    String flashcard_type;
    List<CreateAnswerRequestData> answers;
    
}
