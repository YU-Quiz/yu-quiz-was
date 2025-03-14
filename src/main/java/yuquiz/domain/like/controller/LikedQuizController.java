package yuquiz.domain.like.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yuquiz.common.api.SuccessRes;
import yuquiz.domain.like.api.LikedQuizApi;
import yuquiz.domain.like.dto.quiz.LikedQuizSortType;
import yuquiz.domain.quiz.dto.quiz.QuizSummaryRes;
import yuquiz.domain.like.service.LikedQuizService;
import yuquiz.security.auth.SecurityUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizzes")
public class LikedQuizController implements LikedQuizApi {
    private final LikedQuizService likedQuizService;

    @GetMapping("/liked")
    public ResponseEntity<?> getLikedQuizzes(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "sort", defaultValue = "LIKED_DATE_DESC")LikedQuizSortType sort) {

        Page<QuizSummaryRes> likedQuizzes = likedQuizService.getLikedQuizzes(userDetails.getId(), page, sort);

        return ResponseEntity.status(HttpStatus.OK).body(likedQuizzes);
    }

    @PostMapping("/{quizId}/likes")
    public ResponseEntity<?> likeQuiz(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable(value = "quizId") Long quizId) {

        likedQuizService.likeQuiz(userDetails.getId(), quizId);

        return ResponseEntity.status(HttpStatus.OK).body(SuccessRes.from("성공적으로 추가되었습니다."));
    }

    @DeleteMapping("/{quizId}/likes")
    public ResponseEntity<?> deleteLikeQuiz(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable(value = "quizId") Long quizId) {

        likedQuizService.deleteLikeQuiz(userDetails.getId(), quizId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
