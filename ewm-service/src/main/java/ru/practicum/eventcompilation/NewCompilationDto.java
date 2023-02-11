package ru.practicum.eventcompilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    @Valid
    private Set<Long> events;

    private Boolean pinned;

    @NotNull
    private String title;

}
