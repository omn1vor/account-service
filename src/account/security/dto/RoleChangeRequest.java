package account.security.dto;

import account.security.entity.RoleChangeOperation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class RoleChangeRequest {
    @NotBlank
    private String user;
    @NotBlank
    private String role;
    @NotNull
    private RoleChangeOperation operation;
}
