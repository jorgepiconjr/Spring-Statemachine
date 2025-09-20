package com.jorgepiconjr.statemachinecreditcard.services;

import com.jorgepiconjr.statemachinecreditcard.domain.Payment;
import com.jorgepiconjr.statemachinecreditcard.domain.PaymentEvent;
import com.jorgepiconjr.statemachinecreditcard.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    Payment newPayment(Payment payment);
    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> delcineAuth(Long paymentId);

}
