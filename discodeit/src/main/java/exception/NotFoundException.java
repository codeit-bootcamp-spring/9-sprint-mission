package exception;
import exception.NotFoundException;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
/*
    에러가 일반 문자열 표현이 아닌 '의미있는' 타입의 형태로 나타내기 위해서 씀
    예외도 하나의 클래스니까 따로 파일분리 // 얘도 여러곳으로 분배 가능하니까?
    이렇게 했을 때 재사용도 가능. 일반 예외처리 판단 기준보다 명확한 점을 찾아내기 위해 사용
    null값은 계속 반복해서 호출되면서 오류 가능성 있다고 판단함
    예외처리 시 try-catch 문법을 상위게에서 main이나 서비스 구동체에서 사용 / 하위계 사용시 의미없음.

    추가적으로 message 뿐 만이 아닌 다른 클래스에서도 사용 가능하게 세분화 가능
    NotFoundException / ChannelNotFoundException **

 */