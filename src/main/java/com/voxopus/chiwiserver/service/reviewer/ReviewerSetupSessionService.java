package com.voxopus.chiwiserver.service.reviewer;

import static com.voxopus.chiwiserver.enums.CreateFlashcardState.INIT;
import static com.voxopus.chiwiserver.enums.SetupCommandType.CREATE_FLASHCARD;
import static com.voxopus.chiwiserver.enums.SetupCommandType.FINISH_SETUP;
import static com.voxopus.chiwiserver.enums.SetupCommandType.HELP;
import static com.voxopus.chiwiserver.enums.SetupCommandType.LIST;
import static com.voxopus.chiwiserver.enums.SetupCommandType.MISUNDERSTOOD;

import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.voxopus.chiwiserver.enums.FlashcardType;
import com.voxopus.chiwiserver.enums.SetupCommandType;
import com.voxopus.chiwiserver.model.reviewer.Answer;
import com.voxopus.chiwiserver.model.reviewer.Flashcard;
import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.model.setup_session.CreateFlashcardSession;
import com.voxopus.chiwiserver.model.setup_session.ReviewerSetupSession;
import com.voxopus.chiwiserver.model.setup_session.SetupStep;
import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.repository.reviewer.FlashcardRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerSetupSessionRepository;
import com.voxopus.chiwiserver.repository.setup_session.CreateFlashcardSessionRepository;
import com.voxopus.chiwiserver.repository.setup_session.SetupStepRepository;
import com.voxopus.chiwiserver.response.reviewer.ReviewerSetupResponseData;
import com.voxopus.chiwiserver.response.setup_session.SetupSessionResponseData;
import com.voxopus.chiwiserver.response.whisper.WhisperInference;
import com.voxopus.chiwiserver.session_state.setup.CreateFlashcardSessionState;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class ReviewerSetupSessionService {

    public static final String MISUNDERSTOOD_MESSAGE = "Sorry, I cound't understand that, woof!";
    public static final String UNAVAILABLE_MESSAGE = "feature unavailable, woof!";
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

    public Checker<?> startSession(User user, Long reviewerId){
        Optional<ReviewerSetupSession> session = reviewerSetupSessionRepository
            .findByReviewerId(reviewerId);

        if(session.isPresent()){
            Calendar now = Calendar.getInstance();
            Calendar expiration = session.get().getDateUsed();
            expiration.add(Calendar.MINUTE, 30);
            if(now.before(expiration)){
                return Checker.fail("A session is still active");
            }
            reviewerSetupSessionRepository.delete(session.get());
            reviewerSetupSessionRepository.flush();
        }

        Optional<Reviewer> reviewer = reviewerRepository.findById(reviewerId);

        ReviewerSetupSession newSession = ReviewerSetupSession.builder()
            .reviewer(reviewer.get())
            .dateUsed(Calendar.getInstance())
            .user(user)
            .build();

        reviewerSetupSessionRepository.save(newSession);

        return Checker.ok(ReviewerSetupResponseData.builder()
                .id(newSession.getId())
                .date_started(newSession.getDateStarted())
                .date_used(newSession.getDateUsed())
                .reviewer_id(newSession.getReviewer().getId())
                .build());
    }

    public Checker<?> processCommand(Long userId, byte[] audioData){
        var session = reviewerSetupSessionRepository.findByUserId(userId);
        if(!session.isPresent()){
            return Checker.fail("user has no session yet");
        }

        WhisperInference inference = whisperTranscribe(audioData);
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

    private SetupSessionResponseData dispatchCommand(ReviewerSetupSession session, String speech, SetupCommandType commandType){
        switch (commandType) {
            // TODO: actually create more functions or entities for each command for them to do different things
            case CREATE_FLASHCARD:
                return createFlashcardCommandProcess(session, speech);
            case FINISH_SETUP:
                return finishSetupCommandProcess(session);
            case HELP:
                return new SetupSessionResponseData(HELP_MESSAGE, commandType);
            case LIST:
                return new SetupSessionResponseData(UNAVAILABLE_MESSAGE, commandType);
            case MISUNDERSTOOD:
            default:
                return new SetupSessionResponseData(MISUNDERSTOOD_MESSAGE, commandType);
        }
    }

    private SetupSessionResponseData finishSetupCommandProcess(ReviewerSetupSession session){
        deleteSetupSession(session);
        return new SetupSessionResponseData("Alright, I cleaned it all up, woof!", FINISH_SETUP);
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

    private SetupSessionResponseData createFlashcardCommandProcess(ReviewerSetupSession reviewerSetupSession, String speech){
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
                flashcardRepository.save(Flashcard.builder()
                        .type(FlashcardType.SIMPLE)
                        .question(session.getQuestion())
                        .reviewer(reviewerSetupSession.getReviewer())
                        .answers(createSimpleAnswer(session.getAnswer()))
                        .build());
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
        return new SetupSessionResponseData(message, CREATE_FLASHCARD);
    }

    private List<Answer> createSimpleAnswer(String answer){
        return List.of(Answer.builder()
            .answer(answer)
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
            default:
                return MISUNDERSTOOD;
        }
    }

    private WhisperInference whisperTranscribe(byte[] audioData){
        RestTemplate template = new RestTemplate();
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition disposition = ContentDisposition.builder("form-data")
            .name("file")
            .filename("audio.wav")
            .build();

        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, disposition.toString());
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(audioData, fileMap);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", entity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request =
            new HttpEntity<MultiValueMap<String,Object>>(body, headers);

        URI uri = URI.create("http://localhost:5050/inference");
        ResponseEntity<WhisperInference> response =
            template.postForEntity(uri, request, WhisperInference.class);
        return response.getBody();
    }
    
}
