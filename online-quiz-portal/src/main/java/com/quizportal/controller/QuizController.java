package com.quizportal.controller;

import com.quizportal.model.*;
import com.quizportal.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // GET all active quizzes
    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllActiveQuizzes());
    }

    // GET quiz by ID (metadata only)
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuiz(@PathVariable Long id) {
        return quizService.getQuizById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET questions for exam (no correct answers!)
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<Map<String, Object>>> getExamQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuestionsForExam(id));
    }

    // POST submit quiz answers
    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitQuiz(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            Authentication auth
    ) {
        @SuppressWarnings("unchecked")
        Map<String, String> rawAnswers = (Map<String, String>) body.get("answers");
        long timeTaken = Long.parseLong(body.get("timeTakenSeconds").toString());

        Map<Long, String> answers = new HashMap<>();
        for (Map.Entry<String, String> entry : rawAnswers.entrySet()) {
            answers.put(Long.parseLong(entry.getKey()), entry.getValue());
        }

        Map<String, Object> result = quizService.gradeAttempt(id, auth.getName(), answers, timeTaken);
        return ResponseEntity.ok(result);
    }

    // GET user's attempt history
    @GetMapping("/history")
    public ResponseEntity<?> getUserHistory(Authentication auth) {
        return ResponseEntity.ok(quizService.getUserHistory(auth.getName()));
    }

    // GET global leaderboard
    @GetMapping("/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard() {
        return ResponseEntity.ok(quizService.getLeaderboard());
    }

    // GET leaderboard for a specific quiz
    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getQuizLeaderboard(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizLeaderboard(id));
    }
}
