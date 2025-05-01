// PaymentRepository.java
package com.example.lottery.repository;

import com.example.lottery.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
