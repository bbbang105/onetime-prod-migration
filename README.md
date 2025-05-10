# OneTime Backend 🚀

![디스콰이엇1](https://github.com/user-attachments/assets/1f07d38a-643f-4657-8623-bf36e81fbd77)


## 🌐 System Architecture

<img width="1138" alt="25 02 28_에타시간표추출_아키텍처" src="https://github.com/user-attachments/assets/3abc1425-cc3f-4dbe-923b-eeb86fa7720d" />

- 실사용자가 있는 서비스이기에, 블루-그린 무중단 배포 파이프라인을 구축하여 **다운타임을 최소화하고 안정적으로 버전 업데이트**를 진행하고 있습니다.
- **새로운 버전이 릴리즈되기 전 팀 내에서 자체 QA 과정**을 거칩니다. 이를 위해 **테스트 환경을 별도로 운영**하고 있습니다.
- develop 브랜치에 각 feature가 push 또는 merge되면 자동으로 테스트 서버에 배포됩니다. 검수가 완료되면 release 브랜치에 staging한 후, 최종적으로 main 브랜치에 merge하여 운영 서버에 배포하는 구조로 버전을 관리하고 있습니다.

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
