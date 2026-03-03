#!/usr/bin/env python3
"""
디스코드잇 API 자동화 테스트 스크립트

사용법:
    python test_api_automation.py

요구사항:
    pip install requests
"""

import requests
import json
from datetime import datetime
from typing import Dict, Optional
import sys


class DiscodeitAPITester:
    """디스코드잇 API 테스트 클래스"""

    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({"Content-Type": "application/json"})

        # 테스트 데이터 저장
        self.user_id: Optional[str] = None
        self.channel_id: Optional[str] = None
        self.message_id: Optional[str] = None
        self.read_status_id: Optional[str] = None
        self.binary_content_id: Optional[str] = None

        # 테스트 결과
        self.passed = 0
        self.failed = 0
        self.errors = []

    def print_header(self, title: str):
        """헤더 출력"""
        print("\n" + "=" * 60)
        print(f"  {title}")
        print("=" * 60)

    def print_test(self, name: str, passed: bool, detail: str = ""):
        """테스트 결과 출력"""
        status = "✓ PASS" if passed else "✗ FAIL"
        color = "\033[92m" if passed else "\033[91m"
        reset = "\033[0m"

        print(f"\n{color}[{status}]{reset} {name}")
        if detail:
            print(f"       {detail}")

        if passed:
            self.passed += 1
        else:
            self.failed += 1
            self.errors.append(name)

    def test_user_create(self):
        """사용자 등록 테스트"""
        try:
            response = self.session.post(
                f"{self.base_url}/users",
                json={
                    "username": "apitest_user",
                    "email": "apitest@example.com",
                    "password": "testpass123"
                }
            )

            if response.status_code == 201:
                data = response.json()
                self.user_id = data["id"]
                self.print_test(
                    "사용자 등록",
                    True,
                    f"사용자 ID: {self.user_id}"
                )
            else:
                self.print_test(
                    "사용자 등록",
                    False,
                    f"Status: {response.status_code}, {response.text}"
                )
        except Exception as e:
            self.print_test("사용자 등록", False, str(e))

    def test_login(self):
        """로그인 테스트"""
        try:
            response = self.session.post(
                f"{self.base_url}/auth/login",
                json={
                    "username": "apitest_user",
                    "password": "testpass123"
                }
            )

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "로그인",
                    True,
                    f"로그인 사용자: {data['username']}"
                )
            else:
                self.print_test(
                    "로그인",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("로그인", False, str(e))

    def test_user_list(self):
        """전체 사용자 조회 테스트"""
        try:
            response = self.session.get(f"{self.base_url}/users")

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "전체 사용자 조회",
                    True,
                    f"사용자 수: {len(data)}"
                )
            else:
                self.print_test(
                    "전체 사용자 조회",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("전체 사용자 조회", False, str(e))

    def test_user_get(self):
        """특정 사용자 조회 테스트"""
        if not self.user_id:
            self.print_test("특정 사용자 조회", False, "사용자 ID 없음")
            return

        try:
            response = self.session.get(f"{self.base_url}/users/{self.user_id}")

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "특정 사용자 조회",
                    True,
                    f"사용자명: {data['username']}"
                )
            else:
                self.print_test(
                    "특정 사용자 조회",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("특정 사용자 조회", False, str(e))

    def test_user_update(self):
        """사용자 정보 수정 테스트"""
        if not self.user_id:
            self.print_test("사용자 정보 수정", False, "사용자 ID 없음")
            return

        try:
            response = self.session.put(
                f"{self.base_url}/users/{self.user_id}",
                json={
                    "username": "updated_apitest",
                    "email": "updated_apitest@example.com",
                    "password": "newpass456"
                }
            )

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "사용자 정보 수정",
                    True,
                    f"수정된 사용자명: {data['username']}"
                )
            else:
                self.print_test(
                    "사용자 정보 수정",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("사용자 정보 수정", False, str(e))

    def test_channel_create_public(self):
        """공개 채널 생성 테스트"""
        try:
            response = self.session.post(
                f"{self.base_url}/channels/public",
                json={
                    "name": "자동화 테스트 채널",
                    "description": "Python 자동화 스크립트로 생성된 채널"
                }
            )

            if response.status_code == 201:
                data = response.json()
                self.channel_id = data["id"]
                self.print_test(
                    "공개 채널 생성",
                    True,
                    f"채널 ID: {self.channel_id}"
                )
            else:
                self.print_test(
                    "공개 채널 생성",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("공개 채널 생성", False, str(e))

    def test_channel_list(self):
        """사용자별 채널 목록 조회 테스트"""
        if not self.user_id:
            self.print_test("채널 목록 조회", False, "사용자 ID 없음")
            return

        try:
            response = self.session.get(
                f"{self.base_url}/channels",
                params={"userId": self.user_id}
            )

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "채널 목록 조회",
                    True,
                    f"채널 수: {len(data)}"
                )
            else:
                self.print_test(
                    "채널 목록 조회",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("채널 목록 조회", False, str(e))

    def test_channel_update(self):
        """채널 정보 수정 테스트"""
        if not self.channel_id:
            self.print_test("채널 정보 수정", False, "채널 ID 없음")
            return

        try:
            response = self.session.put(
                f"{self.base_url}/channels/{self.channel_id}",
                json={
                    "name": "수정된 채널명",
                    "description": "수정된 설명입니다"
                }
            )

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "채널 정보 수정",
                    True,
                    f"수정된 채널명: {data['name']}"
                )
            else:
                self.print_test(
                    "채널 정보 수정",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("채널 정보 수정", False, str(e))

    def test_message_create(self):
        """메시지 전송 테스트"""
        if not self.channel_id or not self.user_id:
            self.print_test("메시지 전송", False, "채널 ID 또는 사용자 ID 없음")
            return

        try:
            response = self.session.post(
                f"{self.base_url}/messages",
                json={
                    "content": "자동화 테스트 메시지입니다",
                    "channelId": self.channel_id,
                    "authorId": self.user_id
                }
            )

            if response.status_code == 201:
                data = response.json()
                self.message_id = data["id"]
                self.print_test(
                    "메시지 전송",
                    True,
                    f"메시지 ID: {self.message_id}"
                )
            else:
                self.print_test(
                    "메시지 전송",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("메시지 전송", False, str(e))

    def test_message_list(self):
        """채널별 메시지 조회 테스트"""
        if not self.channel_id:
            self.print_test("메시지 목록 조회", False, "채널 ID 없음")
            return

        try:
            response = self.session.get(
                f"{self.base_url}/messages",
                params={"channelId": self.channel_id}
            )

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "메시지 목록 조회",
                    True,
                    f"메시지 수: {len(data)}"
                )
            else:
                self.print_test(
                    "메시지 목록 조회",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("메시지 목록 조회", False, str(e))

    def test_message_update(self):
        """메시지 수정 테스트"""
        if not self.message_id:
            self.print_test("메시지 수정", False, "메시지 ID 없음")
            return

        try:
            response = self.session.put(
                f"{self.base_url}/messages/{self.message_id}",
                json={"content": "수정된 메시지 내용"}
            )

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "메시지 수정",
                    True,
                    f"수정된 내용: {data['content']}"
                )
            else:
                self.print_test(
                    "메시지 수정",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("메시지 수정", False, str(e))

    def test_user_status_update(self):
        """온라인 상태 업데이트 테스트"""
        if not self.user_id:
            self.print_test("온라인 상태 업데이트", False, "사용자 ID 없음")
            return

        try:
            response = self.session.put(
                f"{self.base_url}/users/{self.user_id}/status",
                json={"lastActiveAt": datetime.utcnow().isoformat() + "Z"}
            )

            if response.status_code == 200:
                data = response.json()
                self.print_test(
                    "온라인 상태 업데이트",
                    True,
                    f"온라인: {data.get('online', 'N/A')}"
                )
            else:
                self.print_test(
                    "온라인 상태 업데이트",
                    False,
                    f"Status: {response.status_code}"
                )
        except Exception as e:
            self.print_test("온라인 상태 업데이트", False, str(e))

    def print_summary(self):
        """테스트 결과 요약 출력"""
        self.print_header("테스트 결과 요약")

        total = self.passed + self.failed
        success_rate = (self.passed / total * 100) if total > 0 else 0

        print(f"\n총 테스트: {total}")
        print(f"성공: \033[92m{self.passed}\033[0m")
        print(f"실패: \033[91m{self.failed}\033[0m")
        print(f"성공률: {success_rate:.1f}%")

        if self.errors:
            print("\n실패한 테스트:")
            for error in self.errors:
                print(f"  - {error}")

        print("\n생성된 리소스:")
        print(f"  - 사용자 ID: {self.user_id}")
        print(f"  - 채널 ID: {self.channel_id}")
        print(f"  - 메시지 ID: {self.message_id}")

        print("=" * 60)

        return self.failed == 0

    def run_all_tests(self):
        """모든 테스트 실행"""
        self.print_header("디스코드잇 API 자동화 테스트 시작")
        print(f"Base URL: {self.base_url}")
        print(f"시작 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")

        # 1. 사용자 관리
        self.print_header("1. 사용자 관리")
        self.test_user_create()
        self.test_login()
        self.test_user_list()
        self.test_user_get()
        self.test_user_update()

        # 2. 채널 관리
        self.print_header("2. 채널 관리")
        self.test_channel_create_public()
        self.test_channel_list()
        self.test_channel_update()

        # 3. 메시지 관리
        self.print_header("3. 메시지 관리")
        self.test_message_create()
        self.test_message_list()
        self.test_message_update()

        # 4. 온라인 상태
        self.print_header("4. 온라인 상태")
        self.test_user_status_update()

        # 결과 요약
        success = self.print_summary()

        return 0 if success else 1


def main():
    """메인 함수"""
    tester = DiscodeitAPITester()

    try:
        exit_code = tester.run_all_tests()
        sys.exit(exit_code)
    except KeyboardInterrupt:
        print("\n\n테스트가 중단되었습니다.")
        sys.exit(1)
    except Exception as e:
        print(f"\n\n예상치 못한 오류 발생: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
