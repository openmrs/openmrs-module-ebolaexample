update openmrs.person  as p
inner join openmrs.patient_program as pp
on p.person_id = pp.patient_id
set p.death_date = pp.date_changed,
    p.dead = 1,
    p.cause_of_death = pp.outcome_concept_id
where pp.outcome_concept_id in (160034, 142934)
    and p.dead=0;
