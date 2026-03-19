# 유지보수 구현 로그

이 문서는 공개 저장소에 남길 수 있도록 로컬 절대경로, 사용자명, SDK 설치 경로를 적지 않는다. 파일 경로는 모두 저장소 상대 경로만 사용한다.

## 1차 웨이브 변경표

| 이슈/목표 | 적용한 변경 | 해결 방식 | 관련 파일 | 상태 |
| --- | --- | --- | --- | --- |
| 빌드 기준선 복구 | 테스트 가능 기준선을 다시 세우고 유지보수 문서 세트를 추가 | 이후 이슈 작업 전에 재현, 분류, 검증 흐름을 고정할 수 있게 기반을 만들었다 | `agent.md`, `docs/maintenance/issue-triage.md`, `docs/maintenance/roadmap.md`, `docs/maintenance/anonymization-and-security.md`, `README.md` | 완료 |
| 최신 Android 타게팅 필요 | `compileSdk`와 `targetSdkVersion`을 35로 상향 | Play 최신 요구사항에 맞는 방향으로 기준선을 끌어올렸다. 아직 AGP 자체 업그레이드는 후속 작업이다 | `app/build.gradle`, `gradle.properties` | 완료 |
| API 35 대비 AGP/Gradle 뒤처짐 | AGP를 `8.6.0`, Gradle wrapper를 `8.7`로 올리고 `android.suppressUnsupportedCompileSdk`를 제거했다 | API 35 지원을 억제 옵션이 아니라 공식 최소 호환 버전 조합으로 맞췄고, `buildConfig` 설정도 모듈 `buildFeatures`로 옮겨 AGP 9.0 경고 하나를 추가 정리했다 | `build.gradle`, `gradle/wrapper/gradle-wrapper.properties`, `gradle.properties`, `app/build.gradle` | 완료 |
| Kotlin plugin deprecation 경고 | Kotlin Gradle plugin을 `1.9.24`, Compose compiler extension을 `1.5.14`로 올리고 stdlib 버전을 정렬했다 | AGP 업그레이드 뒤 남아 있던 `Convention` deprecation 경고를 Kotlin plugin 쪽에서 제거했고, `--warning-mode all` 기준으로 우리 빌드 스크립트에서 드러나는 Gradle 9 경고를 없앴다 | `build.gradle`, `app/build.gradle` | 완료 |
| 이슈 `#719`, PR `#726` 릴리스 경로 부재 | tag push와 manual dispatch를 지원하는 `release.yml`을 추가하고 기존 CI를 최신 action 버전으로 정리 | 최신 브랜치에서 unsigned/signed APK를 GitHub Actions로 다시 만들고 배포할 수 있는 운영 경로를 복구했다 | `.github/workflows/ci.yml`, `.github/workflows/release.yml` | 완료 |
| 릴리스 workflow의 branch push 오작동 | `release.yml`의 트리거를 `push tags`에서 `release published`와 `workflow_dispatch`로 바꿨다 | 브랜치 push 때 0초 실패하는 가짜 release run을 없애고, 최신판은 CI debug artifact로, 정식 배포는 GitHub Release 이벤트로 역할을 분리했다 | `.github/workflows/release.yml`, `.github/workflows/ci.yml` | 완료 |
| 저장소 정책 현대화 착수, 이슈 `#95` 대응 기반 | SAF 기반 선택 경로를 도입하고 `content://` 입력을 앱 내부 import 파일로 저장 | 외부 절대경로 전제를 줄이고, 재시작 이후에도 다시 열 수 있는 URI 권한 흐름을 시작했다 | `app/src/main/java/com/kyhsgeekcode/filechooser/NewFileChooserActivity.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/viewmodel/MainViewModel.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/PermissionUtils.kt` | 진행 중 |
| 이슈 `#95` 외부 문서 인텐트 권한 누락 | 앱이 `ACTION_VIEW`나 `EXTRA_STREAM`으로 열린 경우에도 persistable grant 가능 여부를 계산하고 `content://` URI 권한을 선제적으로 유지 | SAF picker 밖에서 들어온 문서도 같은 storage 정책 흐름으로 흡수해서, provider가 허용하는 경우 앱 재실행 이후에도 접근이 끊길 가능성을 줄였다 | `app/src/main/java/com/kyhsgeekcode/disassembler/MainActivity.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/PermissionUtils.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/PermissionUtilsTest.kt` | 완료 |
| 프로젝트 경계의 raw path 의존 | `ProjectModel`에 source helper를 추가하고 프로젝트 트리/리스트/저장소가 helper를 사용하도록 교체 | `sourceFilePath` 문자열 조합을 한곳으로 모아서 이후 `sourceDescriptor` 중심 구조로 더 옮기기 쉬운 상태를 만들었다 | `app/src/main/java/com/kyhsgeekcode/disassembler/project/models/ProjectModel.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/ui/FileDrawerTree.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/FileDrawerListAdapter.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/FileDrawerListItem.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/project/ProjectDataStorage.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/viewmodel/MainViewModel.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/ProjectManagerTest.kt` | 완료 |
| broad storage 우회 경로 정리 | 더 이상 쓰지 않는 legacy picker 코드와 Android 11+의 `MANAGE_ALL_FILES_ACCESS` 유도 설정을 제거 | SAF 기반 경로를 기본 흐름으로 고정하고, 정책상 불리한 all-files access 진입점을 줄였다 | `app/src/main/java/com/kyhsgeekcode/disassembler/MainActivity.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/preference/SettingsFragment.kt`, `app/src/main/res/xml/pref_settings.xml`, `app/src/main/res/xml-v30/pref_settings.xml`, `app/src/main/res/values/array.xml` | 완료 |
| 프로젝트 저장 모델 확장 | `sourceDescriptor`를 추가하고 새 프로젝트 생성 시 원본 유형을 함께 기록 | 기존 `sourceFilePath` 호환성은 유지하면서 이후 `Uri | file path | cache` 구분으로 넘어갈 발판을 만들었다 | `app/src/main/java/com/kyhsgeekcode/disassembler/project/models/ProjectModel.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/project/ProjectManager.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/viewmodel/MainViewModel.kt` | 완료 |
| SAF와 파워유저 경로 분리 | 기본 import는 SAF 직행으로 두고, settings에서 power-user mode를 켰을 때만 `Advanced import`를 노출 | 기본 사용자 경로와 비정책적/고급 경로를 같은 UI에서 섞지 않고 분리했다 | `app/src/main/java/com/kyhsgeekcode/disassembler/importing/ImportEntryPointCatalog.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/preference/PowerUserModeSettings.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/ui/MainTab.kt`, `app/src/main/java/com/kyhsgeekcode/filechooser/NewFileChooserActivity.kt`, `app/src/main/java/com/kyhsgeekcode/filechooser/NewFileChooserAdapter.kt`, `app/src/main/java/com/kyhsgeekcode/filechooser/model/FileItem.kt`, `app/src/main/res/xml/pref_settings.xml`, `app/src/main/res/xml-v30/pref_settings.xml` | 완료 |
| 파워유저 advanced source 과노출 | power-user mode 아래에 파일시스템, 설치 앱, 리서치 도구 토글을 분리하고 chooser 루트 항목을 설정값에 따라 필터링 | 고급 사용자는 필요한 위험 경로만 선택적으로 켤 수 있고, 기본 power-user mode도 최소 권한/최소 노출 원칙으로 운영할 수 있게 했다 | `app/src/main/java/com/kyhsgeekcode/disassembler/importing/AdvancedImportSourceCatalog.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/preference/PowerUserModeSettings.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/preference/SettingsFragment.kt`, `app/src/main/java/com/kyhsgeekcode/filechooser/NewFileChooserActivity.kt`, `app/src/main/java/com/kyhsgeekcode/filechooser/NewFileChooserAdapter.kt`, `app/src/main/java/com/kyhsgeekcode/filechooser/model/FileItem.kt`, `app/src/main/res/xml/pref_settings.xml`, `app/src/main/res/xml-v30/pref_settings.xml`, `app/src/main/res/values/strings.xml`, `app/src/test/java/com/kyhsgeekcode/disassembler/importing/AdvancedImportSourceCatalogTest.kt` | 완료 |
| 구형 저장소 권한 정리 | `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`를 `maxSdkVersion=28`로 제한하고 `requestLegacyExternalStorage` 제거 | 최신 Android에서 불필요한 legacy 외부 저장소 모델 의존성을 줄였다 | `app/src/main/AndroidManifest.xml` | 완료 |
| 패키징 경고 정리 | manifest의 `extractNativeLibs` 선언을 제거하고 Gradle `jniLibs.useLegacyPackaging = false`로 이동 | native packaging 관련 경고를 Gradle 구성 위치로 정리했다 | `app/build.gradle`, `app/src/main/AndroidManifest.xml` | 완료 |
| 이슈 `#670` 앱 런처 미노출 | `MainActivity`에 `android:enabled="true"`를 명시 | 일부 기기/설정에서 런처 진입점이 빠지는 문제를 명시적으로 막았다 | `app/src/main/AndroidManifest.xml` | 완료 |
| 이슈 `#396` Hex 화면 가독성 문제 | Hex view를 16바이트 행, offset 컬럼, ASCII 컬럼 구조로 재구성 | 바이너리 탐색 시 위치와 내용이 동시에 보이도록 바꿨다 | `app/src/main/java/com/kyhsgeekcode/disassembler/ui/components/HexView.kt` | 완료 |
| 이슈 `#348` Disassembly 탭 가로 스크롤 민감도 | 수평 스크롤 책임을 외곽 컨테이너로 옮겼다 | 긴 디스어셈블리 라인에서 좌우 이동이 더 안정적으로 동작하도록 조정했다 | `app/src/main/java/com/kyhsgeekcode/disassembler/ui/tabs/BinaryDisasmTab.kt` | 완료 |
| 이슈 `#160` 회전 시 재진입 크래시 후보 | `MainActivity`가 재생성될 때 기존 `ACTION_VIEW`/`EXTRA_STREAM` intent를 다시 처리하지 않도록 가드하고, 새 외부 인텐트는 `onNewIntent`에서만 처리하도록 분리 | 회전 같은 configuration change에서 같은 import 요청이 중복 실행되는 경로를 차단해 상태 복원 중 재import/중복 초기화 가능성을 줄였다 | `app/src/main/java/com/kyhsgeekcode/disassembler/MainActivity.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/MainActivityIntentHandlingTest.kt` | 완료 |
| instrumentation test 기반 부재 | AndroidX instrumentation runner를 설정하고 `MainActivity`가 emulator에서 `RESUMED` 상태까지 올라오는 smoke test와 CI emulator job을 추가 | unit test만으로는 잡히지 않는 startup/regression을 GitHub Actions emulator에서 자동 확인할 수 있는 최소 기반을 만들었다 | `app/build.gradle`, `app/src/androidTest/java/com/kyhsgeekcode/disassembler/MainActivitySmokeTest.kt`, `.github/workflows/ci.yml` | 완료 |
| PR 회귀를 잡는 instrumentation 범위 부족 | Compose UI test 의존성, import entry-point test tag, 회전 재생성 smoke test, standard/power-user mode UI 검증 테스트를 추가 | 현재 PR의 핵심 변경인 회전 재생성 경계와 `Select file`/`Advanced import` 노출 규칙을 emulator에서 직접 검증할 수 있게 했다 | `app/build.gradle`, `app/src/main/java/com/kyhsgeekcode/disassembler/ui/MainTab.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/ui/MainTestTags.kt`, `app/src/androidTest/java/com/kyhsgeekcode/disassembler/MainActivitySmokeTest.kt`, `app/src/androidTest/java/com/kyhsgeekcode/disassembler/MainActivityImportEntryPointStandardModeTest.kt`, `app/src/androidTest/java/com/kyhsgeekcode/disassembler/MainActivityImportEntryPointPowerUserModeTest.kt`, `app/src/androidTest/java/com/kyhsgeekcode/disassembler/PowerUserModePreferenceRule.kt` | 완료 |
| 이슈 `#523`, `#442`, `#235` 대용량 파일/리스트 경계 | 큰 파일은 `ProjectDataStorage` 메모리 캐시에서 제외하고, 문자열 검색 결과는 5,000개로 상한을 두고 stable key로 렌더링하도록 바꿨다 | 150MB 같은 큰 파일을 한 번 열 때 캐시 맵이 오래 붙잡는 메모리 압박을 줄이고, 문자열 탭의 대형 리스트가 계속 불어나면서 Recycler/Lazy list가 불안정해지는 경로를 완화했다 | `app/src/main/java/com/kyhsgeekcode/disassembler/project/ProjectDataStorage.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/ui/components/TableView.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/ui/tabs/StringTab.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/project/ProjectDataStorageCachePolicyTest.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/ui/tabs/StringSearchResultAccumulatorTest.kt` | 완료 |
| 이슈 `#514`, `#543`, `#576` `.so`/ELF 아키텍처와 override autosetup | `x86_64`, `PPC64` ELF machine type을 64-bit disassembly mode로 매핑하고, binary overview에서 수동 설정을 적용하면 disassembly handle을 다시 열도록 바꿨다 | 일부 `.so`가 32-bit mode로 잘못 열리던 경로를 줄이고, `Override autosetup`이 값만 바꾸고 실제 disassembly는 안 바뀌던 문제를 수정했다 | `app/src/main/java/com/kyhsgeekcode/disassembler/models/Architecture.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/ui/tabs/BinaryOverviewTab.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/ui/tabs/BinaryTab.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/models/ArchitectureTest.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/ui/tabs/BinaryManualSetupConfigTest.kt` | 완료 |
| 프로젝트 경로/파일명 회귀 방지 | 프로젝트 상대경로 계산과 import 파일명 정규화를 pure helper로 분리 | 단위 테스트가 가능하도록 로직을 분리하고 경계 케이스를 줄였다 | `app/src/main/java/com/kyhsgeekcode/disassembler/project/ProjectManager.kt`, `app/src/main/java/com/kyhsgeekcode/disassembler/viewmodel/MainViewModel.kt` | 완료 |
| 회귀 테스트 부재 | `ProjectManager`, 저장소 권한, Hex 레이아웃, import 파일명 테스트 추가 | 최소한의 유지보수 안전망을 확보했다 | `app/src/test/java/com/kyhsgeekcode/disassembler/ProjectManagerTest.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/PermissionUtilsTest.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/ui/components/HexViewLayoutTest.kt`, `app/src/test/java/com/kyhsgeekcode/disassembler/viewmodel/ImportedFileNameTest.kt` | 완료 |

## 검증

| 검증 항목 | 결과 | 비고 |
| --- | --- | --- |
| `./gradlew help` | 통과 | JDK 17 기준 |
| `./gradlew testDebugUnitTest assembleDebug` | 통과 | JDK 17 기준 |
| `./gradlew assembleRelease` | 통과 | unsigned release APK 생성 확인 |
| `./gradlew help --warning-mode all` | 부분 정리 | `buildConfig` deprecation은 제거, 남은 Gradle 9 경고는 플러그인 적용 경로에서 발생 |
| `./gradlew testDebugUnitTest assembleDebug assembleRelease --warning-mode all` | 통과 | deprecation 라인 grep 기준 추가 경고 미검출 |
| 문서 비식별화 점검 | 통과 | 새 문서에는 저장소 상대 경로만 기록 |
| 파워유저 import entry-point 테스트 | 통과 | standard mode는 SAF only, power-user mode는 advanced import 추가 |
| 파워유저 advanced source catalog 테스트 | 통과 | power-user 비활성 시 advanced source 없음, 활성 시 선택된 source group만 노출 |
| persistable URI permission helper 테스트 | 통과 | content scheme + persistable/read 플래그 조합만 유지 대상으로 판정 |
| project source helper 테스트 | 통과 | source file 및 `_libs` 경로 계산이 helper 계약으로 고정 |
| `MainActivity` intent 재처리 가드 테스트 | 통과 | 첫 생성에서는 처리, 회전 재생성에서는 건너뛰는 규칙을 고정 |
| `assembleDebugAndroidTest` | 통과 | AndroidX runner와 smoke test가 컴파일되는지 확인 |
| 강화된 `assembleDebugAndroidTest` | 통과 | Compose UI 기반 PR 회귀 테스트 세트가 instrumentation APK로 묶이는지 확인 |
| large-file cache policy 테스트 | 통과 | 큰 파일은 메모리 캐시에 남기지 않는 규칙을 고정 |
| string search accumulator 테스트 | 통과 | 문자열 결과 상한과 truncation 동작을 고정 |
| architecture mapping 테스트 | 통과 | `x86_64`, `PPC64`가 64-bit mode로 매핑되는 규칙을 고정 |
| binary manual setup reload 테스트 | 통과 | override autosetup 변경 시 disassembly 재로드 필요 여부를 고정 |
| workflow YAML 파싱 | 통과 | `.github/workflows/ci.yml`, `.github/workflows/release.yml` 모두 Ruby YAML 파서 기준 확인 |

## 다음 웨이브 후보

| 우선순위 | 항목 | 이유 |
| --- | --- | --- |
| 높음 | AGP 업그레이드 | `compileSdk 35` 경고를 근본적으로 제거해야 한다 |
| 높음 | `MainActivity`의 남은 legacy picker 경로 정리 | SAF 전환을 앱 전체로 일관되게 맞춰야 한다 |
| 높음 | 오픈 PR `#723`~`#726` 검토 | 이미 제출된 수정안을 흡수하면 유지보수 속도를 높일 수 있다 |
| 중간 | 대용량 파일/RecyclerView/회전 크래시 재현 | 오래된 crash report 계열 이슈 소거에 직접 연결된다 |
