# 🍃 Back-end Repository
대학생 창업 도모 서비스 Light Up 프로젝트의 백엔드 저장소입니다. 

## 💻 Member
|  베가   |   수   |                현승                 |
|:-----:|:-----:|:---------------------------------:|
| [배현우] | [최경수] | [정현승] |
| [팀장] | [팀원] | [팀원] |

## 📁 디렉토리 구조
```bash
├─.github
│  └─PULL_REQUEST_TEMPLATE
│  └─ISSUE_TEMPLATE
├─README.md
└─src
    ├─main
    │
    └─test
    │
    └─...
            
```

## 🌳 Git branch 전략 및 PR 규칙
```bash
├─main
│  └─hotfix/긴급패치#1
├─develop
│  └─feature/기능#1 #(각자의 로컬에서 develop 브랜치에서 파생)
│  └─feature/기능#2 #(각자의 로컬에서 develop 브랜치에서 파생)
``` 

- `main` : 배포용 브랜치
- `develop` : 개발용 통합 브랜치
- `feature/기능명` : 개별 기능 개발용 브랜치
- `hotfix/패치명` : 긴급 버그 수정용 브랜치

1. 모든 기능 개발은 `develop` 브랜치에서 `feature/기능명` 브랜치를 생성해 작업합니다.
2. 작업 완료 후, `develop` 브랜치로 Pull Request를 생성합니다.
3. 긴급 버그 수정은 `main` 브랜치에서 `hotfix/패치명` 브랜치를 생성하여 수정 후 `main` 과 `develop` 브랜치에 모두 merge합니다.
4. 팀원중 1명 이상의 approve를 받으면, pr을 머지하고 해당 pr을 생성한 브랜치는 삭제합니다.

## 🔖 Commit 컨벤션
모든 커밋 메세지는 아래와 같은 규칙을 따릅니다:

| Message  | 설명             |
|:--------:|:---------------|
|   feat   | 새로운 기능 추가      |
|   fix    | 버그 수정          |
| refactor | 리팩토링(기능 변화 없음) |
|   docs   | 문서 수정          |
| comment  | 주석 추가 및 변경     |
|   test   | 테스트 코드 추가      |
|  rename  | 파일 혹은 폴더명 수정   |
|  remove  | 파일 혹은 폴더 삭제    |
|  chore   | 기타 변경사항        |

## 📝 Server 아키텍처
<img width="2000" height="1125" alt="프로젝트 아키텍처" src="https://github.com/user-attachments/assets/4a80fd31-f6ae-4b0e-af18-63941c3d4282" />

