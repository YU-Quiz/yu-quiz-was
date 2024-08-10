package yuquiz.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import yuquiz.domain.user.entity.User;

@Schema(name = "SignUpDto", description = "회원가입 요청 DTO")
public record SignUpReq(
        @Schema(description = "아이디", example = "test")
        @NotBlank(message = "아이디는 필수 입력입니다.")
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$", message = "아이디는 특수문자를 제외한 4~20자리여야 합니다.")
        String username,

        @Schema(description = "비밀번호", example = "password123")
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,16}", message = "비밀번호는 8~16자 영문과 숫자를 사용하세요.")
        String password,

        @Schema(description = "닉네임", example = "테스터")
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
        String nickname,

        @Schema(description = "아이디", example = "test@gmail.com")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "학과", example = "컴퓨터공학과")
        @NotBlank(message = "학과는 필수 선택 값입니다.")
        String majorName,

        @Schema(description = "이메일 수신 동의", example = "true")
        boolean agreeEmail
) {
    public User toEntity(String encodePassword) {
        return User.builder()
                .username(username)
                .password(encodePassword)
                .nickname(nickname)
                .email(email)
                .majorName(majorName)
                .agreeEmail(agreeEmail)
                .build();
    }
}
