package exception;

public class LoginFailedException extends RuntimeException {

    public LoginFailedException() {
        super("로그인 실패: 이메일 또는 유저번호가 올바르지 않습니다.");
    }
}

