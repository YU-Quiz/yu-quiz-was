package yuquiz.domain.quiz.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuquiz.domain.quiz.api.AdminQuizApi;
import yuquiz.domain.quiz.dto.QuizSortType;
import yuquiz.domain.quiz.dto.QuizSummaryRes;
import yuquiz.domain.quiz.service.AdminQuizService;

@RestController
@RequestMapping("/api/v1/admin/quizzes")
@RequiredArgsConstructor
public class AdminQuizController implements AdminQuizApi {

    private final AdminQuizService adminQuizService;

    @GetMapping
    public ResponseEntity<?> getAllQuizzes(@RequestParam QuizSortType sort,
                                           @RequestParam @Min(0) Integer page) {

        Page<QuizSummaryRes> quizzes = adminQuizService.getAllQuizzes(sort, page);

        return ResponseEntity.status(HttpStatus.OK).body(quizzes);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable("quizId") Long quizId) {

        adminQuizService.deleteQuiz(quizId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
