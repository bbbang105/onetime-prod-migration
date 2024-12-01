# OneTime Backend 🚀

## 🌐 System Architecture

<img width="925" alt="image" src="https://github.com/user-attachments/assets/12ebde7d-4623-4af8-9417-7d968c91c5fa">

## 🧱 ERD

<img width="1085" alt="원타임_ERD" src="https://github.com/user-attachments/assets/ae6386ad-4ef7-4196-bc38-fde5bdfc4591">


## 📄 API Documentation

[📝 REST Docs + Swagger](https://onetime-test.store/swagger-ui/index.html#/)

## 🔒 Rules

### Branch

- 생성한 이슈에 따라서 브랜치 생성 `Ex) feature/#4/login`
- `main branch` : 개발 최종 완료 시 merge
- `develop branch` : 배포 서버용
- `test branch` : 테스트 서버용
- `feature branch` : 각 새로운 기능 개발
- `hotfix branch` : 배포 이후 긴급 수정

### **Commit Message**

- 이슈 번호 붙여서 커밋 `Ex) #4 [feat] : 로그인 기능을 추가한다`
- Body는 추가 설명 필요하면 사용

| ***작업태그*** | ***내용*** |
| --- | --- |
| **feat** | 새로운 기능 추가 / 일부 코드 추가 / 일부 코드 수정 (리팩토링과 구분) / 디자인 요소 수정 |
| **fix** | 버그 수정 |
| **refactor** | 코드 리팩토링 |
| **style** | 코드 의미에 영향을 주지 않는 변경사항 (코드 포맷팅, 오타 수정, 변수명 변경, 에셋 추가) |
| **chore** | 빌드 부분 혹은 패키지 매니저 수정 사항 / 파일 이름 변경 및 위치 변경 / 파일 삭제 |
| **docs** | 문서 추가 및 수정 |
| **rename** | 패키지 혹은 폴더명, 클래스명 수정 (단독으로 시행하였을 시) |
| **remove** | 패키지 혹은 폴더, 클래스를 삭제하였을 때 (단독으로 시행하였을 시) |

### Naming

- **패키지명** : 한 단어 소문자 사용 `Ex) service`
- **클래스명** : 파스칼 케이스 사용 `Ex) JwtUtil`
- **메서드명** : 카멜 케이스 사용, 동사로 시작  `Ex) getUserScraps`
- **변수명** : 카멜 케이스 사용 `Ex) jwtToken`
- **상수명** : 대문자 사용 `Ex) EXPIRATION_TIME`
- **컬럼명** : 스네이크 케이스 사용 `Ex) user_id`


### API Response

```json
{
  "code": "201",
  "message": "이벤트 생성에 성공했습니다.",
  "payload": {
    "event_id": "5e35b658-ee4b-4c52-98dc-94b79f0e64c9"
  },
  "is_success": true
}
```

- `is_success` : 성공 여부
- `code` : 성공 코드, HTTP 상태 코드와 동일
- `message` : 성공 메세지
- `payload` : 데이터가 들어가는 곳
