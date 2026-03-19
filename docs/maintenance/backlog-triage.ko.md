# 백로그 1차 분류표

이 문서는 2026-03-19 기준으로 열려 있는 GitHub PR 16개, 이슈 45개를 유지보수 재가동 관점에서 다시 묶은 결과다.  
상태 이름은 `docs/maintenance/issue-triage.md`의 공통 분류를 따른다.

## 요약

| 항목 | 수량 | 판단 |
| --- | --- | --- |
| 오픈 PR | 16 | 실제로 바로 검토할 PR은 소수이고, 상당수는 오래된 의존성 bump PR이다 |
| 오픈 이슈 | 45 | 표면상 45개지만, 실제 작업 묶음은 대략 20~25개 수준으로 수렴한다 |
| 즉시 닫기/대체 가능 후보 | 많음 | `#723`, `#724`, `#725`는 `#728`에 사실상 흡수됐다 |
| 즉시 구현 후보 | 소수 | 릴리스 파이프라인, storage 정책 마감, crash 재현 클러스터가 우선이다 |

## 오픈 PR 분류

| PR | 제목 | 제안 상태 | 판단 | 다음 액션 |
| --- | --- | --- | --- | --- |
| `#728` | Codex/maintenance baseline | 진행 중 | 현재 유지보수 기준선 PR이다 | 리뷰 반영 후 병합 기준으로 사용 |
| `#727` | Simplify release workflow: remove signing, fix deprecated action | `covered-by-open-pr` | `master`가 아니라 `feat/release-apk-workflow`를 대상으로 한 draft 서브 PR이다 | `#726`와 함께 보고 별도 병합 대신 내용만 흡수 검토 |
| `#726` | update deploy workflow | `planned-fast-follow` | 이슈 `#719`와 직접 연결되는 릴리스 워크플로 작업이다 | `master` 기준 최신 Gradle/Android 설정에 맞춰 재검토 |
| `#725` | fix: isolate horizontal scroll in disassembly view (`#348`) | `covered-by-open-pr` | 수정 내용이 `#728`에 이미 들어갔다 | `#728` 병합 후 superseded 처리 |
| `#724` | fix: add offset column and 16-byte rows in HexView (`#396`) | `covered-by-open-pr` | 수정 내용이 `#728`에 이미 들어갔다 | `#728` 병합 후 superseded 처리 |
| `#723` | fix: explicitly enable MainActivity in launcher (`#670`) | `covered-by-open-pr` | 수정 내용이 `#728`에 이미 들어갔다 | `#728` 병합 후 superseded 처리 |
| `#704` | firebase-bom 29.0.3 -> 31.2.3 | `obsolete-or-policy-invalid` | 너무 오래되었고 현재 기준선과 함께 수동 검토하는 편이 안전하다 | 단독 PR 병합 대신 수동 dependency refresh로 대체 |
| `#701` | AGP 7.0.4 -> 7.4.2 | `obsolete-or-policy-invalid` | 현재 목표는 AGP 7.4.2가 아니라 더 최신 Android 기준에 맞는 단계적 업그레이드다 | 단독 병합하지 말고 새 업그레이드 작업으로 대체 |
| `#699` | navigation-compose bump | `obsolete-or-policy-invalid` | alpha 제안이며 너무 오래되었다 | 최신 stable 기준으로 별도 검토 |
| `#695` | appcompat bump | `obsolete-or-policy-invalid` | 단독 bump로는 의미가 약하고 이미 기준선 갱신 흐름에 섞여야 한다 | batch dependency refresh로 대체 |
| `#693` | accompanist-permissions bump | `obsolete-or-policy-invalid` | 제안 버전이 alpha이고 현재 코드 방향과 함께 재판단해야 한다 | storage/power-user 전략과 함께 재검토 |
| `#692` | material bump | `obsolete-or-policy-invalid` | 오래된 자동 bump다 | batch dependency refresh로 대체 |
| `#677` | fragment-ktx bump | `obsolete-or-policy-invalid` | 오래된 자동 bump다 | batch dependency refresh로 대체 |
| `#637` | lifecycle-viewmodel-compose bump | `obsolete-or-policy-invalid` | 오래된 자동 bump다 | batch dependency refresh로 대체 |
| `#615` | constraintlayout bump | `obsolete-or-policy-invalid` | 오래된 자동 bump다 | batch dependency refresh로 대체 |
| `#565` | preference-ktx bump | `obsolete-or-policy-invalid` | 오래된 자동 bump다 | batch dependency refresh로 대체 |

## 오픈 이슈 클러스터

| 작업 묶음 | 관련 이슈 | 제안 상태 | 판단 | 다음 액션 |
| --- | --- | --- | --- | --- |
| 기준선 PR로 이미 다루는 이슈 | `#670`, `#396`, `#348` | `covered-by-open-pr` | 현재 `#728`에서 이미 수정됨 | `#728` 병합 후 정리 |
| 최신 Android storage 정책 | `#95` | `planned-fast-follow` | 핵심 유지보수 항목이며 이미 SAF 전환을 시작했다 | 앱 전체 import/open 경로를 SAF 중심으로 계속 이관 |
| 릴리스 산출물 부재 | `#719` | `planned-fast-follow` | 코드 문제보다 릴리스 파이프라인 문제다 | `#726`와 함께 릴리스 워크플로 정리 |
| 대용량/메모리/RecyclerView 크래시 | `#219`, `#235`, `#442`, `#523` | `planned-fast-follow` | `#728`에서 큰 파일 byte cache 제한과 문자열 검색 결과 상한/stable key를 먼저 넣었다 | `#728` 병합 후 실제 150MB 파일과 긴 문자열 리스트로 재검증하고 나머지 OOM 경로를 분리 |
| 회전/상태 복원 크래시 | `#160` | `covered-by-open-pr` | `#728`에서 Activity 재생성 시 외부 import intent 재처리를 막는 1차 가드를 넣었다 | `#728` 병합 후 실제 회전 회귀를 확인하고 정리 |
| `.so`/ELF/autosetup | `#514`, `#543`, `#576`, `#137` | `covered-by-open-pr` | `#728`에서 64-bit ELF machine type 매핑과 override autosetup 재적용 경로를 먼저 수정했다 | `#728` 병합 후 실제 `.so` 샘플로 재검증하고 남는 parser 문제만 분리 |
| crash report 저신호 묶음 | `#716`, `#672`, `#512`, `#508`, `#507`, `#490`, `#438`, `#376`, `#280` | `needs-repro` | 제목만으로는 원인 판단이 어렵고 재현 자료가 부족하다 | 공통 템플릿으로 추가 정보 요청 후 재현 안 되면 정리 |
| SWF 요청 중복 | `#721`, `#112` | `planned-fast-follow` | 같은 방향의 기능 요청이다 | 최신 요청 `#721` 중심으로 정리하고 하나는 중복 처리 검토 |
| 포맷 확장 요청 | `#120`, `#116`, `#124`, `#129` | `planned-fast-follow` | 유효한 확장 요청이지만 기준선 복구 후가 맞다 | 포맷별 난이도와 수요를 다시 평가 |
| export/저장 유틸 | `#123`, `#159`, `#720` | `planned-fast-follow` | 사용자 가치가 있으나 현재 기준선 복구보다 후순위다 | 파일 출력 경로를 SAF 기준으로 다시 설계한 뒤 착수 |
| 재컴파일/대형 기능 요청 | `#529`, `#706` | `obsolete-or-policy-invalid` | 유지보수 범위를 넘어서는 별도 제품 수준 요구에 가깝다 | 현재 유지보수 스코프에서는 보류 또는 종료 후보 |
| 모호한 기능 요청 | `#717`, `#710`, `#596`, `#582`, `#532`, `#491`, `#425`, `#162`, `#158` | `obsolete-or-policy-invalid` | 설명이 너무 넓거나 현재 제품 방향과 맞지 않는 항목이 많다 | 구체화 요청 후 근거 없으면 정리 |
| 구형 Android 지원 | `#221` | `obsolete-or-policy-invalid` | 현재 목표는 최신 Play 요구사항과 최신 Android 대응이다 | 지원 범위를 현대화 방향으로 명확히 고정 |
| 하이라이터 개선 | `#97` | `planned-fast-follow` | 품질 개선 항목으로는 타당하다 | 기준선 복구 후 UI/텍스트 렌더링 개선 트랙으로 이동 |

## 실제 우선순위

| 우선순위 | 항목 | 근거 |
| --- | --- | --- |
| 1 | `#728` 병합 가능 수준까지 정리 | 현재 기준선 PR이 병목이다 |
| 2 | `#719` + `#726` 릴리스 파이프라인 정리 | 최신 빌드를 배포할 수 있어야 이슈 종료도 설득력이 생긴다 |
| 3 | `#95` storage 정책 마감 | 최신 Android 대응의 핵심이다 |
| 4 | 메모리/회전 crash 클러스터 재현 | 오래된 crash report를 실질 작업 묶음으로 줄일 수 있다 |
| 5 | 포맷 확장 요청 재정렬 | 실제 유지보수 범위와 별도 연구 과제를 나눠야 한다 |

## 닫기 전 체크 규칙

| 상황 | 원칙 |
| --- | --- |
| 이미 `#728`에 포함된 이슈/PR | `#728` 병합 후 superseded로 정리 |
| 오래된 dependabot PR | 그대로 병합하지 않고 새 업그레이드 작업으로 대체 |
| crash report | 재현 정보가 없으면 `needs-repro` 코멘트를 먼저 남긴다 |
| 기능 요청 | 유지보수 핵심 범위와 별도 제품 수준 요청을 분리한다 |
