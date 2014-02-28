# Migration script for nasa/MCT-Plugins#134
# May need to re-run if older distributions are used,
# as these may restore/inject old taxonomy.

# Specifically, this migrates from databases used at commit hash:
#     e1949eb9a890411946782ad581e87fa8f0220106
# to:
#     a02daf7a2bd5cab129401085e4ee51370987ac86

# Clear out old bootstrap components
DELETE FROM tag_association WHERE component_id LIKE '%repo%';

# Update base display names (e.g. User Tags -> My Tags)
UPDATE component_spec
   SET component_name = replace(component_name, 'User', 'My')
 WHERE component_name LIKE 'User %'
   AND component_type LIKE 'gov.nasa.arc.mct.scenario.component.%RepositoryComponent';