package com.app.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.model.Card;
import com.app.model.STATUS;

public interface CardRepository extends JpaRepository<Card, Integer>{
       public Optional<Card> findByCardNumber(String cardNumber);
       public void deleteByCardNumber(String cardNumber);
       public boolean existsByCardNumber(String cardNumber);
    
       @Query("SELECT c FROM Card c JOIN FETCH c.owner u WHERE " +
              "((:isAdminSearch = false AND u.username = :username) OR " +
              "(:isAdminSearch = true AND (:username IS NULL OR u.username LIKE %:username%))) " +
              "AND (:cardNumber IS NULL OR c.cardNumber LIKE %:cardNumber%) " +
              "AND (:status IS NULL OR c.status = :status) " +
              "AND (:minBalance IS NULL OR c.balance >= :minBalance) " +
              "AND (:maxBalance IS NULL OR c.balance <= :maxBalance) " +
              "AND (:minEndDate IS NULL OR c.validityPeriod >= :minEndDate) " +
              "AND (:maxEndDate IS NULL OR c.validityPeriod <= :maxEndDate)")
       public Page<Card> findByCriteria(@Param("username") String username,
                                   @Param("cardNumber") String cardNumber,
                                   @Param("status") STATUS status,
                                   @Param("minBalance") Double minBalance,
                                   @Param("maxBalance") Double maxBalance,
                                   @Param("minEndDate") LocalDate minEndDate,
                                   @Param("maxEndDate") LocalDate maxEndDate,
                                   @Param("isAdminSearch") Boolean isAdminSearch,
                                   Pageable pageable);
}
