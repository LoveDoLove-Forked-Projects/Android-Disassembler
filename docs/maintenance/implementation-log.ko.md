# 유지보수 구현 로그

이 문서는 공개 저장소에 남길 수 있도록 로컬 절대경로, 사용자명, SDK 설치 경로를 적지 않는다. 파일 경로는 모두 저장소 상대 경로만 사용한다.

## 1차 웨이브 변경표

| 이슈/목표 | 적용한 변경 | 해결 방식 | 관련 파일 | 상태 |
| --- | --- | --- | --- | --- |
| 빌드 기준선 복구 | 테스트 가능 기준선을 다시 세우고 유지보수 문서 세트를 추가 | 이후 이슈 작업 전에 재현, 분류, 검증 흐름을 고정할 수 있게 기반을 만들었다 | `agent.md`, `docs/maintenance/issue-triage.md`, `docs/maintenance/roadmap.md`, `docs/maintenance/anonymization-and-security.md`, `README.md` | 완료 |
| 최신 Android 타게팅 필요 | `compileSdk`와 `targetSdkVersion`을 35로 상향 | Play 최신 요구사항에 맞는 방향으로 기준선을 끌어올렸다. 아직 AGP 자체 업그레이드는 후속 작업이다 | `app/build.gradle`, `gradle.properties` | 완료 |
| 저장소 정책 현대화 착수, 이슈 `#95` 대응 기반 | SAF 기반 선택 경로를 도입하고 `content://` 입력을 앱 내부 import 파일로 저장 | 외부 절대경로 전제를 줄이고, 재시작 이후에도 다시 열 수 있는 URI 권한 흐름을 시작했다 | `app/src/main/java/com/kyhsgeekcode/filechooser/NewFileChooserActivity.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/viewmodel/MainViewModel.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/PermissionUtils.kt` | 진행 중 |
| 구형 저장소 권한 정리 | `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`를 `maxSdkVersion=28`로 제한하고 `requestLegacyExternalStorage` 제거 | 최신 Android에서 불필요한 legacy 외부 저장소 모델 의존성을 줄였다 | `app/src/main/AndroidManifest.xml` | 완료 |
| 패키징 경고 정리 | manifest의 `extractNativeLibs` 선언을 제거하고 Gradle `jniLibs.useLegacyPackaging = false`로 이동 | native packaging 관련 경고를 Gradle 구성 위치로 정리했다 | `app/build.gradle`, `app/src/main/AndroidManifest.xml` | 완료 |
| 이슈 `#670` 앱 런처 미노출 | `MainActivity`에 `android:enabled="true"`를 명시 | 일부 기기/설정에서 런처 진입점이 빠지는 문제를 명시적으로 막았다 | `app/src/main/AndroidManifest.xml` | 완료 |
| 이슈 `#396` Hex 화면 가독성 문제 | Hex view를 16바이트 행, offset 컬럼, ASCII 컬럼 구조로 재구성 | 바이너리 탐색 시 위치와 내용이 동시에 보이도록 바꿨다 | `app/src/main/java/com/kyhsgeekcode/disassembler/ui/components/HexView.kt` | 완료 |
| 이슈 `#348` Disassembly 탭 가로 스크롤 민감도 | 수평 스크롤 책임을 외곽 컨테이너로 옮겼다 | 긴 디스어셈블리 라인에서 좌우 이동이 더 안정적으로 동작하도록 조정했다 | `app/src/main/java/com/kyhsgeekcode/disassembler/ui/tabs/BinaryDisasmTab.kt` | 완료 |
| 프로젝트 경로/파일명 회귀 방지 | 프로젝트 상대경로 계산과 import 파일명 정규화를 pure helper로 분리 | 단위 테스트가 가능하도록 로직을 분리하고 경계 케이스를 줄였다 | `app/src/main/java/com/kyhsgeekcode/disassembler/project/ProjectManager.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/viewmodel/MainViewModel.kt` | 완료 |
| 회귀 테스트 부재 | `ProjectManager`, 저장소 권한, Hex 레이아웃, import 파일명 테스트 추가 | 최소한의 유지보수 안전망을 확보했다 | `app/src/test/java/com/kyhsgeekcode/disassembler/ProjectManagerTest.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/PermissionUtilsTest.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/ui/components/HexViewLayoutTest.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/viewmodel/ImportedFileNameTest.kt` | 완료 |

## 검증

| 검증 항목 | 결과 | 비고 |
| --- | --- | --- |
| `./gradlew help` | 통과 | JDK 17 기준 |
| `./gradlew testDebugUnitTest assembleDebug` | 통과 | JDK 17 기준 |
| 문서 비식별화 점검 | 통과 | 새 문서에는 저장소 상대 경로만 기록 |

## 다음 웨이브 후보

| 우선순위 | 항목 | 이유 |
| --- | --- | --- |
| 높음 | AGP 업그레이드 | `compileSdk 35` 경고를 근본적으로 제거해야 한다 |
| 높음 | `MainActivity`의 남은 legacy picker 경로 정리 | SAF 전환을 앱 전체로 일관되게 맞춰야 한다 |
| 높음 | 오픈 PR `#723`~`#726` 검토 | 이미 제출된 수정안을 흡수하면 유지보수 속도를 높일 수 있다 |
| 중간 | 대용량 파일/RecyclerView/회전 크래시 재현 | 오래된 crash report 계열 이슈 소거에 직접 연결된다 |
