# `#728` 병합 후 정리 체크리스트

이 문서는 유지보수 기준선 PR `#728`이 병합된 직후, 바로 닫을 항목과 검증 후 닫을 항목을 구분해서 정리한 체크리스트다.

## 바로 닫을 항목

| 종류 | 번호 | 근거 |
| --- | --- | --- |
| 이슈 | `#670` | `MainActivity` launcher 노출 문제는 manifest 명시로 `#728`에 포함됨 |
| 이슈 | `#396` | Hex view offset/16-byte row 개선이 `#728`에 포함됨 |
| 이슈 | `#348` | disassembly horizontal scroll fix가 `#728`에 포함됨 |
| 이슈 | `#160` | recreation 시 external import intent 재처리 가드가 `#728`에 포함됨 |
| 이슈 | `#123` | project ZIP export 복구가 `#728`에 포함됨 |
| 이슈 | `#159` | import 파일명 정규화와 fallback 규칙이 `#728`에 포함됨 |
| 이슈 | `#720` | binary detail `.txt` export가 `#728`에 포함됨 |
| 이슈 | `#129` | generic archive extraction으로 `.ar` 지원이 `#728`에 포함됨 |

## 병합 후 재확인 뒤 닫을 항목

| 종류 | 번호 | 병합 후 확인 포인트 |
| --- | --- | --- |
| 이슈 | `#514` | `.so` 샘플이 현재 64-bit architecture mapping으로 정상 해석되는지 |
| 이슈 | `#543` | `Override autosetup` 이후 disassembly가 실제로 재로드되는지 |
| 이슈 | `#576` | `.so` 입력 경로가 최신 baseline에서 충분히 커버되는지 |
| 이슈 | `#235` | chooser payload 축소와 large-state 완화 이후 `TransactionTooLarge` 재현이 남는지 |
| 이슈 | `#442` | string result cap/stable key 이후 긴 문자열 리스트에서 Recycler/List 불안정이 남는지 |
| 이슈 | `#523` | large-file byte cache 제외 이후 150MB 클래스 입력에서 메모리 압박이 남는지 |
| 이슈 | `#95` | 최신 Android에서 기본 import/export/open 경로가 SAF 중심으로 충분히 수렴했는지 |
| PR | `#726` | `#728` 이후에도 별도 병합 가치가 남는지, 아니면 superseded 처리할지 |

## 정리 규칙

| 상황 | 액션 |
| --- | --- |
| `#728`로 완전히 대체된 항목 | 병합 후 바로 닫고 `fixed by #728` 코멘트 남김 |
| 부분 완화만 된 항목 | `resolved`로 닫지 않고 재현/실기기 확인 후 닫음 |
| 오래된 PR이 새 기준선보다 뒤처진 경우 | 별도 병합 없이 superseded/obsolete로 닫음 |
