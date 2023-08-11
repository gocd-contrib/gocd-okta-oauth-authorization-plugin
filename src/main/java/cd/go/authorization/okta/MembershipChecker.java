/*
 * Copyright 2017 Svetlin Zamfirov
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

import java.io.IOException;
import java.util.List;

import static cd.go.authorization.okta.OktaPlugin.LOG;
import static java.text.MessageFormat.format;

public class MembershipChecker {

    public boolean isAMemberOfAtLeastOneGroup(OktaUser loggedInUser, AuthConfig authConfig, List<String> groupsAllowed) throws IOException {
        if (groupsAllowed.isEmpty()) {
            LOG.info("[MembershipChecker] No groups provided.");
            return false;
        }

        return checkMembershipUsingUsersAccessToken(loggedInUser, groupsAllowed);
    }

    private boolean checkMembershipUsingUsersAccessToken(OktaUser loggedInUser, List<String> groupsAllowed) throws IOException {
        final List<String> myGroups = loggedInUser.groups();

        for (String groupName : myGroups) {
            if (groupsAllowed.contains(groupName)) {
                LOG.debug(format("[MembershipChecker] User `{0}` is a member of `{1}` group.", loggedInUser.getEmail(), groupName));
                return true;
            }
        }

        return false;
    }
}
