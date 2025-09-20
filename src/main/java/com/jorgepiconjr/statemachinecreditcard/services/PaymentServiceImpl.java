package com.jorgepiconjr.statemachinecreditcard.services;

import com.jorgepiconjr.statemachinecreditcard.domain.Payment;
import com.jorgepiconjr.statemachinecreditcard.domain.PaymentEvent;
import com.jorgepiconjr.statemachinecreditcard.domain.PaymentState;
import com.jorgepiconjr.statemachinecreditcard.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "payment_id";
    @Autowired
    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTH_APPROVED);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> delcineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
        return sm;
    }

    // Sending Event to Statemachine
    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event){
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        sm.sendEvent(msg);
    }

    // Method to get Statemachine of some Payment to change state of Statemachine considering actual state of Payment
    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId){
        // Fetching Payment from Payment Repository
        Payment payment = paymentRepository.getOne(paymentId);
        // Fetching Statemachine of payment from Statemachinefactory
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));

        // Stopping Statemachine to change the "state" afterwards
        sm.stop();

        // Changing "state" of Statemachine inserting the actual "state" of payment in Data Base
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null));
        });

        // Starting Statemachine again after "state" change
        sm.start();
        return sm;

    }

}
