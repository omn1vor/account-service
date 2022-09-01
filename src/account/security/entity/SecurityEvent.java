package account.security.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "security_events")
public class SecurityEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;
    private String subject;
    private String object;
    private String path;

    public SecurityEvent(String email, Action action, String object, String path) {
        this.date = LocalDateTime.now();
        this.action = action;
        this.subject = email;
        this.object = object;
        this.path = path;
    }
}
