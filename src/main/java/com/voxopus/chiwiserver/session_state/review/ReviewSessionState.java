package com.voxopus.chiwiserver.session_state.review;

import static com.voxopus.chiwiserver.enums.StateStatus.CONTINUE;
import static com.voxopus.chiwiserver.enums.StateStatus.FINISHED;
import static com.voxopus.chiwiserver.enums.StateStatus.MISUNDERSTOOD;

import com.voxopus.chiwiserver.enums.AnswerState;
import com.voxopus.chiwiserver.enums.QuizState;
import com.voxopus.chiwiserver.enums.StateStatus;
import com.voxopus.chiwiserver.model.review_session.FlashcardQueueItem;
import com.voxopus.chiwiserver.model.review_session.QuizSession;
import com.voxopus.chiwiserver.session_state.SessionState;
import com.voxopus.chiwiserver.session_state.SessionStateHelper;
import com.voxopus.chiwiserver.session_state.StateResult;
import com.voxopus.chiwiserver.util.StringHelper;

public class ReviewSessionState extends SessionState<QuizSession>{

    public ReviewSessionState(QuizSession session) {
        super(session);
    }

    @Override
    public StateResult handleStates(String input) {
        switch (session.getState()) {
            case ASK_QUESTION:
                return askQuestion();
            case LISTEN_FOR_ANSWER:
                return listenForAnswer(input);
            case CONFIRM_ANSWER:
                return confirmAnswer(input);
            default:
                return new StateResult("misunderstood", MISUNDERSTOOD);
        }
    }

    private StateResult askQuestion(){
        Long index = session.getReviewSession().getCurrentFlashcard();
        FlashcardQueueItem curFlashcard = session.getReviewSession().getFlashcardQueueItems().get(index.intValue());
        session.setState(QuizState.LISTEN_FOR_ANSWER);
        return new StateResult(curFlashcard.getFlashcard().getQuestion(), StateStatus.CONTINUE);
    }

    private StateResult listenForAnswer(String input){
        session.setState(QuizState.CONFIRM_ANSWER);
        if(input.equals(""))
            return new StateResult(null, MISUNDERSTOOD);
        session.setAnswer(input);
        return new StateResult("is " + input + " correct?", CONTINUE);
    }

    private StateResult confirmAnswer(String confirm){
        String normalized = StringHelper.normalize(confirm);
        Long index = session.getReviewSession().getCurrentFlashcard();
        FlashcardQueueItem curFlashcard = session.getReviewSession().getFlashcardQueueItems().get(index.intValue());
        switch (SessionStateHelper.checkConfirm(normalized)) {
            case YES:
                evaluateAnswer(curFlashcard);
                return new StateResult(null, FINISHED);
            case NO:
                session.setState(QuizState.LISTEN_FOR_ANSWER);
                return new StateResult(curFlashcard.getFlashcard().getQuestion(), CONTINUE);
            case MISUNDERSTOOD:
            default:
                return new StateResult(null, MISUNDERSTOOD);
        }
    }

    private void evaluateAnswer(FlashcardQueueItem curFlashcard){
        String answer = curFlashcard.getFlashcard().getAnswers().get(0).getAnswer().toLowerCase();

        if(session.getAnswer().toLowerCase().equals(answer))
            curFlashcard.setAnswerState(AnswerState.CORRECT);
        else
            curFlashcard.setAnswerState(AnswerState.WRONG);
    }

}
