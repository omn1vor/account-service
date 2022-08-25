package account.service;

import account.entity.UserGroup;
import account.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserGroupService {
    @Autowired
    private UserGroupRepository userGroupRepo;

    public UserGroup getAdministrator() {
        return findCreateGroupByCode("ADMINISTRATOR");
    }

    public UserGroup getAccountant() {
        return findCreateGroupByCode("ACCOUNTANT");
    }

    public UserGroup getUser() {
        return findCreateGroupByCode("USER");
    }

    public Optional<UserGroup> getGroupByCode(String code) {
        return userGroupRepo.findByCode(code);
    }

    private UserGroup findCreateGroupByCode(String code) {
        return userGroupRepo.findByCode(code).orElseGet(() -> createGroup(code));
    }

    private UserGroup createGroup(String code) {
        UserGroup userGroup = new UserGroup();
        userGroup.setCode(code.toUpperCase());
        userGroup.setName(code);
        userGroupRepo.save(userGroup);
        return userGroup;
    }
}
