package ru.practicum.eventcompilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequestDto {

    @Valid
    private Set<Long> events;

    private Boolean pinned;

    private String title;

}
