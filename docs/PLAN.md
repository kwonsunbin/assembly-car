# Refactoring Plan — Assembly Car

> **Mission (CLAUDE.md):** Legacy 차량 조립 코드를 공학적 관점(비판적 사고)으로 분석하고, SOLID 원칙을 적용하여 단계별로 안전하게 리팩토링한다.
> **Rule (CLAUDE.md):** AI가 독단적으로 코드를 수정하지 않는다. 각 Phase는 사람과 합의 후 구현한다. (Human-in-the-loop)

---

## 1. 현재 코드 문제점 분석

`src/main/java/Assemble.java` 단일 파일에 모든 책임이 집중되어 있다.

### 1.1 공학적 문제점 목록

| # | 문제 유형 | 위치 | 설명 |
|---|-----------|------|------|
| P1 | **SRP 위반 (God Class)** | `Assemble.java` 전체 | UI 렌더링, 입력 처리, 상태 관리, 유효성 검사, 비즈니스 로직이 하나의 클래스에 혼재 |
| P2 | **Primitive Obsession** | `static int[] stack` | 차량 타입·부품을 원시 정수 배열로 관리 → 타입 안전성 없음, 인덱스 실수 가능 |
| P3 | **OCP 위반** | `isValidRange()`, `showXxxMenu()`, `selectXxx()`, `isValidCheck()` | 새 차량 타입이나 부품 추가 시 여러 메서드를 동시에 수정해야 함 |
| P4 | **중복 유효성 로직 (DRY 위반)** | `isValidCheck()` vs `testProducedCar()` | 동일한 부품 호환 규칙이 두 곳에 중복 구현됨 |
| P5 | **Magic Numbers** | 상수 선언부 전체 | `BOSCH_B = 3`, `BOSCH_S = 1` 처럼 같은 브랜드가 다른 파트 카테고리에서 충돌하는 상수값 사용 |
| P6 | **절차적 상태 기계** | `switch(step)` x2 | `step` 정수 + 거대한 `switch` 2개로 흐름 제어 → OOP 구조 없음 |
| P7 | **PRD와 코드 불일치** | `isValidCheck()` | PRD(SUV→조향장치 불가, Truck→제동장치 불가)와 실제 코드 규칙이 불일치 |
| P8 | **UI와 비즈니스 로직 혼재** | `runProducedCar()`, `testProducedCar()` | 결과 출력(UI)과 검증 로직(도메인)이 분리되지 않음 |

---

## 2. 리팩토링 목표

- **PRD 기준으로 유효성 규칙을 정확히 구현**: Sedan(전부 가능), SUV(조향장치 불가), Truck(제동장치 불가)
- **SOLID 원칙 적용**: 각 클래스·인터페이스가 단 하나의 변경 이유를 갖도록 설계
- **확장에 열려있는 구조**: 새 차량 타입·부품 추가 시 기존 코드를 수정하지 않아도 되도록

---

## 3. 단계별 리팩토링 계획

> 각 Phase는 독립적으로 빌드 및 실행 가능한 상태를 유지한다. Phase 완료 후 사람과 합의하고 다음 Phase로 진행한다.

---

### Phase 0 — Characterization Test 작성 (리팩토링 안전망 확보)

**목표:** 리팩토링 전에 현재 동작을 100% 커버하는 테스트를 먼저 작성한다.
리팩토링 중 회귀(regression)가 발생하면 즉시 감지할 수 있는 안전망을 만든다.

**작성 파일:** `src/test/java/AssembleTest.java`

**커버리지 전략:**
- 모든 `private static` 메서드는 Java Reflection으로 접근
- `private static int[] stack` 필드도 Reflection으로 테스트 간 격리 (각 테스트 전 reset)
- `main()` 통합 테스트는 `System.setIn` / `ByteArrayOutputStream`으로 I/O 제어
- JaCoCo 플러그인으로 커버리지 측정 (`./gradlew test` → `build/reports/jacoco/`)

**달성 커버리지:**

| 항목 | 달성 |
|------|------|
| LINE | 100% (173/173) |
| INSTRUCTION | 100% (671/671) |
| METHOD | 100% (18/18) |
| BRANCH | 96.6% (114/118) |

> **Branch 미달성 4개 이유:** 모두 `main()` 내 구조적 dead code다. `switch(step)` default 경로(step이 0~4 밖인 경우)와 `else if (step > CarType_Q)` false 경로(isValidRange가 이미 차단)는 현재 코드 흐름상 절대 도달 불가능하다. 이는 레거시 코드의 버그이며 테스트 공백이 아니다.

**테스트 케이스 목록 (73개):**

| 구분 | 테스트 수 | 커버 내용 |
|------|-----------|-----------|
| Constructor | 1 | default 생성자 (utility class) |
| showXxxMenu | 5 | 5개 메뉴 출력 텍스트 확인 |
| isValidRange | 23 | 5개 step × 범위 초과/미만/경계값 + switch default |
| selectXxx | 12 | 차량·엔진·제동·조향 선택 메시지 확인 |
| isValidCheck | 9 | 5가지 실패 규칙 + && 단락평가 분기 포함 |
| runProducedCar | 6 | 유효성 실패/고장엔진/정상 + Brake·Steering 출력 분기 |
| testProducedCar | 9 | 5가지 FAIL + && 분기 포함 PASS 케이스 |
| fail | 1 | FAIL 메시지 출력 확인 |
| delay | 2 | 정상 실행 + InterruptedException catch 경로 |
| main() 통합 | 7 | exit/invalid input/범위 초과/뒤로가기/전체 흐름 RUN+Test |

**검증:** `./gradlew test` → BUILD SUCCESSFUL, 73 tests passed, 0 failures

---

### Phase 1 — Enum 도입 (Primitive Obsession 제거)

**목표:** `int` 상수를 타입 안전한 Enum으로 교체한다.

**생성할 클래스:**
```
src/main/java/
  domain/
    CarType.java          // SEDAN, SUV, TRUCK
    Engine.java           // GM, TOYOTA, WIA, BROKEN
    BrakeSystem.java      // MANDO, CONTINENTAL, BOSCH
    SteeringSystem.java   // BOSCH, MOBIS
```

**변경 범위:** `Assemble.java` 내 정수 상수 → Enum 참조로 교체  
**검증:** 기존과 동일하게 동작하는지 수동 실행 확인  
**합의 포인트:** Enum 설계(이름, 표시 레이블 포함 여부) 확인 후 진행

---

### Phase 2 — 도메인 모델 분리 (SRP 적용)

**목표:** `int[] stack`을 `CarSpec` 값 객체로 교체하여 선택 상태를 타입 안전하게 관리한다.

**생성할 클래스:**
```
src/main/java/
  domain/
    CarSpec.java          // CarType + Engine + BrakeSystem + SteeringSystem 보유
```

**변경 범위:** `stack[0..4]` 접근 코드 → `CarSpec` getter/setter로 교체  
**검증:** 기존과 동일하게 동작하는지 수동 실행 확인  
**합의 포인트:** `CarSpec`의 불변(immutable) vs 가변(mutable) 설계 선택

---

### Phase 3 — 유효성 검사 분리 (OCP + Strategy Pattern)

**목표:** 중복된 유효성 로직을 제거하고, 새 규칙 추가 시 기존 코드 수정 없이 확장 가능하게 한다.

**생성할 클래스:**
```
src/main/java/
  domain/
    rule/
      CompatibilityRule.java       // interface: validate(CarSpec) → Optional<String>
      SedanCompatibilityRule.java  // Sedan: 모든 부품 허용
      SUVCompatibilityRule.java    // SUV: 조향장치 불가 (PRD 기준)
      TruckCompatibilityRule.java  // Truck: 제동장치 불가 (PRD 기준)
    CompatibilityValidator.java    // 규칙 목록을 순회하여 위반 메시지 수집
```

**변경 범위:** `isValidCheck()` + `testProducedCar()` 의 중복 로직 → `CompatibilityValidator`로 통합  
**검증:** PRD 규칙 3가지가 정확히 동작하는지 각 케이스 수동 테스트  
**합의 포인트:** 규칙 반환 타입(`boolean` vs `Optional<String>` vs custom `Result`), PRD 규칙 해석 최종 확인

---

### Phase 4 — UI 레이어 분리 (SRP 적용)

**목표:** 콘솔 출력(UI)을 비즈니스 로직에서 분리한다.

**생성할 클래스:**
```
src/main/java/
  ui/
    ConsoleRenderer.java   // 모든 System.out.println 담당 (메뉴, 결과, 에러 출력)
```

**변경 범위:** `Assemble.java` 내 `showXxxMenu()`, `runProducedCar()`, `fail()` 출력 코드 → `ConsoleRenderer`로 이동  
**검증:** 기존과 동일하게 출력되는지 확인  
**합의 포인트:** 추후 GUI 확장 가능성 고려한 인터페이스 추상화 여부

---

### Phase 5 — 상태 패턴 적용 (절차적 switch 제거)

**목표:** `switch(step)` 기반의 절차적 흐름을 State Pattern으로 교체한다.

**생성할 클래스:**
```
src/main/java/
  state/
    AssemblyState.java          // interface: display(), handleInput(int, CarSpec) → AssemblyState
    CarTypeSelectionState.java
    EngineSelectionState.java
    BrakeSelectionState.java
    SteeringSelectionState.java
    RunTestState.java
  AssemblySession.java          // 현재 상태 보유 및 전이 관리
```

**변경 범위:** `Assemble.java` main 루프 → `AssemblySession`에 위임, 거대한 `switch` 제거  
**검증:** 전체 플로우(선택 → 뒤로가기 → 테스트) 정상 동작 확인  
**합의 포인트:** State 전이 방향(State가 직접 다음 State 반환 vs Session이 관리) 설계 선택

---

## 4. 최종 패키지 구조 (목표)

```
src/main/java/
  Assemble.java               // main() 진입점만 남김
  domain/
    CarType.java
    Engine.java
    BrakeSystem.java
    SteeringSystem.java
    CarSpec.java
    rule/
      CompatibilityRule.java
      SedanCompatibilityRule.java
      SUVCompatibilityRule.java
      TruckCompatibilityRule.java
    CompatibilityValidator.java
  state/
    AssemblyState.java
    CarTypeSelectionState.java
    EngineSelectionState.java
    BrakeSelectionState.java
    SteeringSelectionState.java
    RunTestState.java
  ui/
    ConsoleRenderer.java
  AssemblySession.java
```

---

## 5. 진행 현황

| Phase | 내용 | 상태 |
|-------|------|------|
| Phase 0 | Characterization Test (81개, 전 클래스 LINE 100%) | ✅ 완료 |
| Phase 1 | Enum 도입 (CarType / Engine / BrakeSystem / SteeringSystem) | ✅ 완료 |
| Phase 2 | 도메인 모델 분리 (CarSpec, int[] stack 제거) | ✅ 완료 |
| Phase 3 | 유효성 검사 분리 (Strategy Pattern) | ⬜ 대기 |
| Phase 4 | UI 레이어 분리 (ConsoleRenderer, 직접 출력 0줄) | ✅ 완료 |
| Phase 5 | 상태 패턴 적용 (State Pattern) | ⬜ 대기 |

---

*이 문서는 각 Phase 완료 및 합의 시점에 갱신한다.*
