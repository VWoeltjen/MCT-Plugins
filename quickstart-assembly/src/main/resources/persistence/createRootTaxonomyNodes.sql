-- create bootstrap tags
insert into tag values('bootstrap:admin', null, 0);
insert into tag values('bootstrap:creator', null, 0);

-- Disciplines 34c90c3068854cc0a85f11ad3c2b5710
insert  into component_spec (obj_version, component_name, external_key, component_type, model_info, owner, component_id, creator_user_id, date_created) values (0, 'Disciplines', '/Disciplines', 'gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent', null, 'admin', '34c90c3068854cc0a85f11ad3c2b5710', 'admin', NOW());
insert  into tag_association values ('34c90c3068854cc0a85f11ad3c2b5710', 'bootstrap:admin', null);

-- User Drop Boxes 89720bb09af711e1a576e691270dcddc
insert  into component_spec (obj_version, component_name, external_key, component_type, model_info, owner, component_id, creator_user_id, date_created) values (0, 'User Drop Boxes', '/UserDropBoxes', 'gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent', null, 'admin', '89720bb09af711e1a576e691270dcddc', 'admin', NOW());
insert  into tag_association values ('89720bb09af711e1a576e691270dcddc', 'bootstrap:admin', null);
