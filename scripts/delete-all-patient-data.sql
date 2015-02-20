delete from obs where obs_group_id is not null;
delete from obs;

delete from ebola_scheduled_dose;
delete from drug_order;
SET FOREIGN_KEY_CHECKS = 0;
delete from orders;
SET FOREIGN_KEY_CHECKS = 1;

delete from encounter_provider;
delete from encounter;

delete from visit_attribute;
delete from visit;

delete from patient_program;

delete from patient_identifier;
delete from patient;

delete from name_phonetics where person_name_id in (
    select person_name_id from person_name
    where person_id not in (select person_id from users)
      and person_id not in (select person_id from provider)
);
delete from person_name
  where person_id not in (select person_id from users)
  and person_id not in (select person_id from provider);

delete from person_attribute
  where person_id not in (select person_id from users)
  and person_id not in (select person_id from provider);

delete from person_address
  where person_id not in (select person_id from users)
  and person_id not in (select person_id from provider);

delete from person
  where person_id not in (select person_id from users)
  and person_id not in (select person_id from provider);