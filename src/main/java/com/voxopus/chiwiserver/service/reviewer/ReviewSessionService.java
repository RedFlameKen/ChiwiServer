package com.voxopus.chiwiserver.service.reviewer;

import static com.voxopus.chiwiserver.enums.ReviewCommandType.COMPLETE;
import static com.voxopus.chiwiserver.enums.ReviewCommandType.FINISH;
import static com.voxopus.chiwiserver.enums.ReviewCommandType.INIT;
import static com.voxopus.chiwiserver.enums.ReviewCommandType.MISUNDERSTOOD;
import static com.voxopus.chiwiserver.enums.ReviewCommandType.QA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.enums.AnswerState;
import com.voxopus.chiwiserver.enums.QuizState;
import com.voxopus.chiwiserver.enums.ReviewCommandType;
import com.voxopus.chiwiserver.model.review_session.FlashcardQueueItem;
import com.voxopus.chiwiserver.model.review_session.QuizSession;
import com.voxopus.chiwiserver.model.review_session.ReviewSession;
import com.voxopus.chiwiserver.model.reviewer.Flashcard;
import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.review_session.FlashcardQueueItemRepository;
import com.voxopus.chiwiserver.repository.review_session.QuizSessionRepository;
import com.voxopus.chiwiserver.repository.review_session.ReviewSessionRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerRepository;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.response.review_session.QuizResponseData;
import com.voxopus.chiwiserver.response.review_session.ReviewResultsResponseData;
import com.voxopus.chiwiserver.response.review_session.ReviewSessionResponseData;
import com.voxopus.chiwiserver.response.whisper.WhisperInference;
import com.voxopus.chiwiserver.session_state.review.ReviewSessionState;
import com.voxopus.chiwiserver.util.Checker;
import com.voxopus.chiwiserver.util.Whisper;

@Service
public class ReviewSessionService {

    public static final String MISUNDERSTOOD_MESSAGE = "Sorry, I cound't understand that, woof!";
    public static final String REMARK1 = "You did terribly, arf!";
    public static final String REMARK2 = "You can do better, arf!";
    public static final String REMARK3 = "You did good, woof!";
    public static final String REMARK4 = "You did great, woof!";
    public static final String REMARK5 = "That was awesome, woof!";

    @Autowired
    ReviewSessionRepository reviewSessionRepository;

    @Autowired
    ReviewerRepository reviewerRepository;

    @Autowired
    QuizSessionRepository quizSessionRepository;

    @Autowired
    FlashcardQueueItemRepository flashcardQueueItemRepository;

    @Autowired
    UserRepository userRepository;

    public Checker<?> startSession(User user, Long reviewerId) {
        Optional<ReviewSession> existingSession = reviewSessionRepository.findByReviewerId(reviewerId);
        if (existingSession.isPresent()) {
            Calendar now = Calendar.getInstance();
            Calendar expiration = existingSession.get().getDateUsed();
            expiration.add(Calendar.MINUTE, 30);
            if (now.before(expiration)) {
                return Checker.fail("A session is still active");
            }
            reviewSessionRepository.delete(existingSession.get());
            reviewSessionRepository.flush();
        }

        Optional<Reviewer> reviewer = reviewerRepository.findById(reviewerId);

        if (reviewer.get().getFlashcards().size() <= 0) {
            return Checker.fail("This reviewer doesn't have any flashcards");
        }

        ReviewSession session = ReviewSession.builder()
                .user(user)
                .dateUsed(Calendar.getInstance())
                .reviewer(reviewer.get())
                .currentFlashcard(Long.valueOf(0))
                .build();

        reviewSessionRepository.save(session);

        final var flashcards = setupFlashcardsQueue(session);

        QuizSession quizSession = QuizSession.builder()
                .reviewSession(session)
                .answer(null)
                .state(QuizState.LISTEN_FOR_ANSWER)
                .build();

        quizSessionRepository.save(quizSession);

        final var response = new ReviewSessionResponseData<>("Let's start reviewing, woof!",
                INIT,
                new QuizResponseData(
                        flashcards.get(0).getFlashcard().getQuestion()));

        return Checker.ok("session started", response);
    }

    private List<FlashcardQueueItem> setupFlashcardsQueue(ReviewSession session) {
        List<Flashcard> flashcards = session.getReviewer().getFlashcards();
        Collections.shuffle(flashcards);
        List<FlashcardQueueItem> queueItems = new ArrayList<>();
        for (int i = 0; i < flashcards.size(); i++) {
            final var queueItem = FlashcardQueueItem.builder()
                    .flashcard(flashcards.get(i))
                    .answerState(AnswerState.UNANSWERED)
                    .reviewSession(session)
                    .queuePosition(Long.valueOf(i))
                    .build();
            queueItems.add(queueItem);
        }
        flashcardQueueItemRepository.saveAll(queueItems);
        return queueItems;
    }

    public Checker<?> processCommand(Long userId, byte[] audioData) {
        var session = reviewSessionRepository.findByUserId(userId);

        if (!session.isPresent()) {
            return Checker.fail("User has no review session active");
        }

        WhisperInference inference = Whisper.transcribe(audioData);
        if (inference.getError() != null) {
            return Checker.fail(inference.getError());
        }

        String speech = inference.getText();
        ReviewCommandType command = getCommand(speech);
        final var result = handleCommands(session.get(), speech, command);

        return Checker.ok(result.getMessage(), result);
    }

    private ReviewSessionResponseData<?> handleCommands(ReviewSession session, String speech,
            ReviewCommandType command) {
        switch (command) {
            case QA:
                return qaCommandProcess(session, speech);
            case FINISH:
                return finishCommandProcess(session);
            case MISUNDERSTOOD:
            default:
                return new ReviewSessionResponseData<>("something went wrong, arf!", command);
        }
    }

    private ReviewSessionResponseData<?> qaCommandProcess(ReviewSession session, String speech) {
        QuizSession quizSession;
        if (session.getQuizSession() == null) {
            quizSession = setupQuizSession(session);
        } else
            quizSession = session.getQuizSession();
        var state = new ReviewSessionState(quizSession);
        var result = state.handleStates(speech);
        String message;
        final Long currentFlashcard = session.getCurrentFlashcard();
        switch (result.getStatus()) {
            case CONTINUE:
                FlashcardQueueItem flashcard = session.getFlashcardQueueItems()
                    .get(currentFlashcard.intValue());
                QuizResponseData data = 
                    new QuizResponseData(flashcard.getFlashcard().getQuestion());
                message = result.getMessage();
                quizSessionRepository.save(quizSession);
                return new ReviewSessionResponseData<>(message, QA, data);
            case FINISHED:
                return qaFinished(session, quizSession, currentFlashcard);
            case MISUNDERSTOOD:
            default:
                message = MISUNDERSTOOD_MESSAGE;
                return new ReviewSessionResponseData<>(message, MISUNDERSTOOD, null);
        }
    }

    private ReviewSessionResponseData<?> qaFinished(ReviewSession session, QuizSession quizSession, Long currentFlashcard) {
        String message;
        QuizResponseData data = null;
        ReviewCommandType command = QA;

        if (currentFlashcard + 1 == session.getFlashcardQueueItems().size()) {
            command = COMPLETE;
            message = "Review Completed!";
        } else {
            final var nextCard = session.getFlashcardQueueItems()
                    .get(currentFlashcard.intValue() + 1).getFlashcard();
            message = "" + nextCard.getQuestion();
            data = new QuizResponseData(nextCard.getQuestion());
            session.setCurrentFlashcard(currentFlashcard + 1);
        }

        session.setQuizSession(null);
        reviewSessionRepository.saveAndFlush(session);
        quizSessionRepository.delete(quizSession);

        return new ReviewSessionResponseData<>(message, command, data);
    }

    public Checker<?> showSessionResults(User user){
        final var session = user.getReviewSession();
        if(session == null){
            return Checker.fail("there is no session");
        }

        final var flashcards = session.getFlashcardQueueItems();
        if(session.getCurrentFlashcard() + 1 < flashcards.size()){
            return Checker.fail("session isn't done yet");
        }

        int score = 0;
        int itemCount;
        String remark;
        itemCount = flashcards.size();
        for (var flashcard : flashcards) {
            if(flashcard.getAnswerState() == AnswerState.CORRECT)
                score++;
        }

        remark = getRemark(score, itemCount);

        clearSession(session);

        return Checker.ok("session finished", ReviewResultsResponseData.builder()
                .score(score)
                .total_items(itemCount)
                .message(remark)
                .build());
    }

    // FIXME: giving the wrong remark
    private String getRemark(int score, int total){
        double ratio = score/total;
        if(ratio >= 0.9){
            return REMARK5;
        } else if(ratio >= 0.8){
            return REMARK4;
        } else if(ratio >= 0.7){
            return REMARK3;
        } else if(ratio >= 0.4){
            return REMARK2;
        } else {
            return REMARK1;
        }
    }

    private void clearSession(ReviewSession session) {
        User user = session.getUser();
        user.setReviewSession(null);
        userRepository.saveAndFlush(user);

        Reviewer reviewer = session.getReviewer();
        reviewer.setReviewSession(null);
        reviewerRepository.saveAndFlush(reviewer);

        reviewSessionRepository.delete(session);
    }

    private QuizSession setupQuizSession(ReviewSession session) {
        QuizSession quizSession = QuizSession.builder()
                .reviewSession(session)
                .answer(null)
                .state(QuizState.LISTEN_FOR_ANSWER)
                .build();
        quizSessionRepository.save(quizSession);
        return quizSession;
    }

    private ReviewSessionResponseData<?> finishCommandProcess(ReviewSession session) {
        clearSession(session);
        return new ReviewSessionResponseData<>("cleaned up, woof!", ReviewCommandType.FINISH);
    }

    private ReviewCommandType getCommand(String speech) {
        String normalized = speech
                .trim()
                .toLowerCase()
                .replaceAll("[^0-9a-z' ]", "");
        System.out.printf("normalized: %s\n", normalized);
        switch (normalized) {
            case "i'm done":
            case "done":
            case "finish":
                return FINISH;
            case "":
                return MISUNDERSTOOD;
            default:
                return QA;
        }
    }

}
