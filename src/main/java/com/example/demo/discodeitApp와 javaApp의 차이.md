일단 discodeitApp가 생긴 이유부터 알아야 함. 
이유는 javaApp로도 실행은 된다. 근데 객체생성권한을 사람이 아니라 Spring에게 넘기려고 생긴거다.

JavaApp 구조의 특징
1. 객체 생성과 연결을 개발자가 직접 수행함.
2. new키워드가 많음.    
3. 구현체가 바뀌면 main도 수정해야함.
4. 의존성이 강하게 결합됨.
= 객체 생성의 책임이 main메소드에 있다.

DiscodeitApp(스프링 방식) 구조의 특징
1. 객체 생성과 연결을 Spring이 수행함.
2. new키워드를 사용하지 않음.
3. 구현체 변경시에 main코드 수정 안해도 괜찮음.
4. 의존성이 느슨함.
= 객체 생성 책임이 Spring에게 있다.


IoC Container - 객체 생성과 관리를 담당하는 Spring의 핵심구성요소
Bean - Spring이 관리하는 객체가 Bean
Dependency Injection - 객체가 직접 의존객체를 생성 않고 Spring(외부)에서 주입받는 것


DiscodeitApp의 역할
1. Spring 애플리케이션 실행
2. IoC Container 생성
3. Bean 조회
4. 테스트 코드 실행


===== JavaApp는 사람이 객체를 조립하는거고 DiscodeitApp는 Spring이 객체를 조립함 =====