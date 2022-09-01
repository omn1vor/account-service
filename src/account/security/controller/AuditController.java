package account.security.controller;

import account.security.entity.SecurityEvent;
import account.security.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping("/events")
    public List<SecurityEvent> getActions() {
        return auditService.getEvents();
    }
}
