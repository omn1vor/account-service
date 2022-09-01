package account.security.service;

import account.security.entity.UserGroup;
import account.security.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserGroupService {
    private final UserGroupRepository userGroupRepo;

    public UserGroupService(@Autowired UserGroupRepository userGroupRepo) {
        this.userGroupRepo = userGroupRepo;
        init();
    }

    public void init() {
        getAdministrator();
        getUser();
        getAccountant();
        getAuditor();
    }

    public UserGroup getAdministrator() {
        return findCreateGroupByCode("ROLE_ADMINISTRATOR", true);
    }

    public UserGroup getAccountant() {
        return findCreateGroupByCode("ROLE_ACCOUNTANT", false);
    }

    public UserGroup getAuditor() {
        return findCreateGroupByCode("ROLE_AUDITOR", false);
    }

    public UserGroup getUser() {
        return findCreateGroupByCode("ROLE_USER", false);
    }

    public Optional<UserGroup> getGroupByCode(String code) {
        return userGroupRepo.findByCode(code);
    }

    private UserGroup findCreateGroupByCode(String code, boolean administrative) {
        return userGroupRepo.findByCode(code).orElseGet(() -> createGroup(code, administrative));
    }

    private UserGroup createGroup(String code, boolean administrative) {
        UserGroup userGroup = new UserGroup();
        userGroup.setCode(code.toUpperCase());
        userGroup.setName(code.replace("ROLE_", ""));
        userGroup.setAdministrative(administrative);
        userGroupRepo.save(userGroup);
        return userGroup;
    }
}
