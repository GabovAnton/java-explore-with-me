package ru.practicum.eventcompilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventCompilationRepository extends JpaRepository<EventCompilation, Long>,
                                                    JpaSpecificationExecutor<EventCompilation> {

}