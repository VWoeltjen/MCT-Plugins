-- admin
set @rootDisciplineId = (SELECT component_id FROM component_spec where external_key = '/Disciplines');
insert into component_spec (obj_version, component_name, external_key, component_type,  model_info, owner, component_id, creator_user_id, date_created) values (0, 'admin', null, 'gov.nasa.arc.mct.core.components.TelemetryDisciplineComponent', null, 'admin', '95236fd3190940b39c975606d8996e26','admin', NOW());
set @parentMaxSeq = ifnull(((SELECT MAX(seq_no) FROM component_relationship where component_id = @rootDisciplineId)) , 0);
insert into component_relationship (component_id, seq_no, associated_component_id) values (@rootDisciplineId, @parentMaxSeq + 1, '95236fd3190940b39c975606d8996e26');
set @lastObjVersion = (SELECT max(obj_version) FROM  component_spec where component_id=@rootDisciplineId);
update component_spec set obj_version = (@lastObjVersion + 1) where component_id=@rootDisciplineId;
-- TestUsers
set @rootDisciplineId = (SELECT component_id FROM component_spec where external_key = '/Disciplines');
insert into component_spec (obj_version, component_name, external_key, component_type,  model_info, owner, component_id, creator_user_id, date_created) values (0, 'TestUsers', null, 'gov.nasa.arc.mct.core.components.TelemetryDisciplineComponent', null, 'admin', 'c052596aad864794b336d91af97acd5d','admin', NOW());
set @parentMaxSeq = ifnull(((SELECT MAX(seq_no) FROM component_relationship where component_id = @rootDisciplineId)) , 0);
insert into component_relationship (component_id, seq_no, associated_component_id) values (@rootDisciplineId, @parentMaxSeq + 1, 'c052596aad864794b336d91af97acd5d');
set @lastObjVersion = (SELECT max(obj_version) FROM  component_spec where component_id=@rootDisciplineId);
update component_spec set obj_version = (@lastObjVersion + 1) where component_id=@rootDisciplineId;
