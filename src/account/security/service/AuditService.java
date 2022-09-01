package account.security.service;

import account.security.entity.Action;
import account.security.entity.SecurityEvent;
import account.security.repository.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Service
public class AuditService {
    @Autowired
    private SecurityEventRepository securityEventRepo;

    @Autowired
    private HttpServletRequest request;

    public void addEvent(String email, Action action, String object, String path) {
        SecurityEvent securityEvent = new SecurityEvent(email, action, object, path);
        securityEventRepo.save(securityEvent);
    }

    public void addEvent(String email, Action action, String object) {
        Principal principal =  request.getUserPrincipal();
        String path = request.getRequestURI();
        addEvent(email, action, object, path);
    }

    public void addEvent(Action action, String object) {
        Principal principal =  request.getUserPrincipal();
        String email = principal == null ? "Anonymous" : principal.getName();
        addEvent(email, action, object);
    }

    public List<SecurityEvent> getEvents() {
        return securityEventRepo.findAll();
    }
}
