package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @Email
    @NotNull
    @Size(max = 512, message = "{validation.name.size.too_long}")
    private String email;

    @NotNull
    @Size(max = 300, message = "{validation.name.size.too_long}")
    private String name;

}
