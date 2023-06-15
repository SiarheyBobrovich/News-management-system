ALTER SEQUENCE news.comment_id_seq RESTART WITH 1;
ALTER SEQUENCE news.news_id_seq RESTART WITH 1;

INSERT INTO news.news (time, title, text, author)
VALUES ('2023-01-01 01:01:01', 'First news', 'First text', 'journalist'),
       ('2023-02-02 02:02:02', 'Second news', 'Second text', 'journalist1');

INSERT INTO news.comment (time, text, username, news_id)
VALUES ('2023-01-01 01:01:01', 'First comment', 'user1', 1),
       ('2023-02-02 02:02:02', 'Second comment', 'user2', 1);