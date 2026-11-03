# Yotion 작업 규칙 초안

이 문서는 코드 검토를 바탕으로 작성한 제안이다. 저장소 루트에 적용하기 전 내용을 검토한다.

## 프로젝트와 구조
- Java 17, Spring Boot 4.0.2, Gradle Wrapper 9.3.0, Spring MVC, Spring Data JPA, Spring Security, Lombok을 사용한다.
- 기본 패키지는 `com.yo.yotion`이다.
- 기능 코드는 `domain/{user,page,member,post}` 아래의 `controller`, `service`, `repository`, `entity`, `dto`에 배치한다. 필요한 계층만 추가한다.
- 기존 단일 Service 클래스 구성을 유지한다. 작업에 필요하지 않은 Service 인터페이스, 공통 추상화, 새 프레임워크를 추가하지 않는다.
- `src/main/java`가 실제 Java 소스 위치다. `bin`, `build`, 루트 `security/CustomUserDetails.java`, ZIP 파일을 구현 기준으로 삼지 않는다.

## 작업 범위와 호환성
- 수정 전에 관련 Controller → Service → Repository → Entity/DTO와 호출 경로를 읽는다.
- 기존 API 경로, HTTP 상태, 요청·응답 필드, DB 컬럼과 enum 저장값을 임의로 변경하지 않는다.
- 현재 `Page.title`은 DB `name`, 응답 `name`으로 매핑된다. 이를 단순 오타로 보고 일괄 변경하지 않는다.
- 미완성 기능과 확정된 정책을 구분한다. TODO·학습 메모만으로 요구사항을 단정하지 않는다.
- 작업과 무관한 포맷 변경, 패키지 이동, 전역 리팩터링을 하지 않는다.

## Controller와 DTO
- Controller는 요청 매핑, `@Valid` 입력 검증, 인증 사용자 전달, Service 호출, `ResponseEntity` 응답에 집중한다.
- 새 비즈니스 규칙과 권한 검증은 Service 또는 Entity에 둔다. 기존 Controller의 정책 검사는 해당 기능 수정 범위에서 다룬다.
- 외부 API에는 Entity를 직접 반환하지 않고 `*RequestDto`, `*ResponseDto`를 사용한다.
- 요청 DTO에는 필요한 Jakarta Validation 제약과 한국어 메시지를 둔다. 새 필드는 기본적으로 private으로 작성한다.
- 응답 변환은 기존 `static from(...)`과 Lombok Builder 패턴을 따른다. 변환에 필요한 필드가 누락되지 않게 확인한다.
- 현재 성공 응답은 DTO 직접 반환이다. 미완성 `common.response.ApiResponse`를 임의로 전면 도입하지 않는다.
- `UserService.findByEmail` 같은 내부 Entity 반환과 외부 API 응답을 구분한다. 비밀번호·토큰을 응답이나 로그에 노출하지 않는다.

## Service와 트랜잭션
- `@Service`, `@RequiredArgsConstructor`, final 필드 생성자 주입을 따른다.
- Service 클래스의 기본은 `@Transactional(readOnly = true)`, 쓰기 메서드는 `@Transactional`로 재정의한다.
- 생성·권한 확인·관련 Entity 저장을 하나의 일관된 트랜잭션으로 처리한다.
- 수정은 Entity의 의미 있는 메서드와 JPA 변경 감지를 활용한다. 불필요한 setter나 반복 save를 추가하지 않는다.
- 인증 사용자 ID와 대상 사용자 ID, 페이지 ID의 순서와 의미를 호출부까지 확인한다.
- 외부 메일 등의 부수 효과는 DB 커밋 시점과 실패 처리 정책을 고려한다. 이벤트 발행만으로 메일 전송이 구현되었다고 판단하지 않는다.

## Repository와 Entity
- Repository는 `JpaRepository<Entity, Long>`와 기존 파생 쿼리 패턴을 따른다. 필요한 경우 JPQL, `@EntityGraph`를 사용한다.
- DTO 변환에서 접근하는 연관관계와 조회 범위를 확인한다. 무조건 EAGER로 바꾸지 않는다.
- Entity는 Getter 중심으로 두고 `create`, `updateTitle`, `updateProfile`, `changePassword`, `delete` 같은 의도를 드러내는 메서드를 사용한다.
- 신규 연관관계는 기존 LAZY 패턴을 기준으로 검토한다. 기존 테이블·컬럼명, IDENTITY 키, 문자열 enum 매핑을 존중한다.
- 기존 User/Page/Member/Post의 `deletedAt` 및 `@SQLRestriction` 기반 소프트 삭제를 유지한다. 물리 삭제로 대체하지 않는다.
- 소프트 삭제와 유니크 제약의 충돌, 삭제된 연관 데이터의 재조회·재가입 동작을 확인한다.
- Builder 생성 시 필수 필드와 기본값을 확인한다. 버전 숫자만으로 동시성 제어가 보장된다고 판단하지 않는다.

## 예외 처리: 현황과 신규 코드 방향
- 현재 User/Page는 `IllegalArgumentException`, Member는 `global.exception.BusinessException`을 사용하며 `common.exception`에도 별도 체계가 있다. 이를 통일된 기존 관례라고 설명하지 않는다.
- 신규 도메인 오류는 `global.exception.BusinessException`/`ErrorCode` 방향을 우선 검토한다. 실제 적용 시 전용 핸들러와 의도한 HTTP 상태를 함께 검증한다.
- 현재 GlobalExceptionHandler에는 BusinessException 전용 처리가 없다. ErrorCode에 상태를 정의한 것만으로 403/404/409 응답이 보장되지 않는다.
- 기존 API의 오류 형식·상태 변경은 변경 범위와 호환성 영향을 명시한다. 신규 예외 체계를 추가하지 않는다.

## 보안과 설정
- 사용자 식별은 인증 주체를 기준으로 한다. 요청의 userId를 신뢰하여 권한을 부여하지 않는다.
- 페이지별 MemberRole 권한은 Service에서 검사한다. 전역 `ROLE_USER`와 페이지 역할을 혼동하지 않는다.
- 현재 인증 연결이 미완성이므로 `@AuthenticationPrincipal`이 항상 존재한다고 가정하지 않는다.
- BCrypt PasswordEncoder 사용을 유지한다. OAuth 계정의 비밀번호 처리와 인증 실패 응답도 확인한다.
- `.env`, 비밀번호, 키, 실제 접속 정보를 문서·테스트·로그·커밋에 복사하지 않는다. 새 비밀값은 환경변수로 받는다.
- 기존 DB에 연결하는 실행 전 datasource 및 `ddl-auto`를 확인한다. `create` 설정으로 기존 DB의 애플리케이션·컨텍스트 테스트를 실행하지 않는다.
- 테스트는 별도 프로필과 격리된 DB를 사용한다. 설정에 있는 Redis/JWT/Swagger 항목만으로 구현 완료를 가정하지 않는다.

## 네이밍과 문체
- 클래스는 PascalCase, 메서드·변수는 camelCase, enum 상수는 UPPER_SNAKE_CASE를 사용한다.
- 기존 `*Dto`, `*Service`, `*Repository` 접미사와 한국어 주석·메시지를 따른다. 새 변수에는 `inviterMember`, `inviteeUser`처럼 역할을 드러내는 이름을 쓴다.
- enum `EXPRIRED`, 컬럼 `postion` 등 기존 저장값의 오타는 영향 확인 없이 바꾸지 않는다.
- 인접 코드의 들여쓰기를 따르되 불필요한 빈 줄·사용하지 않는 import를 새로 추가하지 않는다.

## 검증과 Git
- 시작 시 `git status --short --branch`와 관련 diff를 확인한다. 사용자의 변경과 미추적 파일을 보존한다.
- 특히 현재 미추적 `src/main/java/com/yo/yotion/domain.zip`을 삭제·덮어쓰기·임의 커밋하지 않는다.
- 동작 변경에는 관련 성공·실패 경계의 의미 있는 테스트를 작성하거나 보완한다. 문서 수정에는 기계적인 테스트를 추가하지 않는다.
- 격리된 설정을 마련한 뒤 `./gradlew test`를 실행한다. 필요하면 `./gradlew compileJava`로 컴파일을 먼저 확인한다.
- 현재 테스트는 contextLoads 하나뿐이다. 통과만으로 인증·권한·초대·삭제 기능을 검증했다고 보고하지 않는다.
- 실행한 검사와 결과, 실행하지 못한 검사와 이유를 명시한다. 완료 시 변경 파일·동작·남은 제한을 요약한다.
