package account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class PasswordChangeResponse {
    private String email;
    private String status;
}
