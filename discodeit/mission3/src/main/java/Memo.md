1.서로의 의존성을 만듬.
2.UserStatus -> 사용자별 마지막으로 확인된 접속시간을 표현하는 모델.온라인상태확인->마지막 접속시간을 기준으로 현재 로그인한 유저를 판단할 수 있는 메소드-> 
    Instant를 활용해 boolean메소드를 만들고 확인.->repository구성->메소드는?->읽음메서드와 아님메서드를 구분?

3.ReadStatus->사용자가 채널별 마지막으로 메시지를 읽은 시간을 표현하는 도메인모델..



UserService고도화
DTO를 활용하여 파라미터 그룹화.
DTO 파라미터그룹화->두개의 엔티티 파라미터를 동시에 관리가 가능?
선택적으로 프로필 이미지 그룹화?-> 그러면 등록 파라미터 프로필 이미지 등록 파라미터를 처음엔 따로 두고 둘이 같이 관리하는 record클래스를 만들어서 파라미터나 타입으로 넘겨줌.
중복 검증 로직은 service에서. repository가아닌.
binaryontent->프로필 이미지 userservice에서 관리?->
ifpresent 파일이 잇으면 주고 없으면 말고..
orelsethrow 파일이잇으면 주고 없으면 예외발생

즉 UserStatus/binarycontent는 누가 먼저 삭제되어도 상관이없음->but user가 먼저 삭제되면 나머진 유령데이터가됌

AuthService-> login
    username,password와 일치하는 유저가 있는지 확인
일치하면 유저정보반환/없으면 예외발생
dto를 이용하여 파라미터 그룹화
userrepository의존성주입
따로 Login entity를 만들어야하나? 아니 userrepotisry를 의존성 주입 받고 userservice를 구현받으면되지않을까"라고 하기엔 . 메서드 오버라이딩은 다 못함.
그럼 그냥 의존성 주입만?
그래도 가능할듯? 근데 login하려면 user객체를 알아야함.
User를 알아야 그 user를 loginuser로 만들지.어떻게? 이미 user객체가잇음 그걸 가져올수가있나?
dto로 이름과 비밀번호 이메일을 받고 그걸 userrepository에 잇는거를 가져와서 비교하고 일치 확인 로직을 구성한다?
stream으로 거르고 걸러서 첫번째껄 찾은다음 최근 배운 orelsethrow로 예외를 만든다.근데 
메인에서 호출을 어떻게하지-> 의존성 주입은 userrepository를 받앗고 new로 새로운 객체를 만든다는건 스프링을 배우기 전방식 그럼 레포지토리로
만든다면?


channel 고도화 \
-> private와 pubilc을 구분하는 메서드 구현
   

findAll->finddto사용할수있을듯->특정 user가 볼수잇는 channl목록을 조회하도록 조ㅗ히조건 추가하고 메소드명을 변경?
업데이트를 함 private는 제외 if문으로 예외 처리
Channel channel = channelRepository.findid를 해서 channel이 찾은 채널을 참조하게끔하고 그 체널을 업데이트 시키면됌.
어떻게? channel.


message->create부분은 선택적으로 이미지첨부가능
-> 이번에 배운점 dto도 하나의 클래스 즉 필드,메서드 구현이 가능함 만약 선태적으로 할거면 record선언후 메서드로 일부분 null로 해서 가능