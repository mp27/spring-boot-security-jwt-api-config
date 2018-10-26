package mp27.security.starter.service;

import mp27.security.starter.model.Role;
import mp27.security.starter.model.RoleName;

import java.util.Optional;

public interface RoleService extends CrudService<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
