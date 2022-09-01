package account.configuration;

import account.security.entity.Action;
import account.security.service.AuditService;
import account.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class AuthenticationFailureListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private UserService userService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String email = (String) event.getAuthentication().getPrincipal();
        String path = request.getRequestURI();
        auditService.addEvent(email, Action.LOGIN_FAILED, path);
        boolean needToLock = userService.registerFailedLoginAttempt(email);
        if (needToLock) {
            auditService.addEvent(Action.BRUTE_FORCE, path);
            userService.lockUser(email);
        }
    }
}