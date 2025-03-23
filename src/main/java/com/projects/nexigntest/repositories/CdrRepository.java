package com.projects.nexigntest.repositories;

import com.projects.nexigntest.entities.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CdrRepository extends JpaRepository<Cdr, Long> {
    List<Cdr> findByCallerIdAndStartTimeBetween(Long callerId, LocalDateTime startTime, LocalDateTime endTime);
    List<Cdr> findByCallerIdAndStartTimeAfter(Long callerId, LocalDateTime startTime);
}
