# Payment State Machine with Spring

This project is a practical example of implementing a payment processing state machine using **Spring Boot** and **Spring Statemachine**. It demonstrates how to model the lifecycle of a payment—from creation through pre-authorization and final capture—in a clean and state-driven way.

---

### Technology Stack

*   **Java 17**
*   **Spring Boot**
*   **Spring Statemachine**
*   **Spring Data JPA**
*   **H2 Database**
*   **Maven**
*   **JUnit 5 & Spring Test**

---

### How the State Machine Works

The machine follows a two-step payment authorization flow. The states and transitions are managed based on specific events that represent the outcome of payment operations.

### State Machine Diagram

The diagram below visually represents the entire flow of states and events described above.

<img src="https://github.com/jorgepiconjr/Spring-Statemachine/blob/main/src/main/resources/static/img.png?raw=true" alt="Payment State Machine Diagram" width="500"/>

#### Events and Transitions

1.  **Creation → Pre-Authorization:**
    *   The process begins when a `PRE_AUTHORIZE` event is sent.
    *   This transitions the payment into the `NEW` state, ready for processing.

2.  **Pre-Authorization Outcome:**
    *   If the pre-authorization is successful, a `PRE_AUTH_APPROVED` event moves the state to `PRE_AUTH`. The funds are now on hold.
    *   If the pre-authorization is declined, a `PRE_AUTH_DECLINED` event moves the state to `PRE_AUTH_ERROR`, which is a final failure state.

3.  **Final Authorization (Capture) Outcome:**
    *   From the `PRE_AUTH` state, an `AUTHORIZE` event is sent to capture the held funds.
    *   If the capture is successful, an `AUTHORIZED` event transitions the state to `AUTH`, the final success state.
    *   If the capture is declined, an `AUTH_DECLINED` event transitions the state to `AUTH_ERROR`, another final failure state.
