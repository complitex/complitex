select * from street_type st
left join street_type_attribute sta on (sta.`object_id` = st.`object_id`)
left join street_type_string_culture stsc on (sta.`value_id` = stsc.`id`);