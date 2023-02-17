package ru.practicum.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Transactional
    @Modifying
    @Query("update Event e set e.views = e.views + 1 where e.id = ?1")
    int updateIncrementViewsById(Long id);

    @Query("select e from Event e where e.id in ?1")
    Set<Event> findByIdIn(Collection<Long> ids);

    List<Event> findByInitiatorId(Long id);

}