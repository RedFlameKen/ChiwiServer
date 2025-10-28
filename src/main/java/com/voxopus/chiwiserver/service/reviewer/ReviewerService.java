package com.voxopus.chiwiserver.service.reviewer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxopus.chiwiserver.enums.FlashcardType;
import com.voxopus.chiwiserver.model.reviewer.Answer;
import com.voxopus.chiwiserver.model.reviewer.Flashcard;
import com.voxopus.chiwiserver.model.reviewer.ReviewSession;
import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.model.user.User;
import com.voxopus.chiwiserver.repository.reviewer.AnswerRepository;
import com.voxopus.chiwiserver.repository.reviewer.FlashcardRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewSessionRepository;
import com.voxopus.chiwiserver.repository.reviewer.ReviewerRepository;
import com.voxopus.chiwiserver.repository.user.UserRepository;
import com.voxopus.chiwiserver.request.reviewer.CreateAnswerRequestData;
import com.voxopus.chiwiserver.request.reviewer.CreateFlashcardRequestData;
import com.voxopus.chiwiserver.request.reviewer.CreateReviewerRequestData;
import com.voxopus.chiwiserver.response.reviewer.AnswerResponseData;
import com.voxopus.chiwiserver.response.reviewer.FlashcardResponseData;
import com.voxopus.chiwiserver.response.reviewer.ListReviewersResponseData;
import com.voxopus.chiwiserver.response.reviewer.ReviewerResponseData;
import com.voxopus.chiwiserver.util.Checker;

@Service
public class ReviewerService {

    @Autowired
    private ReviewSessionRepository reviewSessionRepository;

    @Autowired
    private ReviewerRepository reviewerRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    public Checker<ReviewerResponseData> addReviewer(CreateReviewerRequestData data) {
        Optional<User> user = userRepository.findById(data.getUser_id());

        if (!user.isPresent()) {
            return Checker.fail("no user with the given id was found");
        }

        Date cur_date = new Date();
        Reviewer reviewer = Reviewer.builder()
                .name(data.getReviewer_name())
                .user(user.get())
                .subject(data.getSubject())
                .date_created(cur_date)
                .date_modified(cur_date)
                .build();

        reviewerRepository.save(reviewer);
        return Checker.ok("reviewer successfully created",
                ReviewerResponseData.builder()
                        .reviewer_id(reviewer.getId())
                        .reviewer_name(reviewer.getName())
                        .subject(reviewer.getSubject())
                        .user_id(reviewer.getUser().getId())
                        .build());
    }

    public Checker<List<ListReviewersResponseData>> getReviewersByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return Checker.fail("no user with the given id was found");
        }

        List<Reviewer> reviewers = reviewerRepository.findByUserId(userId);
        ArrayList<ListReviewersResponseData> responseData = new ArrayList<>();
        reviewers.forEach(reviewer -> {
            responseData.add(ListReviewersResponseData.builder()
                    .id(reviewer.getId())
                    .name(reviewer.getName())
                    .flashcards_count(reviewer.getFlashcards().size())
                    .build());
        });

        return Checker.ok("successfully listed reviewers", responseData);
    }

    public Checker<ReviewSession> startSession(Long userId, Long reviewerId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return Checker.fail("user not found");
        }

        Optional<Reviewer> reviewer = reviewerRepository.findById(reviewerId);
        if (!reviewer.isPresent()) {
            return Checker.fail("reviewer not found");
        }

        List<ReviewSession> sessions = reviewSessionRepository.findAllByUserId(userId);
        if (!sessions.isEmpty()) {
            return Checker.fail("a session already exists");
        }

        ReviewSession session = ReviewSession.builder()
                .reviewer(reviewer.get())
                .user(user.get())
                .timeStarted(new Date())
                .build();

        return new Checker<>(session);
    }

    public Checker<?> createFlashcard(Long reviewerId, CreateFlashcardRequestData data) {
        Optional<Reviewer> reviewer = reviewerRepository.findById(reviewerId);
        if (!reviewer.isPresent()) {
            return Checker.fail("reviewer not found");
        }

        FlashcardType type;
        try {
            type = FlashcardType.valueOf(data.getFlashcard_type());
        } catch (Exception e) {
            return Checker.fail(e, "no such flashcard type exists");
        }

        Date curDate = new Date();

        Flashcard flashcard = Flashcard.builder()
                .question(data.getQuestion())
                .reviewer(reviewer.get())
                .date_created(curDate)
                .type(type)
                .date_modified(curDate)
                .build();

        flashcard = flashcardRepository.save(flashcard);

        ArrayList<AnswerResponseData> answers = new ArrayList<>();

        writeAnswers(flashcard, answers, data.getAnswers());

        return Checker.ok("flashcard successfully created",
                FlashcardResponseData.builder()
                        .flashcard_id(flashcard.getId())
                        .question(flashcard.getQuestion())
                        .flashcard_type(flashcard.getType().toString())
                        .date_created(flashcard.getDate_created())
                        .date_modified(flashcard.getDate_modified())
                        .reviewer_id(flashcard.getReviewer().getId())
                        .answers(answers)
                        .build());
    }

    public Checker<List<FlashcardResponseData>> listFlashcards(Long reviewerId){
        Optional<Reviewer> reviewer = reviewerRepository.findById(reviewerId);
        if (!reviewer.isPresent()) {
            return Checker.fail("reviewer not found");
        }

        ArrayList<FlashcardResponseData> response = new ArrayList<>();

        List<Flashcard> flashcards = flashcardRepository.findByReviewerId(reviewerId);

        for (Flashcard flashcard : flashcards) {
            ArrayList<AnswerResponseData> answers = new ArrayList<>();
            for (Answer answer : flashcard.getAnswers()) {
                answers.add(AnswerResponseData.builder()
                        .id(answer.getId())
                        .answer(answer.getAnswer())
                        .build());
            }
            response.add(FlashcardResponseData.builder()
                .flashcard_id(flashcard.getId())
                .question(flashcard.getQuestion())
                .flashcard_type(flashcard.getType().toString())
                .reviewer_id(flashcard.getReviewer().getId())
                .date_created(flashcard.getDate_created())
                .date_modified(flashcard.getDate_modified())
                .answers(answers)
                .build());
        }

        return Checker.ok("successfully fetched reviewers", response);
    }

    private void writeAnswers(Flashcard flashcard, ArrayList<AnswerResponseData> answers,
            List<CreateAnswerRequestData> requestAnswers) {
        for (int i = 0; i < requestAnswers.size(); i++) {
            var _answer = requestAnswers.get(i);
            Answer answer = Answer.builder()
                .answer(_answer.getAnswer())
                .flashcard(flashcard)
                .build();
            answerRepository.save(answer);
            answers.add(AnswerResponseData.builder()
                    .id(answer.getId())
                    .answer(answer.getAnswer())
                    .build());
        }
    }

    public Checker<AnswerResponseData> addAnswer(Long flashcardId, CreateAnswerRequestData data) {
        Optional<Flashcard> flashcard = flashcardRepository.findById(flashcardId);

        if(!flashcard.isPresent())
            Checker.fail("flashcard not found");

        // TODO: add check for if number of answers are appropriate to flashcard type
        Answer answer = Answer.builder()
                .answer(data.getAnswer())
                .flashcard(flashcard.get())
                .build();

        answer = answerRepository.save(answer);

        return Checker.ok("Successfully created answer", AnswerResponseData.builder()
                .id(answer.getId())
                .answer(answer.getAnswer())
                .build());
    }

    public Checker<List<AnswerResponseData>> listAnswers(Long flashcardId){
        Optional<Flashcard> flashcard = flashcardRepository.findById(flashcardId);

        if(!flashcard.isPresent())
            Checker.fail("flashcard not found");

        List<Answer> answers = answerRepository.findByFlashcardId(flashcardId);

        ArrayList<AnswerResponseData> response = new ArrayList<>();

        for (Answer answer : answers) {
            response.add(AnswerResponseData.builder()
                    .id(answer.getId())
                    .answer(answer.getAnswer())
                    .build());
        }

        return Checker.ok("successfully fetched answers", response);
    }

}
