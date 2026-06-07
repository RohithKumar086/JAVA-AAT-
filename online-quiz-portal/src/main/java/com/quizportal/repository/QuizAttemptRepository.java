package com.quizportal.repository;

import com.quizportal.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserIdOrderByAttemptedAtDesc(Long userId);

    List<QuizAttempt> findByQuizIdOrderByPercentageDescTimeTakenSecondsAsc(Long quizId);

    @Query("SELECT qa FROM QuizAttempt qa ORDER BY qa.percentage DESC, qa.timeTakenSeconds ASC")
    List<QuizAttempt> findGlobalLeaderboard();

    boolean existsByUserIdAndQuizId(Long userId, Long quizId);
}
