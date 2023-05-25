ALTER SEQUENCE security.users_id_seq RESTART WITH 1;

INSERT INTO security.role (role)
VALUES ('ADMIN'),
       ('JOURNALIST'),
       ('SUBSCRIBER');

INSERT INTO security.users (login, password, email, first_name, last_name, role)
VALUES ('Siarhey1987', '$2a$10$6sON22ecWM4I0lYgtk39g.5knW16kclM..9cmH5PIw87nRavno/4.', 'siarhey@siarhey.by', 'Siarhey', 'Babrovich', 'SUBSCRIBER');
