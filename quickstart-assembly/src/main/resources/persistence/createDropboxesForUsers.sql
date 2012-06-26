set @userDropBoxesId = (SELECT component_id FROM component_spec where external_key = '/UserDropBoxes');
-- admin in group admin
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('My Sandbox', 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', null, 'admin', '1e069650c52f4f19a4a8af0bab50fadc', 'admin', NOW());
insert into tag_association (component_id, tag_id) select component_id, 'bootstrap:creator' from component_spec where component_id = '1e069650c52f4f19a4a8af0bab50fadc';
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('admin\'s Drop Box', 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', null, '*', '1752610348aa45d28056689907d16a4e', 'admin', NOW());
insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('1e069650c52f4f19a4a8af0bab50fadc', '1752610348aa45d28056689907d16a4e', 0);
set @userDropBoxesMaxSeq = (SELECT COALESCE(MAX(seq_no),0) FROM component_relationship where component_id = @userDropBoxesId);
insert  into component_relationship  (component_id, associated_component_id, seq_no) values (@userDropBoxesId, '1752610348aa45d28056689907d16a4e', @userDropBoxesMaxSeq + 1);
-- testUser1 in group TestUsers
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('My Sandbox', 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', null, 'testUser1', '81f77876352247d8b19ecb0924ab00e0', 'testUser1', NOW());
insert into tag_association (component_id, tag_id) select component_id, 'bootstrap:creator' from component_spec where component_id = '81f77876352247d8b19ecb0924ab00e0';
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('testUser1\'s Drop Box', 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', null, '*', 'fe1f51776af243a9901dfaa36e3f0967', 'testUser1', NOW());
insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('81f77876352247d8b19ecb0924ab00e0', 'fe1f51776af243a9901dfaa36e3f0967', 0);
set @userDropBoxesMaxSeq = (SELECT COALESCE(MAX(seq_no),0) FROM component_relationship where component_id = @userDropBoxesId);
insert  into component_relationship  (component_id, associated_component_id, seq_no) values (@userDropBoxesId, 'fe1f51776af243a9901dfaa36e3f0967', @userDropBoxesMaxSeq + 1);
-- testUser2 in group TestUsers
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('My Sandbox', 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', null, 'testUser2', 'd87ebec8cc1947f2a03804260d46f47d', 'testUser2', NOW());
insert into tag_association (component_id, tag_id) select component_id, 'bootstrap:creator' from component_spec where component_id = 'd87ebec8cc1947f2a03804260d46f47d';
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('testUser2\'s Drop Box', 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', null, '*', '8aa58dc3fc0846e3b1dab838c055e579', 'testUser2', NOW());
insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('d87ebec8cc1947f2a03804260d46f47d', '8aa58dc3fc0846e3b1dab838c055e579', 0);
set @userDropBoxesMaxSeq = (SELECT COALESCE(MAX(seq_no),0) FROM component_relationship where component_id = @userDropBoxesId);
insert  into component_relationship  (component_id, associated_component_id, seq_no) values (@userDropBoxesId, '8aa58dc3fc0846e3b1dab838c055e579', @userDropBoxesMaxSeq + 1);
-- testUser3 in group TestUsers
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('My Sandbox', 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', null, 'testUser3', '686fa075aa964e85bcfe8c28c48b85f3', 'testUser3', NOW());
insert into tag_association (component_id, tag_id) select component_id, 'bootstrap:creator' from component_spec where component_id = '686fa075aa964e85bcfe8c28c48b85f3';
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('testUser3\'s Drop Box', 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', null, '*', 'f08d651e35e74866a4cd111fc883dc5d', 'testUser3', NOW());
insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('686fa075aa964e85bcfe8c28c48b85f3', 'f08d651e35e74866a4cd111fc883dc5d', 0);
set @userDropBoxesMaxSeq = (SELECT COALESCE(MAX(seq_no),0) FROM component_relationship where component_id = @userDropBoxesId);
insert  into component_relationship  (component_id, associated_component_id, seq_no) values (@userDropBoxesId, 'f08d651e35e74866a4cd111fc883dc5d', @userDropBoxesMaxSeq + 1);
-- testUser4 in group TestUsers
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('My Sandbox', 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', null, 'testUser4', '01cb68446d6b452bb1f8a3efbad33255', 'testUser4', NOW());
insert into tag_association (component_id, tag_id) select component_id, 'bootstrap:creator' from component_spec where component_id = '01cb68446d6b452bb1f8a3efbad33255';
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('testUser4\'s Drop Box', 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', null, '*', '02dd6fe9e6354de782aabf03c72135ec', 'testUser4', NOW());
insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('01cb68446d6b452bb1f8a3efbad33255', '02dd6fe9e6354de782aabf03c72135ec', 0);
set @userDropBoxesMaxSeq = (SELECT COALESCE(MAX(seq_no),0) FROM component_relationship where component_id = @userDropBoxesId);
insert  into component_relationship  (component_id, associated_component_id, seq_no) values (@userDropBoxesId, '02dd6fe9e6354de782aabf03c72135ec', @userDropBoxesMaxSeq + 1);
-- testUser5 in group TestUsers
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('My Sandbox', 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', null, 'testUser5', '0c0e4130eaf34ee1bbe0a0027aec9b60', 'testUser5', NOW());
insert into tag_association (component_id, tag_id) select component_id, 'bootstrap:creator' from component_spec where component_id = '0c0e4130eaf34ee1bbe0a0027aec9b60';
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('testUser5\'s Drop Box', 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', null, '*', 'aa9cfe1779b64fda9fa010d4ab0f64c3', 'testUser5', NOW());
insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('0c0e4130eaf34ee1bbe0a0027aec9b60', 'aa9cfe1779b64fda9fa010d4ab0f64c3', 0);
set @userDropBoxesMaxSeq = (SELECT COALESCE(MAX(seq_no),0) FROM component_relationship where component_id = @userDropBoxesId);
insert  into component_relationship  (component_id, associated_component_id, seq_no) values (@userDropBoxesId, 'aa9cfe1779b64fda9fa010d4ab0f64c3', @userDropBoxesMaxSeq + 1);
-- testUser6 in group TestUsers
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('My Sandbox', 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', null, 'testUser6', 'dd0d6ad6da604364b98bdbfbcbe07f8b', 'testUser6', NOW());
insert into tag_association (component_id, tag_id) select component_id, 'bootstrap:creator' from component_spec where component_id = 'dd0d6ad6da604364b98bdbfbcbe07f8b';
insert into component_spec (component_name,  component_type,  model_info, owner, component_id, creator_user_id, date_created) values ('testUser6\'s Drop Box', 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', null, '*', '71b9123401fe4194b379349af3b6cc1b', 'testUser6', NOW());
insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('dd0d6ad6da604364b98bdbfbcbe07f8b', '71b9123401fe4194b379349af3b6cc1b', 0);
set @userDropBoxesMaxSeq = (SELECT COALESCE(MAX(seq_no),0) FROM component_relationship where component_id = @userDropBoxesId);
insert  into component_relationship  (component_id, associated_component_id, seq_no) values (@userDropBoxesId, '71b9123401fe4194b379349af3b6cc1b', @userDropBoxesMaxSeq + 1);
