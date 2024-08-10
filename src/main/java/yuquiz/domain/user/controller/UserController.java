package yuquiz.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuquiz.common.api.SuccessRes;
import yuquiz.domain.user.api.UserApi;
import yuquiz.domain.user.dto.SignUpReq;
import yuquiz.domain.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    /* 회원가입 */
    @Override
    @PostMapping
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpReq signUpReq) {

        userService.createUser(signUpReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("회원가입 성공."));
    }
}
