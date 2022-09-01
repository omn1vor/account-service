package account.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    @NotBlank
    private String name;
    @NotBlank @JsonProperty("lastname")
    private String lastName;
    @NotBlank @Pattern(regexp = ".+@acme\\.com$")
    private String email;
    @NotBlank @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private List<String> roles = new ArrayList<>();
}
