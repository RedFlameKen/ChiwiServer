package com.voxopus.chiwiserver.service.reviewer;

import java.net.URI;
import java.util.Calendar;
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

import com.voxopus.chiwiserver.enums.SetupCommandType;
import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.model.setup_session.ReviewerSetupSession;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerSetupSessionRepository;
import com.voxopus.chiwiserver.response.reviewer.ReviewerSetupResponseData;
import com.voxopus.chiwiserver.response.setup_session.SetupSessionResponseData;
import com.voxopus.chiwiserver.response.whisper.WhisperInference;
import com.voxopus.chiwiserver.util.Checker;

import static com.voxopus.chiwiserver.enums.SetupCommandType.*;

@Service
public class ReviewerSetupSessionService {

    public static final String MISUNDERSTOOD_MESSAGE = "Sorry, I cound't understand that, woof!";
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
    ReviewerSetupSessionRepository reviewerSetupSessionRepository;

    public Checker<?> startSession(Long userId, Long reviewerId){
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
        }

        Optional<Reviewer> reviewer = reviewerRepository.findById(reviewerId);

        ReviewerSetupSession newSession = ReviewerSetupSession.builder()
            .reviewer(reviewer.get())
            .dateUsed(Calendar.getInstance())
            .build();

        reviewerSetupSessionRepository.save(newSession);

        return Checker.ok(ReviewerSetupResponseData.builder()
                .id(newSession.getId())
                .date_started(newSession.getDateStarted())
                .date_used(newSession.getDateUsed())
                .reviewer_id(newSession.getReviewer().getId())
                .build());
    }

    public Checker<?> processCommand(byte[] audioData){
        WhisperInference inference = whisperTranscribe(audioData);
        if(inference.getText() != null){
            System.out.printf("there was inferred text: %s\n", inference.getText());
        }
        if(inference.getError() != null){
            System.out.printf("there was an error\n");
            return Checker.fail(inference.getError());
        }

        String speech = inference.getText();
        SetupCommandType command = getSetupCommandType(speech);
        var response = dispatchCommand(speech, command);

        return Checker.ok("command processed", response);
    }

    private SetupSessionResponseData dispatchCommand(String speech, SetupCommandType commandType){
        switch (commandType) {
            // TODO: actually create more functions or entities for each command for them to do different things
            case CREATE_FLASHCARD:
                return new SetupSessionResponseData("Let's setup a flashcard, woof!", commandType);
            case FINISH_SETUP:
                return new SetupSessionResponseData("", commandType);
            case HELP:
                return new SetupSessionResponseData(HELP_MESSAGE, commandType);
            case LIST:
                return new SetupSessionResponseData("", commandType);
            case MISUNDERSTOOD:
            default:
                return new SetupSessionResponseData(MISUNDERSTOOD_MESSAGE, commandType);
        }
    }

    private SetupCommandType getSetupCommandType(String speech){
        String normalized = speech
            .trim()
            .toLowerCase()
            .replaceAll("[^0-9a-z ]", "");
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
