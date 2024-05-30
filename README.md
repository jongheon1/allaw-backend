# Allaw Backend Service

## 개요

Allaw Service는 ElasticSearch와 OpenAI GPT 모델을 활용하여 법안 정보를 효율적으로 검색하고 요약하며, 법안에 대한 질의응답과 의견 작성 기능을 제공합니다. 또한, 키워드 구독을 통해 법안 업데이트 알림을 제공합니다. 이를 통해 사용자는 법안 정보를 쉽고 빠르게 접근하고, 법안에 대한 이해도를 높일 수 있습니다.


---
## 주요 기능

- **법안 검색**
- **법안 내용 요약**
- **법안 구독**
- **의견 남기기**
- **법안 기반 챗봇**

---
## API 명세

### GPT 관련

| 기능                  | 메서드 | Endpoint                  | 설명                                               |
|-----------------------|--------|---------------------------|----------------------------------------------------|
| 법안 요약             | POST   | /api/gpt/{billId}/summary | 지정된 법안 ID에 해당하는 법안 문서를 요약합니다. |
| 법안 기반 질문 답변   | POST   | /api/gpt/{billId}/chat    | 지정된 법안 ID에 해당하는 법안 내용을 기반으로 사용자의 질문에 답변합니다. |
| 일반 질문             | POST   | /api/gpt/query            | 일반적인 질문에 대해 GPT 모델의 답변을 반환합니다. |
| 동의 의견 추가        | POST   | /api/gpt/agree            | 후보의 공약에 대한 동의 의견을 추가합니다. |
| 반대 의견 추가        | POST   | /api/gpt/disagree         | 후보의 공약에 대한 반대 의견을 추가합니다. |

### 의견 관리

| 기능        | 메서드 | Endpoint            | 설명                           |
|-------------|--------|---------------------|--------------------------------|
| 의견 추가   | POST   | /api/opinions       | 새로운 의견을 추가합니다.      |
| 의견 조회   | GET    | /api//bill/{billId}       | 지정된 법안 ID에 해당하는 모든 의견을 조회합니다.        |
| 의견 수정   | PUT    | /api/opinions/{id}  | 지정된 ID의 의견을 수정합니다. |
| 의견 삭제   | DELETE | /api/opinions/{id}  | 지정된 ID의 의견을 삭제합니다. |

### 구독 관리

| 기능        | 메서드 | Endpoint               | 설명                                |
|-------------|--------|------------------------|-------------------------------------|
| 구독 추가   | POST   | /subscribe             | 키워드를 구독합니다.                |
| 구독 삭제   | DELETE | /unsubscribe/{keywordId}| 지정된 키워드 ID의 구독을 취소합니다. |
| 구독 조회   | GET    | /subscriptions         | 현재 사용자의 모든 구독을 조회합니다. |

---
## ERD (Entity-Relationship Diagram)
![alt text](allaw-erd.png)