package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    List<Request> findByEvent_Initiator_IdAndEvent_Id(Long userId, Long eventId);

    List<Request> findByIdIn(Collection<Long> ids);

}