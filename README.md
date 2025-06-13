# OneTime Backend 🚀

![디스콰이엇1](https://github.com/user-attachments/assets/1f07d38a-643f-4657-8623-bf36e81fbd77)


## 🌐 System Architecture

<img width="983" alt="250529_아키텍처" src="https://github.com/user-attachments/assets/9c95e15d-e763-40ba-ab46-d1eab5c9acb7" />

## 🔒 Rules

### Branch

- 이슈 기반 브랜치 생성
  - `ex) feature/#4/login`
- `main branch`
  - 현재 서비스 중인 브랜치
  - release 브랜치에서 검수가 완료된 소스 코드 병합
  - PR이 병합된 경우 prod-cicd.yml 스크립트 실행
- `release branch`
  - 검수 중인 브랜치로 Staging 환경 역할
  - `release/v*` 형식의 브랜치 이름 사용
  - 검수 완료 후 main 브랜치로 병합, 이후 브랜치 제거
- `develop branch`
  - 개발 중인 기능 병합 브랜치
  - 배포 단위 기능 모두 병합 시 release 브랜치로 분기
  - PR 병합 또는 코드 푸시 시 test-cicd.yml 스크립트 실행
- `hotfix 브랜치`
  - 배포 후 긴급 수정 시 사용
