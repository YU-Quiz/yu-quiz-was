package yuquiz.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.quiz.dto.quiz.*;
import yuquiz.domain.quiz.repository.PinnedQuizRepository;
import yuquiz.domain.quiz.entity.Quiz;
import yuquiz.domain.quiz.exception.QuizExceptionCode;
import yuquiz.domain.quiz.repository.QuizRepository;
import yuquiz.domain.like.repository.LikedQuizRepository;
import yuquiz.domain.report.repository.ReportRepository;
import yuquiz.domain.subject.entity.Subject;
import yuquiz.domain.subject.exception.SubjectExceptionCode;
import yuquiz.domain.subject.repository.SubjectRepository;
import yuquiz.domain.quiz.entity.TriedQuiz;
import yuquiz.domain.quiz.repository.TriedQuizRepository;
import yuquiz.domain.user.entity.User;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TriedQuizRepository triedQuizRepository;
    private final PinnedQuizRepository pinnedQuizRepository;
    private final LikedQuizRepository likedQuizRepository;
    private final ReportRepository reportRepository;

    private static final Integer QUIZ_PER_PAGE = 20;

    @Transactional
    public void createQuiz(QuizReq quizReq, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        Subject subject = subjectRepository.findById(quizReq.subjectId())
                .orElseThrow(() -> new CustomException(SubjectExceptionCode.INVALID_ID));


        Quiz quiz = quizReq.toEntity(user, subject);
        quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long quizId, Long userId) {

        if (!validateWriter(quizId, userId)) {
            throw new CustomException(QuizExceptionCode.UNAUTHORIZED_ACTION);
        }

        quizRepository.deleteById(quizId);
    }

    @Transactional
    public void updateQuiz(Long quizId, QuizReq quizReq, Long userId) {

        if (!validateWriter(quizId, userId)) {
            throw new CustomException(QuizExceptionCode.UNAUTHORIZED_ACTION);
        }

        Subject subject = subjectRepository.findById(quizReq.subjectId())
                .orElseThrow(() -> new CustomException(SubjectExceptionCode.INVALID_ID));

        quizRepository.updateById(quizId, quizReq.title(), quizReq.question(), subject,
                quizReq.answer(), quizReq.choices(), quizReq.quizImg(), quizReq.quizType());

        reportRepository.deleteByQuiz(quizId);
    }

    @Transactional
    public QuizRes getQuizById(Long userId, Long quizId) {
        User user = findUserByUserId(userId);
        Quiz quiz = findQuizByQuizId(quizId);

        boolean isLiked = likedQuizRepository.existsByUserAndQuiz(user, quiz);
        boolean isPinned = pinnedQuizRepository.existsByUserAndQuiz(user, quiz);
        boolean isWriter = validateWriter(quizId, userId);

        quiz.increaseViewCount();

        return QuizRes.fromEntity(quiz, isLiked, isPinned, isWriter);
    }

    @Transactional
    public boolean gradeQuiz(Long userId, Long quizId, String answer) {
        Quiz quiz = findQuizByQuizId(quizId);
        User user = findUserByUserId(userId);

        boolean isSolved = quiz.getAnswer().equals(answer);

        TriedQuiz triedQuiz = triedQuizRepository.findByUserAndQuiz(user, quiz)
                .orElse(new TriedQuiz(isSolved, user, quiz));

        triedQuiz.updateIsSolved(isSolved);
        triedQuizRepository.save(triedQuiz);

        return isSolved;
    }

    public String getAnswer(Long quizId) {
        return findQuizByQuizId(quizId).getAnswer();
    }

    @Transactional(readOnly = true)
    public QuizListRes getQuizzesByKeywordAndSubject(Long userId, String keyword, Long subjectId, QuizSortType sort, Integer page) {
        Pageable pageable = PageRequest.of(page, QUIZ_PER_PAGE);
        User user = findUserByUserId(userId);
        Page<Quiz> quizzes = quizRepository.getQuizzes(keyword, pageable);

        List<QuizSummaryRes> list = new ArrayList<>();

        quizzes.getContent().forEach(quiz -> {
            Boolean isSolved = triedQuizRepository.findIsSolvedByUserAndQuiz(user, quiz);
            list.add(QuizSummaryRes.fromEntity(quiz, isSolved));
        });

        return QuizListRes.of(list, quizzes.getNumber(), quizzes.getTotalElements(), quizzes.getTotalPages());
    }

    @Transactional(readOnly = true)
    public Page<QuizSummaryRes> getQuizzesByWriter(Long userId, QuizSortType sort, Integer page) {
        User user = findUserByUserId(userId);

        Pageable pageable = PageRequest.of(page, QUIZ_PER_PAGE, sort.getSort());
        Page<Quiz> quizzes = quizRepository.findAllByWriter(user, pageable);

        return quizzes.map(quiz -> QuizSummaryRes.fromEntity(quiz, true));
    }

    private User findUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));
    }

    private Quiz findQuizByQuizId(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(QuizExceptionCode.INVALID_ID));
    }

    private boolean validateWriter(Long quizId, Long userId) {
        return quizRepository.findWriterById(quizId)
                .map(writerId -> writerId.equals(userId))
                .orElse(false);
    }
}
