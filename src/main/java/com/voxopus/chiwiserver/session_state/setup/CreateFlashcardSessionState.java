package com.voxopus.chiwiserver.session_state.setup;

import static com.voxopus.chiwiserver.enums.CreateFlashcardState.CONFIRM_ANSWER;
import static com.voxopus.chiwiserver.enums.CreateFlashcardState.CONFIRM_QUESTION;
import static com.voxopus.chiwiserver.enums.CreateFlashcardState.LISTEN_FOR_ANSWER;
import static com.voxopus.chiwiserver.enums.CreateFlashcardState.LISTEN_FOR_QUESTION;
import static com.voxopus.chiwiserver.enums.StateStatus.CONTINUE;
import static com.voxopus.chiwiserver.enums.StateStatus.FINISHED;
import static com.voxopus.chiwiserver.enums.StateStatus.MISUNDERSTOOD;

import com.voxopus.chiwiserver.model.setup_session.CreateFlashcardSession;
import com.voxopus.chiwiserver.session_state.SessionState;
import com.voxopus.chiwiserver.session_state.SessionStateHelper;
import com.voxopus.chiwiserver.session_state.StateResult;
import com.voxopus.chiwiserver.util.StringHelper;

public class CreateFlashcardSessionState extends SessionState<CreateFlashcardSession>{

    public CreateFlashcardSessionState(CreateFlashcardSession session){
        super(session);
    }

    @Override
    public StateResult handleStates(String input) {
        switch (session.getState()) {
            case INIT:
                session.setState(LISTEN_FOR_QUESTION);
                return new StateResult("What's the question?", CONTINUE);
            case LISTEN_FOR_QUESTION:
                return listenQuestion(input);
            case CONFIRM_QUESTION:
                return confirmQuestion(input);
            case LISTEN_FOR_ANSWER:
                return listenAnswer(input);
            case CONFIRM_ANSWER:
                return confirmAnswer(input);
            default:
                return new StateResult(null, MISUNDERSTOOD);
        }
    }

    public StateResult listenQuestion(String question){
        session.setState(CONFIRM_QUESTION);
        question = StringHelper.removeMinorNoise(question);
        session.setQuestion(question);
        return new StateResult("is \"" + question + "\" correct?", CONTINUE);
    }

    public StateResult confirmQuestion(String confirm){
        String normalized = StringHelper.normalize(confirm);
        switch (SessionStateHelper.checkConfirm(normalized)) {
            case YES:
                session.setState(LISTEN_FOR_ANSWER);
                return new StateResult("What's the answer?", CONTINUE);
            case NO:
                session.setState(LISTEN_FOR_QUESTION);
                session.setQuestion(null);
                return new StateResult("What's the question?", CONTINUE);
            case MISUNDERSTOOD:
            default:
                return new StateResult(null, MISUNDERSTOOD);
        }
    }

    public StateResult listenAnswer(String answer){
        session.setState(CONFIRM_ANSWER);
        answer = StringHelper.removeMinorNoise(answer);
        session.setAnswer(answer);
        return new StateResult("is \"" + answer + "\" correct?", CONTINUE);
    }

    public StateResult confirmAnswer(String confirm){
        String normalized = StringHelper.normalize(confirm);
        switch (SessionStateHelper.checkConfirm(normalized)) {
            case YES:
                return new StateResult("Flashcard Created!", FINISHED);
            case NO:
                session.setState(LISTEN_FOR_ANSWER);
                return new StateResult("What's the answer?", CONTINUE);
            case MISUNDERSTOOD:
            default:
                return new StateResult(null, MISUNDERSTOOD);
        }
    }
    
}
