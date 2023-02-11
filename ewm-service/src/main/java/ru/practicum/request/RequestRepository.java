package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    @Query("select r.id, r.created, r.event.id, r.requester.id, r.status  from Request r  where r.event.id IN " +
           "(select e.id from Event e where e.initiator.id = ?1 and e.id = ?2)")
    List<Request> findRequestByUserAndEvent(Long userId, Long eventId);

    @Query("select r from Request r where r.id in ?1")
    List<Request> findByIdIn(Collection<Long> ids);

}