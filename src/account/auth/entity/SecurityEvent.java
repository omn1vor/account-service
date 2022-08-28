package account.auth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "security_events")
public class SecurityEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;
    private String subject;
    private String object;
    private String path;
}
