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

import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.model.reviewer.ReviewerSetupSession;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerSetupSessionRepository;
import com.voxopus.chiwiserver.response.reviewer.ReviewerSetupResponseData;
import com.voxopus.chiwiserver.response.whisper.WhisperInference;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class ReviewerSetupSessionService {

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
        return Checker.ok("successfully transcribed audio", inference);
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
