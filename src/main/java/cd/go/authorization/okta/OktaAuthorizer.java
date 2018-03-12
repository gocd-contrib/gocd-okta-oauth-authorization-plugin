/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.authorization.okta;

import cd.go.authorization.okta.models.AuthConfig;
import cd.go.authorization.okta.models.Role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cd.go.authorization.okta.OktaPlugin.LOG;
import static java.text.MessageFormat.format;

public class OktaAuthorizer {
    private final MembershipChecker membershipChecker;

    public OktaAuthorizer() {
        this(new MembershipChecker());
    }

    public OktaAuthorizer(MembershipChecker membershipChecker) {
        this.membershipChecker = membershipChecker;
    }

    public List<String> authorize(OktaUser loggedInUser, AuthConfig authConfig, List<Role> roles) throws IOException {
        final OktaUser user = loggedInUser;
        final List<String> assignedRoles = new ArrayList<>();

        if (roles.isEmpty()) {
            return assignedRoles;
        }

        LOG.info(format("[Authorize] Authorizing user {0}", user.getEmail()));

        for (Role role : roles) {
            final List<String> allowedUsers = role.roleConfiguration().users();
            if (!allowedUsers.isEmpty() && allowedUsers.contains(user.getEmail().toLowerCase())) {
                LOG.debug(format("[Authorize] Assigning role `{0}` to user `{1}`. As user belongs to allowed users list.", role.name(), user.getEmail()));
                assignedRoles.add(role.name());
                continue;
            }

            if (membershipChecker.isAMemberOfAtLeastOneGroup(loggedInUser, authConfig, role.roleConfiguration().groups())) {
                LOG.debug(format("[Authorize] Assigning role `{0}` to user `{1}`. As user is a member of at least one group.", role.name(), user.getEmail()));
                assignedRoles.add(role.name());
                continue;
            }
        }

        LOG.debug(format("[Authorize] User `{0}` is authorized with `{1}` role(s).", user.getEmail(), assignedRoles));

        return assignedRoles;
    }
}
