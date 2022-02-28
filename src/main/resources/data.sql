insert into guest(id, name) values(null, 'Roger Federer');
insert into guest(id, name) values(null, 'Rafael Nadal');

insert into tennis_court(id, name) values(null, 'Roland Garros - Court Philippe-Chatrier');

insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (null, '2023-12-20T20:00:00.0', '2023-12-20T21:00:00.0', 1);

insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (null, '2024-12-20T20:00:00.0', '2024-12-20T21:00:00.0', 1);

insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (null, '2024-12-20T20:00:00.0', '2024-12-20T21:00:00.0', 1);

insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (null, '2025-12-20T20:00:00.0', '2025-12-20T21:00:00.0', 1);

insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (null, '2026-12-20T20:00:00.0', '2026-12-20T21:00:00.0', 1);

-- past schedule
insert
    into
        schedule
        (id, start_date_time, end_date_time, tennis_court_id)
    values
        (null, '2020-12-20T20:00:00.0', '2020-12-20T21:00:00.0', 1);

insert into reservation (id, value, reservation_status, refund_value, guest_id, schedule_id)
     values (null, 10, 0, 10, 1, 2);

insert into reservation (id, value, reservation_status, refund_value, guest_id, schedule_id)
     values (null, 10, 0, 10, 1, 3);

insert into reservation (id, value, reservation_status, refund_value, guest_id, schedule_id)
     values (null, 10, 1, 10, 1, 2);

insert into reservation (id, value, reservation_status, refund_value, guest_id, schedule_id)
     values (null, 10, 0, 10, 1, 2);

insert into reservation (id, value, reservation_status, refund_value, guest_id, schedule_id)
     values (null, 10, 0, 10, 1, 2);