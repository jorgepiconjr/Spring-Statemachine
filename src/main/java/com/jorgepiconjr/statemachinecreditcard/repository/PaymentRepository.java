package com.jorgepiconjr.statemachinecreditcard.repository;

import com.jorgepiconjr.statemachinecreditcard.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
