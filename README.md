## 📘 개요
- SNS 서비스를 제공하기 위한 백엔드 서버 입니다.
- <a href='https://github.com/dev-khg/preorder/tree/prod-monolith'>모놀리스 아키텍쳐</a>로 구현하고, 추후 MSA 아키텍쳐로 변경하는 방식으로 진행하였습니다.


## 📕 기술 스택
- Java 21, Spring boot 3.1.8
- Spring Security, Spring Cloud Gateway, OpenFeign, Eureka, Resilience4j, JWT, JPA
- MySQL, Redis, Kafka, H2(로컬 테스트용)
- Junit5, Mockito
- Docker, Docker-Compose


## 📙 Module 구조
#### `common-project` : 모든 프로젝트에 공통적으로 포함할 것들을 모듈화한 프로젝트

#### `eureka-service` : 동적으로 각 서비스를 등록하고, 식별하기 위한 서비스

#### `gateway-service` : API Gateway 역할(라우팅, 로드밸런싱, 필터링 등)

#### `user-service` : 유저 도메인 관련 서버 (로그인, 회원가입, 팔로우 등)

#### `newsfeed-service` : 뉴스피드 도메인 관련 서버 (게시글, 댓글, 좋아요 등)

#### `activity-service` : 활동 도메인 서버 (유저의 활동 내역 (게시글 작성, 팔로우, 좋아요 등)을 저장 및 처리하는 서버)

<br/>

## 📘 E-R 다이어그램
<p align='center'>
    <img width="677" alt="image" src="https://github.com/dev-khg/preorder/assets/108642272/441f6cbb-30dc-4991-86bb-4b75547da1e4">
</p>

- <strong style="font-size: 16px;">User-Service</strong> : 회원, 팔로우 관련 데이터 집합<br/>
- <strong style="font-size: 16px;">NewFeed-Service</strong> : 게시글, 댓글 관련 데이터 집합<br/>
- <strong style="font-size: 16px;">Activity-Service</strong> : 활동 관련 데이터 집합 (= 팔로우 이력, 게시글, 댓글 이력 등)<br/>

<br/>

## 📕 Architecture & Description
<p align='center'>
    <img width="1296" alt="image" src="https://github.com/dev-khg/preorder/assets/108642272/f1d223b6-7275-4e65-b53f-8bc0383e31fb">
</p>

### <strong>MSA 아키텍쳐는 각 도메인 별 서비스를 분리하여, 동적으로 스케일링이 가능하고, 장애의 전파를 차단하는 것에 목적을 둠</strong> <br/>

  - <strong style="font-size: 16px;">동적 스케일링</strong> : 동적 스케일링을 위해서는 로드밸런싱과 서비스의 동적인 추가는 필수적이다. 따라서, Eureka Service를 통한 동적 추가 및 Spring Gateway를 이용한 로드밸런싱 기능을 제공 <br/>

  - <strong style="font-size: 16px;">도메인별 서버 분리</strong> : 기존 모놀리스 서비스는 모든 기능이 하나의 서버에 제공되므로, 부분적 스케일링이 불가능하고, 장애 발생시 서버전체의 장애로 전파될 가능성이 있다. 따라서, 도메인 별로 서버를 분리하여, 동적으로 스케일링 확장이 가능하고, 장애 전파를 최소화 할 수 있다. <br/>

  - <strong style="font-size: 16px;">데이터베이스 분리</strong> : 하나의 데이터베이스 서버를 사용한다면, 데이터베이스 서버 장애가 모든 서버의 장애로 이루어짐. 따라서, 도메인별 데이터베이스 서버도 분리함으로써, 장애 전파 최소화
  - <strong style="font-size: 16px;">서킷 브레이커 적용</strong> : MSA 간 정보를 요청할 때, 결합된 서버의 장애 현재 서버의 장애로 전파될 수 있음. -> 따라서, 서킷브레이커를 적용하여 장애의 전파를 차단하고, 장애의 전파와 관련되지 않은 서비스는 정상적으로 제공할 수 있음
      - <strong>간단한 시나리오 재현</strong> : Activity-Service에 피드 요청시 feed-service가 죽으면, 해당 장애로 인해 팔로우, 유저관련 서비스 또한 같이 제공할 수 없음. 따라서, feed-service가 죽어도, 서킷브레이커를 적용하여 해당 서비스와 관련되지 않은 서비스는 정상적으로 제공할 수 있게 됨.

### <strong>카프카 도입 이유</strong>
  - <strong style="font-size: 16px;">이벤트 드리븐 아키텍처</strong> : 이벤트를 생성하고, Kafka를 통해 전송함으로써, 결합도를 낮출 수 있음

  - <strong style="font-size: 16px;">비동기 통신</strong> : 실시간성이 중요하지 않은 서비스는 비동기적으로 진행하여, 사용자의 응답시간 개선 및 시스템 성능향상을 불러올 수 있음

  - <strong style="font-size: 16px;">서버의 수평적 확장 용이</strong> : 도메인 서버확장 시 카프카 인터페이스를 두어 수평적확장에 용이함

<br/><br/>


## 📙 요구사항 명세서
### ✏️ User
- 유저는 회원가입을 통해 계정을 생성할 수 있다.
- 유저는 중복 및 무분별한 계정 생성 방지를 위한 이메일 인증 기능이 필요하다.
- 유저의 비밀번호는 암호화하여 저장하여야 한다.
- 유저의 계정은 이름,프로필 이미지,인사말 정보들을 입력 받을 수 있다.
- 유저는 이메일을 제외한 비밀번호 및 필수정보들을 변경할 수 있어야 한다.
- 유저는 로그아웃 기능을 사용할 수 있어야 한다.

### ✏️ Follow
- 유저는 다른 유저의 정보 구독을 위해 팔로우 및 언팔로우가 가능해야 한다.

### ✏️ Post
- 사용자는 텍스트 기반의 포스트를 작성할 수 있어야 한다.
- 사용자는 텍스트 기반의 포스트를 수정할 수 있어야 한다.
- 사용자는 포스트 좋아요/좋아요 취소 기능을 사용할 수 있어야 한다.

### ✏️ Feed
- 유저는 팔로우한 사용자의 활동(댓글, 좋아요, 팔로우)을 피드에서 조회할 수 있어야 한다.
- 유저는 팔로우한 사용자의 포스트를 피드에서 조회할 수 있어야 한다.

### ✏️ Comment
- 사용자는 포스트에 댓글을 작성할 수 있어야 한다.
- 사용자가 포스트에 작성한 댓글은 수정할 수 있어야 한다.
- 사용자는 댓글 좋아요/좋아요 취소 기능을 사용할 수 있어야 한다.

<br/><br/>

## 📘 도커 실행(docker-compose)
```
1. ./gradlew clean build
2. setting variable in "pre-order.env" file (설정하지 않으면 이메일 관련 인증은 사용 불가)
3. docker-compose up -d
```
<br/>

## 📕 <a href='https://documenter.getpostman.com/view/25250187/2s9YyweJyz'>POSTMAN (API 문서 링크)</a>
<strong>테스트 계정</strong> <br/>
 메인 화원 | ID : test1234@test.com / PW : test1234 <br/>
 메인 회원이 팔로우 하는 회원 | ID : tester-2@test.com / PW : test1234

인가가 필요한 접근 : Header - Authorziation에 AccessToken 삽입 필요 (bearer prefix 없이)

 
