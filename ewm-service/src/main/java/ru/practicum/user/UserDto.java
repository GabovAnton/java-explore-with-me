package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Email
    @Size(max = 512, message = "{validation.name.size.too_long}")
    private String email;

    @ReadOnlyProperty
    private Long id;

    @NotNull
    @Size(max = 300, message = "{validation.name.size.too_long}")
    private String name;

}
