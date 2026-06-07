package com.quizportal.service;

import com.quizportal.model.*;
import com.quizportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    @Autowired private QuizRepository quizRepo;
    @Autowired private QuestionRepository questionRepo;
    @Autowired private QuizAttemptRepository attemptRepo;
    @Autowired private UserRepository userRepo;

    // ── Quiz CRUD ──────────────────────────────────────────────────

    public List<Quiz> getAllActiveQuizzes() {
        List<Quiz> quizzes = quizRepo.findByActiveTrue();
        for (Quiz q : quizzes) {
            q.setQuestionCount((int) questionRepo.countByQuizId(q.getId()));
        }
        return quizzes;
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepo.findById(id);
    }

    public Quiz createQuiz(Quiz quiz) {
        return quizRepo.save(quiz);
    }

    // ── Questions ─────────────────────────────────────────────────

    public List<Question> getQuestionsForQuiz(Long quizId) {
        return questionRepo.findByQuizId(quizId);
    }

    /**
     * Returns questions stripped of correct answers (for exam delivery).
     */
    public List<Map<String, Object>> getQuestionsForExam(Long quizId) {
        List<Question> questions = questionRepo.findByQuizId(quizId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("id", q.getId());
            qMap.put("questionText", q.getQuestionText());
            qMap.put("optionA", q.getOptionA());
            qMap.put("optionB", q.getOptionB());
            qMap.put("optionC", q.getOptionC());
            qMap.put("optionD", q.getOptionD());
            result.add(qMap);
        }
        return result;
    }

    // ── Grading ───────────────────────────────────────────────────

    /**
     * Grades a submission. answers map: { questionId -> selectedOption ("A"/"B"/"C"/"D") }
     * Returns a detailed result map.
     */
    public Map<String, Object> gradeAttempt(
            Long quizId,
            String username,
            Map<Long, String> answers,
            long timeTakenSeconds
    ) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Question> questions = questionRepo.findByQuizId(quizId);

        int correct = 0;
        List<Map<String, Object>> breakdown = new ArrayList<>();

        for (Question q : questions) {
            String selected = answers.getOrDefault(q.getId(), "");
            boolean isCorrect = q.getCorrectOption().equalsIgnoreCase(selected);
            if (isCorrect) correct++;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("questionId", q.getId());
            item.put("questionText", q.getQuestionText());
            item.put("selected", selected.isEmpty() ? "Not answered" : selected);
            item.put("correct", q.getCorrectOption());
            item.put("isCorrect", isCorrect);
            item.put("optionA", q.getOptionA());
            item.put("optionB", q.getOptionB());
            item.put("optionC", q.getOptionC());
            item.put("optionD", q.getOptionD());
            breakdown.add(item);
        }

        double percentage = questions.isEmpty() ? 0 : (correct * 100.0) / questions.size();

        // Persist attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setScore(correct);
        attempt.setTotalQuestions(questions.size());
        attempt.setPercentage(Math.round(percentage * 10.0) / 10.0);
        attempt.setTimeTakenSeconds(timeTakenSeconds);
        attemptRepo.save(attempt);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("quizTitle", quiz.getTitle());
        result.put("score", correct);
        result.put("totalQuestions", questions.size());
        result.put("percentage", attempt.getPercentage());
        result.put("timeTakenSeconds", timeTakenSeconds);
        result.put("grade", getGrade(attempt.getPercentage()));
        result.put("breakdown", breakdown);
        return result;
    }

    private String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "F";
    }

    // ── History & Leaderboard ─────────────────────────────────────

    public List<QuizAttempt> getUserHistory(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return attemptRepo.findByUserIdOrderByAttemptedAtDesc(user.getId());
    }

    public List<Map<String, Object>> getLeaderboard() {
        List<QuizAttempt> attempts = attemptRepo.findGlobalLeaderboard();
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = 1;
        for (QuizAttempt a : attempts) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("rank", rank++);
            entry.put("username", a.getUser().getUsername());
            entry.put("quizTitle", a.getQuiz().getTitle());
            entry.put("score", a.getScore());
            entry.put("totalQuestions", a.getTotalQuestions());
            entry.put("percentage", a.getPercentage());
            entry.put("timeTakenSeconds", a.getTimeTakenSeconds());
            entry.put("attemptedAt", a.getAttemptedAt().toString());
            leaderboard.add(entry);
        }
        return leaderboard;
    }

    public List<Map<String, Object>> getQuizLeaderboard(Long quizId) {
        List<QuizAttempt> attempts = attemptRepo.findByQuizIdOrderByPercentageDescTimeTakenSecondsAsc(quizId);
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = 1;
        for (QuizAttempt a : attempts) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("rank", rank++);
            entry.put("username", a.getUser().getUsername());
            entry.put("score", a.getScore());
            entry.put("totalQuestions", a.getTotalQuestions());
            entry.put("percentage", a.getPercentage());
            entry.put("timeTakenSeconds", a.getTimeTakenSeconds());
            leaderboard.add(entry);
        }
        return leaderboard;
    }
}
