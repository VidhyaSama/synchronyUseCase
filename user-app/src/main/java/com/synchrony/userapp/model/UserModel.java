package com.synchrony.userapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "should be filled out")
    private String email;
    @NotBlank(message = "should be filled out")
    @Size(min=5, max=10, message = "size should be in between 5 and 10")
    private String password;

    public UserModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
