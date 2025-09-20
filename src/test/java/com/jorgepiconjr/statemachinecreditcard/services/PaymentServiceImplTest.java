package com.jorgepiconjr.statemachinecreditcard.services;

import com.jorgepiconjr.statemachinecreditcard.domain.Payment;
import com.jorgepiconjr.statemachinecreditcard.domain.PaymentEvent;
import com.jorgepiconjr.statemachinecreditcard.domain.PaymentState;
import com.jorgepiconjr.statemachinecreditcard.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;
    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPayment = paymentService.newPayment(payment);

        System.out.println("-----------------> " + savedPayment.getState() + "\n");

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());
        Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println("\n-----------------> " + sm.getState().getId());
        System.out.println("-----------------> " + preAuthedPayment + "\n");
    }
}