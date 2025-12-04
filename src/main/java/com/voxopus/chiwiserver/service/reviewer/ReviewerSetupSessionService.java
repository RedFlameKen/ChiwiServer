package com.voxopus.chiwiserver.service.reviewer;

import static com.voxopus.chiwiserver.enums.CreateFlashcardState.INIT;
import static com.voxopus.chiwiserver.enums.SetupCommandType.CREATE_FLASHCARD;
import static com.voxopus.chiwiserver.enums.SetupCommandType.FINISH_SETUP;
import static com.voxopus.chiwiserver.enums.SetupCommandType.HELP;
import static com.voxopus.chiwiserver.enums.SetupCommandType.LIST;
import static com.voxopus.chiwiserver.enums.SetupCommandType.MISUNDERSTOOD;
import static com.voxopus.chiwiserver.enums.SetupCommandType.RUDE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.enums.FlashcardType;
import com.voxopus.chiwiserver.enums.SetupCommandType;
import com.voxopus.chiwiserver.model.reviewer.Answer;
import com.voxopus.chiwiserver.model.reviewer.Flashcard;
import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.model.setup_session.CreateFlashcardSession;
import com.voxopus.chiwiserver.model.setup_session.ReviewerSetupSession;
import com.voxopus.chiwiserver.model.setup_session.SetupStep;
import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.reviewer.AnswerRepository;
import com.voxopus.chiwiserver.repository.reviewer.FlashcardRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerRepository;
import com.voxopus.chiwiserver.repository.setup_session.CreateFlashcardSessionRepository;
import com.voxopus.chiwiserver.repository.setup_session.ReviewerSetupSessionRepository;
import com.voxopus.chiwiserver.repository.setup_session.SetupStepRepository;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.response.reviewer.AnswerResponseData;
import com.voxopus.chiwiserver.response.reviewer.FlashcardResponseData;
import com.voxopus.chiwiserver.response.reviewer.ReviewerSetupResponseData;
import com.voxopus.chiwiserver.response.setup_session.SetupSessionResponseData;
import com.voxopus.chiwiserver.response.whisper.WhisperInference;
import com.voxopus.chiwiserver.session_state.setup.CreateFlashcardSessionState;
import com.voxopus.chiwiserver.util.Checker;
import com.voxopus.chiwiserver.util.StringHelper;
import com.voxopus.chiwiserver.util.Whisper;

@Service
public class ReviewerSetupSessionService {

    public static final String MISUNDERSTOOD_MESSAGE = "Sorry, not sure what you mean, woof!";
    public static final String LIST_FLASHCARDS_MESSAGE = "Here are the flashcards in this reviewer, woof!";
    public static final String UNAVAILABLE_MESSAGE = "Feature unavailable, woof!";
    public static final String RUDE_MESSAGE = "Rude...";
    public static final String HELP_MESSAGE = """
        Commands:
        "help": 
            show the available commands
        "create flashcard": 
            Create a new flashcard
        "finish":
            Finish setting up
        "list flashcards":
            Show a list of all flashcards in this reviewer
        """;

    @Autowired
    ReviewerRepository reviewerRepository;

    @Autowired
    FlashcardRepository flashcardRepository;

    @Autowired
    ReviewerSetupSessionRepository reviewerSetupSessionRepository;

    @Autowired
    CreateFlashcardSessionRepository createFlashcardSessionRepository;

    @Autowired
    SetupStepRepository setupStepRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AnswerRepository answerRepository;

    public Checker<?> cancelSession(User user, Long reviewerId){
        final var reviewer = reviewerRepository.findById(reviewerId);
        if(!reviewer.isPresent()){
            return Checker.fail("reviewer does not exist");
        }

        final var session = reviewer.get().getReviewerSetupSession();

        deleteSetupSession(session);

        return Checker.ok("successfully deleted the session", null);
    }

    public Checker<?> startSession(User user, Long reviewerId){
        ReviewerSetupSession session = user.getReviewerSetupSession();

        if(session != null){
            // Calendar now = Calendar.getInstance();
            // Calendar expiration = session.getDateUsed();
            // expiration.add(Calendar.MINUTE, 30);
            // if(now.before(expiration)){
            //     deleteSetupSession(session.get());
            //     return Checker.ok("A session is still active", true);
            // }
            deleteSetupSession(session);
            // reviewerSetupSessionRepository.delete(session.get());
            // reviewerSetupSessionRepository.flush();
        }

        Optional<Reviewer> reviewer = reviewerRepository.findById(reviewerId);

        ReviewerSetupSession newSession = ReviewerSetupSession.builder()
            .reviewer(reviewer.get())
            .dateUsed(Calendar.getInstance())
            .user(user)
            .build();

        reviewerSetupSessionRepository.save(newSession);

        return Checker.ok(HELP_MESSAGE, ReviewerSetupResponseData.builder()
                .id(newSession.getId())
                .date_started(newSession.getDateStarted())
                .date_used(newSession.getDateUsed())
                .reviewer_id(newSession.getReviewer().getId())
                .build());
    }

    public Checker<?> processCommand(Long userId, String input){
        var session = reviewerSetupSessionRepository.findByUserId(userId);
        if(!session.isPresent()){
            return Checker.fail("user has no session yet");
        }

        var state = session.get().getSetupStep();
        SetupCommandType command;
        if(state == null)
            command = getSetupCommandType(input);

        else command = state.getCommandType();
        var response = dispatchCommand(session.get(), input, command);

        return Checker.ok("command processed", response);
    }

    public Checker<?> processCommand(Long userId, byte[] audioData){
        var session = reviewerSetupSessionRepository.findByUserId(userId);
        if(!session.isPresent()){
            return Checker.fail("user has no session yet");
        }

        WhisperInference inference = Whisper.transcribe(audioData);
        if(inference.getText() != null){
            System.out.printf("there was inferred text: %s\n", inference.getText());
        }
        if(inference.getError() != null){
            System.out.printf("there was an error\n");
            return Checker.fail(inference.getError());
        }

        String speech = inference.getText();
        var state = session.get().getSetupStep();
        SetupCommandType command;
        if(state == null)
            command = getSetupCommandType(speech);
        else command = state.getCommandType();
        var response = dispatchCommand(session.get(), speech, command);

        return Checker.ok("command processed", response);
    }

    private SetupSessionResponseData<?> dispatchCommand(ReviewerSetupSession session, String speech, SetupCommandType commandType){
        speech = StringHelper.removeNextline(speech);
        switch (commandType) {
            case CREATE_FLASHCARD:
                return createFlashcardCommandProcess(session, speech);
            case FINISH_SETUP:
                return finishSetupCommandProcess(session, speech);
            case HELP:
                return new SetupSessionResponseData<>(HELP_MESSAGE, commandType, speech);
            case LIST:
                return listFlashcardsCommandProcess(session, speech);
            case RUDE:
                return new SetupSessionResponseData<>(RUDE_MESSAGE, commandType, speech);
            case MISUNDERSTOOD:
            default:
                return new SetupSessionResponseData<>(MISUNDERSTOOD_MESSAGE, commandType, speech);
        }
    }

    private SetupSessionResponseData<?> listFlashcardsCommandProcess(ReviewerSetupSession session, String speech){
        List<Flashcard> flashcards = session.getReviewer().getFlashcards();

        ArrayList<FlashcardResponseData> data = new ArrayList<>();
        flashcards.forEach((flashcard) -> {
            ArrayList<AnswerResponseData> answers = new ArrayList<>();
            flashcard.getAnswers().forEach((answer) -> {
                answers.add(AnswerResponseData.builder()
                        .id(answer.getId())
                        .answer(answer.getAnswer())
                        .build());
            });
            final var responseData = FlashcardResponseData.builder()
                .question(flashcard.getQuestion())
                .date_created(flashcard.getDate_created())
                .date_modified(flashcard.getDate_modified())
                .flashcard_type(flashcard.getType().toString())
                .flashcard_id(flashcard.getId())
                .reviewer_id(flashcard.getReviewer().getId())
                .answers(answers)
                .build();
            data.add(responseData);
        });
        return new SetupSessionResponseData<>(LIST_FLASHCARDS_MESSAGE, LIST, speech, data);
    }

    private SetupSessionResponseData<?> finishSetupCommandProcess(ReviewerSetupSession session, String speech){
        deleteSetupSession(session);
        return new SetupSessionResponseData<>("Alright, I cleaned it all up, woof!", FINISH_SETUP, speech);
    }

    private void deleteSetupSession(ReviewerSetupSession session){
        session.getReviewer().setReviewerSetupSession(null);
        reviewerRepository.saveAndFlush(session.getReviewer());
        session.getUser().setReviewerSetupSession(null);
        userRepository.saveAndFlush(session.getUser());

        var setupStep = session.getSetupStep();
        if(setupStep != null){
            setupStepRepository.delete(setupStep);
            setupStepRepository.flush();
            session.setSetupStep(null);
        }

        var createFlashcardSession = session.getCreateFlashcardSession();
        if(createFlashcardSession != null){
            createFlashcardSessionRepository.delete(createFlashcardSession);
            createFlashcardSessionRepository.flush();
            session.setCreateFlashcardSession(null);
        }

        reviewerSetupSessionRepository.delete(session);
        reviewerSetupSessionRepository.flush();
    }

    private SetupSessionResponseData<?> createFlashcardCommandProcess(ReviewerSetupSession reviewerSetupSession, String speech){
        CreateFlashcardSession session;
        if(reviewerSetupSession.getCreateFlashcardSession() == null){
            session = CreateFlashcardSession.builder()
                .state(INIT)
                .question(null)
                .answer(null)
                .reviewerSetupSession(reviewerSetupSession)
                .build();
            createFlashcardSessionRepository.save(session);
            var stepCheck = setupStepRepository.findByReviewerSetupSessionId(reviewerSetupSession.getId());
            SetupStep step;
            if(stepCheck.isPresent()){
                step = stepCheck.get();
                step.setCommandType(CREATE_FLASHCARD);
            } else
                step = SetupStep.builder()
                    .reviewerSetupSession(reviewerSetupSession)
                    .commandType(CREATE_FLASHCARD)
                    .build();
            setupStepRepository.save(step);
        } else
            session = reviewerSetupSession.getCreateFlashcardSession();
        var state = new CreateFlashcardSessionState(session);
        var result = state.handleStates(speech);
        String message;
        switch (result.getStatus()) {
            case CONTINUE:
                message = result.getMessage();
                createFlashcardSessionRepository.save(session);
                break;
            case FINISHED:
                message = result.getMessage();
                Flashcard flashcard = flashcardRepository.save(Flashcard.builder()
                        .type(FlashcardType.SIMPLE)
                        .question(session.getQuestion())
                        .reviewer(reviewerSetupSession.getReviewer())
                        .build());
                final var answers = createSimpleAnswer(session.getAnswer(), flashcard);
                answerRepository.saveAll(answers);
                var step = reviewerSetupSession.getSetupStep();
                reviewerSetupSession.setCreateFlashcardSession(null);
                reviewerSetupSession.setSetupStep(null);
                reviewerSetupSessionRepository.saveAndFlush(reviewerSetupSession);
                createFlashcardSessionRepository.delete(session);
                createFlashcardSessionRepository.flush();
                setupStepRepository.delete(step);
                setupStepRepository.flush();
                break;
            case MISUNDERSTOOD:
            default:
                message = MISUNDERSTOOD_MESSAGE;
                break;
        }
        return new SetupSessionResponseData<>(message, CREATE_FLASHCARD, speech);
    }

    private List<Answer> createSimpleAnswer(String answer, Flashcard flashcard){
        return List.of(Answer.builder()
            .answer(answer)
            .flashcard(flashcard)
            .build());
    }

    private SetupCommandType getSetupCommandType(String speech){
        String normalized = speech
            .trim()
            .toLowerCase()
            .replaceAll("[^0-9a-z' ]", "");
        System.out.printf("normalized: %s\n", normalized);
        switch (normalized) {
            case "help":
                return HELP;
            case "create flashcard":
                return CREATE_FLASHCARD;
            case "i'm done":
            case "done":
            case "finish":
                return FINISH_SETUP;
            case "show flashcards":
            case "list flashcards":
                return LIST;
            case "woof":
            case "woof!":
            case "woof woof":
            case "arf":
            case "arf!":
            case "arf arf":
                return RUDE;
            default:
                return MISUNDERSTOOD;
        }
    }

}
