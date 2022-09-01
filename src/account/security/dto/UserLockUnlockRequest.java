package account.security.dto;

import account.security.entity.LockUnlock;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class UserLockUnlockRequest {
    @NotBlank
    private String user;
    private LockUnlock operation;
}
