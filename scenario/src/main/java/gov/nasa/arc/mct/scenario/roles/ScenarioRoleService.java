/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.scenario.roles;

import gov.nasa.arc.mct.platform.spi.PersistenceProvider;
import gov.nasa.arc.mct.platform.spi.RoleService;
import gov.nasa.arc.mct.services.internal.component.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ScenarioRoleService implements RoleService {
	private static final String ALL_USERS = "All Users";
    private Set<String> roles = new HashSet<String>();
	private PersistenceProvider persistence;
    
    public void bind(PersistenceProvider p) {
    	persistence = p;
    	for (String u : p.getAllUsers()) {
    		String role = p.getUser(u).getDisciplineId();
    		if (role != null) {
    			roles.add(role);
    		}
    	}
    	roles.add(ALL_USERS);
    }
    
    public void unbind(PersistenceProvider p) {
    	persistence = null;
    }
    
	@Override
	public boolean hasRole(User user, String roleId) {		
		return user.getDisciplineId().equals(roleId) || ALL_USERS.equals(roleId);
	}

	@Override
	public Set<String> getAllRoles() {
		return roles;
	}

	@Override
	public Set<String> getAllUsers() {
		return persistence != null ? persistence.getAllUsers() : Collections.<String>emptySet();
	}

	@Override
	public String getDefaultRole() {
		return ALL_USERS;
	}

	@Override
	public String getDescription(String role) {
		return role;
	}

	@Override
	public String getPrimaryRole(User user) {
		return user.getDisciplineId();
	}

	@Override
	public Set<String> getAllRoles(String user) {
		Set<String> r = new HashSet<String>();
		if (persistence != null) {
			r.add(persistence.getUser(user).getDisciplineId());
		}
		r.add(ALL_USERS);
		return r;
	}
}
