/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
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
package gov.nasa.arc.mct.qspersistence.service;

import gov.nasa.arc.mct.qspersistence.service.InternalPersistenceAccess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfo;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfoImpl;
import gov.nasa.arc.mct.platform.spi.PersistenceProvider;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.Updatable;
import gov.nasa.arc.mct.services.internal.component.User;

public class PersistenceServiceImpl implements PersistenceProvider {

	private int id = 0; // Used to give components unique ids
	
	private enum Tag {
		NONE,
		BOOTSTRAP_CREATOR,
		BOOTSTRAP_ALL
	}
	
	private List<String>                   bootstrap           = new ArrayList<String>();
	private Map<String, List<String>>      userBootstrap       = new HashMap<String, List<String>>();
	private Map<String, AbstractComponent> persistedComponents = new HashMap<String, AbstractComponent>();
	private Map<String, User>              users               = new HashMap<String, User>();
	private Map<String, List<String>>      references          = new HashMap<String, List<String>>();
	
	private Set<String>                    updated             = new HashSet<String>();
	private ConcurrentHashMap<String, Set<AbstractComponent>> 
	                                       cache               = new ConcurrentHashMap<String, Set<AbstractComponent>>();
	
	private AtomicReference<List<AbstractComponent>> workUnit  = new AtomicReference<List<AbstractComponent>>();
	
	private AtomicBoolean initialized = new AtomicBoolean(false);
	
	public PersistenceServiceImpl() {
	}
	
	private void initialize() {
		if (initialized.getAndSet(true)) return; //Only initialize once!
		
		addUser("admin", "Admin");
		addUser("jimbooster", "Users" );
		
		AbstractComponent systems     = addComponent("Systems", "admin", "admin", "gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent", Tag.BOOTSTRAP_ALL);
		
		AbstractComponent groups      = addComponent("Groups",  "admin", "admin", "gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent", Tag.BOOTSTRAP_ALL);
        
		AbstractComponent display     = addComponent("Display", "jimbooster", "jimbooster", "gov.nasa.arc.mct.components.collection.CollectionComponent", Tag.NONE );
		AbstractComponent collection  = addComponent("Telemetry Collection", "jimbooster", "jimbooster", "gov.nasa.arc.mct.components.collection.CollectionComponent", Tag.NONE );
		link(display, collection);
			
		for (String user : getAllUsers()) {
			AbstractComponent sandbox   = addComponent( "My Sandbox", user, user, "gov.nasa.arc.mct.core.components.MineTaxonomyComponent", Tag.BOOTSTRAP_CREATOR);
			AbstractComponent dropbox   = addComponent( user + "'s drop box", user, user, "gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent", Tag.NONE);
			AbstractComponent dropboxes = addComponent( "All " + getUser(user).getDisciplineId() + " Drop boxes", "admin", "admin", "gov.nasa.arc.mct.components.collection.CollectionComponent", Tag.NONE );
			link (sandbox,   dropbox  );
			link (sandbox,   display  );
			link (groups,    dropboxes);
			link (dropboxes, dropbox  );
		}
		
		AbstractComponent[] telemetry = new AbstractComponent[5];
		for (int i = 0; i < 5; i++) {
			telemetry[i] = addComponent( "Telemetry " + (i+1), "admin", "admin", "org.acme.example.telemetry.TelemetryComponent", Tag.NONE );
		 	link (collection, telemetry[i]);
			link (systems,    telemetry[i]);
		}
		
		ExtendedProperties properties = new ExtendedProperties();
		
		MCTViewManifestationInfo info1 = new MCTViewManifestationInfoImpl();
		info1.setDimension(new Dimension(267, 227));
		info1.setStartPoint(new Point(35, 162));
		info1.setBorderStyle(0);
		info1.setBorderColor(Color.BLACK);
		info1.setHasTitlePanel(true);
		info1.setManifestedViewType("gov.nasa.arc.mct.fastplot.view.PlotViewRole");
		info1.setHasBorder(true);
		info1.setComponentId(collection.getComponentId());
		info1.addInfoProperty("PANEL_ORDER", "1");
		
		MCTViewManifestationInfo info2 = new MCTViewManifestationInfoImpl();
		info2.setDimension(new Dimension(226, 111));
		info2.setStartPoint(new Point(32, 27));
		info2.setBorderStyle(0);
		info2.setBorderColor(Color.BLACK);
		info2.setHasTitlePanel(true);
		info2.setManifestedViewType("gov.nasa.arc.mct.table.view.TableViewRole");
		info2.setHasBorder(true);
		info2.setComponentId(collection.getComponentId());
		info2.addInfoProperty("PANEL_ORDER", "0");
		
		properties.addProperty("CANVAS CONTENT PROPERTY", info1);
		properties.addProperty("CANVAS CONTENT PROPERTY", info2);
		display.getCapability(ComponentInitializer.class).setViewRoleProperty("gov.nasa.arc.mct.canvas.view.CanvasView", properties);
		
		workUnit.set(null);

		InternalPersistenceAccess.setPersistenceService(this);

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				updateComponentsFromDatabase();
			}
			
		}, 5000, 2000);
		
	}
	
	private void addUser(final String user, final String group) {
		users.put(user, new User() {
			@Override
			public String getUserId() {
				return user;
			}

			@Override
			public String getDisciplineId() {
				return group;
			}

			@Override
			public User getValidUser(String userID) {
				return getUser(userID);
			}			
		});
	}
	
	private void link (AbstractComponent parent, AbstractComponent child) {
		String parentId = parent.getComponentId();
		String childId  = child .getComponentId();
		if (!references.containsKey(parentId)) references.put(parentId, new ArrayList<String>());
		references.get(parentId).add(childId);
	}
	
	private AbstractComponent addComponent(String displayName, String owner, String creator, String componentClass, Tag tag) {
		String componentId = "component_" + id++;
		try {
			AbstractComponent ac = PlatformAccess.getPlatform().getComponentRegistry().newInstance(componentClass);
			ComponentInitializer ci = ac.getCapability(ComponentInitializer.class);
			ci.setCreationDate(new Date(System.currentTimeMillis()));
			ci.setCreator(creator);
			ci.setId(componentId);
			ci.setOwner(owner);
			ac.setDisplayName(displayName);
			ac.getCapability(Updatable.class).setVersion(0);
			//if (!ac.isLeaf()) ac.addDelegateComponent(AbstractComponent.NULL_COMPONENT);
			persistedComponents.put(componentId, ac);
			switch (tag) {
			case BOOTSTRAP_ALL:
				bootstrap.add(componentId);
				break;
			case BOOTSTRAP_CREATOR:
				if (!userBootstrap.containsKey(creator)) userBootstrap.put(creator, new ArrayList<String>());
				userBootstrap.get(creator).add(componentId);
				break;
			}
			return ac;
		} catch (Exception e) {
			e.printStackTrace(); //TODO log!
			return null;
		}
	}
	
	@Override
	public void startRelatedOperations() {
		workUnit.compareAndSet(null, new ArrayList<AbstractComponent>());
		//TODO: Check result, throw exception if work already started
	}

	@Override
	public void completeRelatedOperations(boolean save) {
		try {
			if (save) {
				Collection<AbstractComponent> comps = workUnit.get();
				if (comps != null) persist(comps); 
			}
		} finally {
			workUnit.set(null);
		}
	}

	@Override
	public boolean hasComponentsTaggedBy(String tagId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends AbstractComponent> T getComponent(String externalKey,
			Class<T> componentType) {
		initialize();
		for (AbstractComponent comp : persistedComponents.values()) {
			if (comp.getExternalKey().toLowerCase().equals(externalKey.toLowerCase())) {
				if (componentType.isAssignableFrom(comp.getClass())) {
					return componentType.cast(getComponent(comp.getComponentId()));
				}
			}
		}
		return null;
	}

	@Override
	public User getUser(String userId) {
		initialize();
		return users.get(userId);
	}

	@Override
	public AbstractComponent getComponent(String componentId) {
		initialize();
		//AbstractComponent comp = getComponentFromCache(componentId);
		return getComponentFromStore(componentId);
	}

	@Override
	public Collection<AbstractComponent> getReferences(
			AbstractComponent component) {
		initialize();
		List<AbstractComponent> parents = new ArrayList<AbstractComponent>();
		for (Entry<String, List<String>> entry : references.entrySet()) {
			if (entry.getValue().contains(component.getComponentId())) {
				AbstractComponent p = getComponent(entry.getKey());
				if (p != null) parents.add(p);
			}
		}
		return parents;
	}

	@Override
	public void persist(Collection<AbstractComponent> componentsToPersist) {
		for (AbstractComponent comp : componentsToPersist) {
			// TODO: Throw optimistic lock exception when versions mismatch?
			AbstractComponent clone = comp.clone();
			clone.getCapability(ComponentInitializer.class).setId(comp.getComponentId());
			clone.getCapability(Updatable.class).setVersion(comp .getVersion() + 1);
			comp .getCapability(Updatable.class).setVersion(clone.getVersion() + 1);
			List<String> children = new ArrayList<String>();
			if (!comp.isLeaf()) {
				for (AbstractComponent child : clone.getComponents()) {
					children.add(child.getComponentId());
				}
			}
			references.put(clone.getComponentId(), children);
			persistedComponents.put(comp.getComponentId(), clone);
			updated.add(comp.getComponentId());
			putComponentInCache(comp);
			comp.componentSaved();
		}
	}

	@Override
	public void delete(Collection<AbstractComponent> componentsToDelete) {
		for (AbstractComponent comp : componentsToDelete ) {
			for (Entry<String, List<String>> entry : references.entrySet()) {
				if (entry.getValue().contains(comp.getComponentId())) {
					entry.getValue().remove(comp.getComponentId());
					updated.add(entry.getKey());
				}
			}
			persistedComponents.remove(comp.getComponentId());
		}
	}

	@Override
	public List<AbstractComponent> getReferencedComponents(
			AbstractComponent component) {
		initialize();
		List<String> ref = references.get(component.getComponentId());
		if (ref == null) return Collections.emptyList();
		List<AbstractComponent> children = new ArrayList<AbstractComponent>();
		for (String r : ref){
			AbstractComponent c = getComponent(r);
			if (c != null) children.add(c);
		}
		return children;
	}

	@Override
	public Set<String> getAllUsers() {
		initialize();
		return users.keySet();
	}

	@Override
	public Collection<String> getUsersInGroup(String group) {
		initialize();
		List<String> g = new ArrayList<String>();
		
		for (Entry<String, User> entry : users.entrySet()) {
			if (entry.getValue().getDisciplineId().toLowerCase().equals(group.toLowerCase())) {
				g.add(entry.getKey());
			}
		}
		
		return g;
	}

	@Override
	public void addComponentToWorkUnit(AbstractComponent component) {
		Collection<AbstractComponent> comps = workUnit.get();
		if (comps != null) {
			synchronized (comps) {
				comps.add(component);
			}
		}
	}

	@Override
	public void updateComponentsFromDatabase() {
		initialize();
		List<AbstractComponent> maybeStale = new ArrayList<AbstractComponent>();
		for (String id : updated) {
			if (cache.containsKey(id)) {
				AbstractComponent current = getComponentFromStore(id);
				for (AbstractComponent cached : cache.get(id)) {
					maybeStale.add(cached);
					cached.getCapability(Updatable.class).setStaleByVersion(current.getVersion());
					AbstractComponent delegate = cached.getWorkUnitDelegate();
					if (delegate != null) {
						cached.getCapability(Updatable.class).setStaleByVersion(Integer.MAX_VALUE);
						maybeStale.add(delegate);
					}
				}
			}
		}
		for (final AbstractComponent comp : maybeStale) {
			if (comp.isStale()) {
				comp.resetComponentProperties(new AbstractComponent.ResetPropertiesTransaction() {
					
					@Override
					public void perform() {
						comp.getCapability(Updatable.class).notifyStale();						
					}
					
				});
			}
		}
			
		updated.clear();
	}

	@Override
	public List<AbstractComponent> getBootstrapComponents() {
		initialize();
		List<AbstractComponent> comps = new ArrayList<AbstractComponent>();
		List<String> allBootstrap = new ArrayList<String>();
		String userId = PlatformAccess.getPlatform().getCurrentUser().getUserId();
		allBootstrap.addAll(bootstrap);
		if (userBootstrap.containsKey(userId)) allBootstrap.addAll(userBootstrap.get(userId));
		for (String id : allBootstrap) {
			AbstractComponent b = getComponent(id);
			if (b != null) comps.add(b);
		}
		return comps;
	}

	@Override
	public void addNewUser(String userId, String groupId,
			AbstractComponent mysandbox, AbstractComponent dropbox) {
		
		
	}

	public Collection<AbstractComponent> search (String pattern) {
		initialize();
		String regex = pattern.toLowerCase().replaceAll("\\*", ".*");
		List<AbstractComponent> matches = new ArrayList<AbstractComponent>();
		for (AbstractComponent ac : persistedComponents.values()) {
			if (ac.getDisplayName().toLowerCase().matches(regex)) {
				matches.add(getComponent(ac.getComponentId()));
			}
		}
		return matches; //TODO: Search!
	}

	@Override
	public AbstractComponent getComponentFromStore(String componentId) {
		initialize();
		AbstractComponent comp = persistedComponents.get(componentId);
		if (comp != null) comp = comp.clone();
		else              return null;
		comp.getCapability(ComponentInitializer.class).setId(componentId);
//		if (!comp.isLeaf()) {
//			comp.getCapability(ComponentInitializer.class).setComponentReferences(Collections.singleton(AbstractComponent.NULL_COMPONENT));
//		}
		putComponentInCache(comp);
		return comp;
	}
	
	private void putComponentInCache(AbstractComponent comp) {
		String id = comp.getComponentId();
		if (!cache.containsKey(id)) {
			WeakHashMap<AbstractComponent, Boolean> map = new WeakHashMap<AbstractComponent, Boolean>();
			cache.put(id, Collections.newSetFromMap(map));
		}
		cache.get(id).add(comp);
	}
	
	private AbstractComponent getComponentFromCache(String componentId) {
		if (cache.containsKey(componentId)) {
			Set<AbstractComponent> set = cache.get(componentId);
			if (!set.isEmpty()) {
				return set.iterator().next();
			}
		}
		return null;
	}

	@Override
	public AbstractComponent getComponent(String externalKey,
			String componentType) {
		initialize();
		try {
			return (AbstractComponent) getComponent(externalKey, (Class<AbstractComponent>) Class.forName(componentType));
		} catch (ClassNotFoundException cnfe) {
			return null;
		} catch (ClassCastException cce) {
			return null;
		}
	}

	@Override
	public Map<String, ExtendedProperties> getAllProperties(String componentId) {
		initialize();
		return new HashMap<String, ExtendedProperties>();
	}
	
	@Override
	public void tagComponents(String tag,
			Collection<AbstractComponent> components) {
		for (AbstractComponent component : components) {
			if (component != null) {
				tagComponent(tag, component);
			}
		}
	}
	
	private void tagComponent(String tag, AbstractComponent component) {
		if (component == null) return;
		String id = component.getComponentId();
		List<String> target = null;
		if (tag.equals("bootstrap:admin")) {
			target = bootstrap;
		} else if (tag.equals("bootstrap:creator")) {
			String creator = component.getCreator();
			if (!userBootstrap.containsKey(creator)) {
				userBootstrap.put(creator, new ArrayList<String>());
			}
			target = userBootstrap.get(creator);
		}
		if (target != null && !target.contains(id)) {
			target.add(id);
		}
	}
}
