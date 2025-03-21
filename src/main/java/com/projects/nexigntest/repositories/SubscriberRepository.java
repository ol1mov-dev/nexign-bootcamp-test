package com.projects.nexigntest.repositories;

import com.projects.nexigntest.entities.Subscriber;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {
    Subscriber findByPhoneNumber(String number);
}
