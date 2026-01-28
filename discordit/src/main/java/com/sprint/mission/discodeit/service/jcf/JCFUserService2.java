//package service.jcf;
//
//import entity.User;
//import service.UserService;
//import java.util.ArrayList;
//import java.util.List;
//
//public class JCFUserService2 {
//    private final List<User> data = new ArrayList<>();
//
//
//    public User create(String displayName, String email, String phoneNumber) {
//        //try안에 있는 오류를 catch가 잡음
//        try {
//            User user = new User(displayName, email, phoneNumber);
//            data.add(user);
//            System.out.println("유저 등록 성공");
//            return user;
//
//        } catch (Exception e) {
//            System.out.println("유저 등록 실패");
//             return null;
//        }
//
//    }
///* UserService에서 갖고온 상자를 create라는 상자(메서드)안에 내용을 체워준다.
//1.입력한 값으로 새유저 객체를 만들고 유저 객체를 담는 data list에 추가한다
//메서드 수행 반환값으로 새로 등록한 유저객체를 반환한다
//수행중 에러가 발생시 catch부분 안에서 null값을 반환한다     /등록
//*/
//
//
//
//
//    public User find(String id) {
//        return data.stream()
//                .filter(user -> user.getId().toString().equals(id))
//                .findFirst()
//                .orElse(null);
//    }
//    /* 4. 유저객체들이 담겨있는 list에서 매개변수로 받은 id를 가진 user객체중 가장 첫번째
//    유저객체를 반환한다. 이 조건에 맞는 user가 없으면 null을 반환한다
//    */
//
//
//    public List<User> findAll() {return new ArrayList<>(data);
//    }
//    // 6 user객체가 담겨진 data list를 반환한다
//    /* ArrayList쓴 이유 외부에서 함부로 건드리지 못하게 보호하고  외부 리스트 항목을 추가하거나 삭제해도
//    관리하는 원본 list에 영향을 주지 않을려고 했다
//    */
//
//    /* 9 update 메서드에 수정할 유저의 이름과 새이름을 매개변수로 입력받는다
//    그리고 모든 유저정보가 들어있는 data list를 for문 안에서 돌린다
//     */
//
//    public void update(String before, String after) {
//        for (int i = 0; i < data.size(); i++) {
//
//            if (data.get(i).getDisplayName().equals(before)) {
//                //10 만약 data list의 i번째 유저의 이름이 입력변수 before와 똑같다면 수정전 이름을 출력한다
//
//                System.out.println("수정전 이름 : " + data.get(i).getDisplayName());
//
//                data.get(i).setDisplayName(after);
//                //11 data list에 i번째 유저의 이름을 입력변수 after로 덮어씌운다
//                System.out.println("수정후 이름 : " + data.get(i).getDisplayName());
//                //12 수정한 이름이 제대로 적용 되었는지 한번 더 data list의 i번째 유저의 이름을 출력한다
//            }
//
//        }
//    }
//
//
//    public boolean delete(String id) {
//        return data.removeIf(user -> user.getId().toString().equals(id));
//    } //14 모든 유저객체가 담겨있는 data list의 user객체중 매개변수로 입력받은 id값을 가진
//      // user가 있다면 삭제한다
//
//}