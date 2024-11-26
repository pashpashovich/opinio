-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS public.abstract_users (
                                                     user_type character varying(31) NOT NULL,
                                                     id uuid PRIMARY KEY NOT NULL,
                                                     activity_type character varying(255),
                                                     password character varying(255) NOT NULL,
                                                     role character varying(255) NOT NULL,
                                                     username character varying(255) NOT NULL,
                                                     activity_name character varying(255),
                                                     birth_date date
);

CREATE UNIQUE INDEX IF NOT EXISTS uk7dflhsfmee7uyjapgdubpjev3 ON public.abstract_users USING btree (username);

-- Создание таблицы категорий
CREATE TABLE IF NOT EXISTS public.categories (
                                                 id uuid PRIMARY KEY NOT NULL,
                                                 name character varying(255) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ukt8o6pivur7nn124jehx7cygw5 ON public.categories USING btree (name);

-- Создание таблицы организаций
CREATE TABLE IF NOT EXISTS public.organizations (
                                                    description character varying(255),
                                                    name character varying(255) NOT NULL,
                                                    id uuid PRIMARY KEY NOT NULL,
                                                    FOREIGN KEY (id) REFERENCES public.abstract_users (id)
                                                        MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX IF NOT EXISTS ukp9pbw3flq9hkay8hdx3ypsldy ON public.organizations USING btree (name);

-- Создание таблицы бонусов
CREATE TABLE IF NOT EXISTS public.bonuses (
                                              id uuid PRIMARY KEY NOT NULL,
                                              created_at timestamp(6) without time zone NOT NULL,
                                              description TEXT NOT NULL,
                                              name character varying(255) NOT NULL,
                                              updated_at timestamp(6) without time zone NOT NULL,
                                              organization_id uuid NOT NULL,
                                              FOREIGN KEY (organization_id) REFERENCES public.organizations (id)
                                                  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
--djghjc
CREATE UNIQUE INDEX IF NOT EXISTS ukqvqw1rvks6ms13qgd839yeycw ON public.bonuses USING btree (name);

-- Создание таблицы опросов
CREATE TABLE IF NOT EXISTS public.polls (
                                            id uuid PRIMARY KEY NOT NULL,
                                            created_at timestamp(6) without time zone NOT NULL,
                                            description character varying(255),
                                            title character varying(255) NOT NULL,
                                            updated_at timestamp(6) without time zone NOT NULL,
                                            category_id uuid,
                                            created_by uuid,
                                            FOREIGN KEY (created_by) REFERENCES public.organizations (id)
                                                MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
                                            FOREIGN KEY (category_id) REFERENCES public.categories (id)
                                                MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Создание таблицы результатов опросов
CREATE TABLE IF NOT EXISTS public.poll_results (
                                                   id uuid PRIMARY KEY NOT NULL,
                                                   answer jsonb NOT NULL,
                                                   created_at timestamp(6) without time zone NOT NULL,
                                                   submitted_at timestamp(6) without time zone,
                                                   updated_at timestamp(6) without time zone NOT NULL,
                                                   poll_id uuid NOT NULL,
                                                   user_id uuid NOT NULL,
                                                   FOREIGN KEY (poll_id) REFERENCES public.polls (id)
                                                       MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
                                                   FOREIGN KEY (user_id) REFERENCES public.abstract_users (id)
                                                       MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Создание таблицы наград
CREATE TABLE IF NOT EXISTS public.bonus_awards (
                                                   id uuid PRIMARY KEY NOT NULL,
                                                   awarded_at timestamp(6) without time zone,
                                                   bonus_id uuid NOT NULL,
                                                   user_id uuid NOT NULL,
                                                   FOREIGN KEY (user_id) REFERENCES public.abstract_users (id)
                                                       MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
                                                   FOREIGN KEY (bonus_id) REFERENCES public.bonuses (id)
                                                       MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX IF NOT EXISTS uk7s48va9ugk5f5xjqo5aenu76q ON public.bonus_awards USING btree (user_id, bonus_id);

-- Создание таблицы связей пользователей и категорий
CREATE TABLE IF NOT EXISTS public.user_categories (
                                                      user_id uuid NOT NULL,
                                                      category_id uuid NOT NULL,
                                                      FOREIGN KEY (category_id) REFERENCES public.categories (id)
                                                          MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
                                                      FOREIGN KEY (user_id) REFERENCES public.abstract_users (id)
                                                          MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Создание таблицы связей пользователей и организаций которые ему нравки
CREATE TABLE  IF NOT EXISTS public.liked_organizations (
                                     user_id UUID NOT NULL,
                                     organization_id UUID NOT NULL,
                                     PRIMARY KEY (user_id, organization_id),
                                     FOREIGN KEY (user_id) REFERENCES abstract_users (id) ON DELETE CASCADE,
                                     FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.organization_categories (
                                         organization_id UUID NOT NULL,
                                         category_id UUID NOT NULL,
                                         PRIMARY KEY (organization_id, category_id),
                                         FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
                                         FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS public.questions (
                                                id uuid PRIMARY KEY NOT NULL,
                                                question text NOT NULL,
                                                poll_id uuid NOT NULL,
                                                FOREIGN KEY (poll_id) REFERENCES public.polls (id)
                                                    ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE public.bonuses ADD COLUMN poll_id uuid;

ALTER TABLE public.bonuses
    ADD CONSTRAINT fk_bonus_poll
        FOREIGN KEY (poll_id)
            REFERENCES public.polls (id)
            ON DELETE CASCADE ON UPDATE CASCADE;

DROP TABLE IF EXISTS public.bonus_polls;


ALTER TABLE public.organizations
    ADD COLUMN mission text,
    ADD COLUMN email character varying(255),
    ADD COLUMN phone character varying(20),
    ADD COLUMN website character varying(255),
    ADD COLUMN created_at timestamp(6) without time zone NOT NULL  ;

ALTER TABLE public.abstract_users
    ADD COLUMN address character varying(255),
    ADD COLUMN profile_picture_url character varying(255);

CREATE TABLE IF NOT EXISTS public.organization_posts (
                                                         id uuid PRIMARY KEY NOT NULL,
                                                         title character varying(255) NOT NULL,
    content text NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    organization_id uuid NOT NULL,
    FOREIGN KEY (organization_id) REFERENCES public.organizations (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS public.post_comments (
                                                    id uuid PRIMARY KEY NOT NULL,
                                                    content text NOT NULL,
                                                    created_at timestamp(6) without time zone NOT NULL,
    user_id uuid NOT NULL,
    post_id uuid NOT NULL,
    FOREIGN KEY (user_id) REFERENCES public.abstract_users (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES public.organization_posts (id) ON DELETE CASCADE
    );

ALTER TABLE public.organization_posts
    ADD COLUMN comment_count integer DEFAULT 0;

CREATE TABLE IF NOT EXISTS public.answers (
                                              id uuid PRIMARY KEY NOT NULL,
                                              answer text NOT NULL, -- Ответ на вопрос
                                              submitted_at timestamp(6) without time zone NOT NULL DEFAULT NOW(), -- Дата и время отправки ответа
    question_id uuid, -- Вопрос, на который дан ответ
    poll_id uuid, -- Опрос, к которому относится ответ
    user_id uuid, -- Пользователь, который дал ответ (может быть NULL, если анонимный)
    FOREIGN KEY (question_id) REFERENCES public.questions (id) ON DELETE CASCADE,
    FOREIGN KEY (poll_id) REFERENCES public.polls (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES public.abstract_users (id) ON DELETE CASCADE
    );

create table if not exists public.notifications
(
    id         uuid         not null
        primary key,
    created_at timestamp(6) not null,
    message    varchar(255) not null,
    read       boolean      not null,
    user_id    uuid         not null
        constraint fkcoe55js8ugtxxkbtw52s4axix
            references public.abstract_users
);

create table if not exists public.user_subscriptions
(
    user_id         uuid not null
        constraint fkl80xd7n4cesjmaq438nwshjl3
            references public.abstract_users,
    organization_id uuid not null
        constraint fkrqxv6crgqymcfwgfd2x60fjgo
            references public.organizations
);

CREATE INDEX IF NOT EXISTS idx_answers_question_poll ON public.answers (question_id, poll_id);

-- Активируем расширение pgcrypto (для gen_random_uuid) или uuid-ossp (для uuid_generate_v4)
-- Активируем расширение pgcrypto для генерации UUID
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Вставка данных в таблицу abstract_users
INSERT INTO public.abstract_users (id, user_type, activity_type, password, role, username, activity_name, birth_date, address, profile_picture_url)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'USER', 'EDUCATION', 'password1', 'USER', 'user1', 'Activity1', '1990-01-01', 'Address1', 'http://example.com/pic1.jpg'),
    ('00000000-0000-0000-0000-000000000002', 'USER', 'EDUCATION', 'password2', 'USER', 'user2', 'Activity2', '1991-02-01', 'Address2', 'http://example.com/pic2.jpg'),
    ('00000000-0000-0000-0000-000000000003', 'USER', 'EDUCATION', 'password3', 'USER', 'user3', 'Activity3', '1992-03-01', 'Address3', 'http://example.com/pic3.jpg'),
    ('00000000-0000-0000-0000-000000000004', 'USER', 'EDUCATION', 'password4', 'USER', 'user4', 'Activity4', '1993-04-01', 'Address4', 'http://example.com/pic4.jpg'),
    ('00000000-0000-0000-0000-000000000005', 'USER', 'EDUCATION', 'password5', 'USER', 'user5', 'Activity5', '1994-05-01', 'Address5', 'http://example.com/pic5.jpg');

-- Вставка данных в таблицу categories
INSERT INTO public.categories (id, name)
VALUES
    ('3422b448-2460-4fd2-9183-8000de6f8343', 'Образование'),
    ('3422b448-2460-4fd2-9183-8000de6f8344', 'Услуги'),
    ('3422b448-2460-4fd2-9183-8000de6f8345', 'Городская инфраструктура'),
    ('3422b448-2460-4fd2-9183-8000de6f8346', 'Здравоохранение'),
    ('3422b448-2460-4fd2-9183-8000de6f8347', 'Культура'),
    ('3422b448-2460-4fd2-9183-8000de6f8348', 'Продукт');


-- Вставка данных в таблицу organizations
INSERT INTO public.organizations (id, name, description, mission, email, phone, website, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Organization1', 'Description1', 'Mission1', 'org1@example.com', '123-456-7890', 'http://org1.com', NOW()),
    ('00000000-0000-0000-0000-000000000002', 'Organization2', 'Description2', 'Mission2', 'org2@example.com', '123-456-7891', 'http://org2.com', NOW()),
    ('00000000-0000-0000-0000-000000000003', 'Organization3', 'Description3', 'Mission3', 'org3@example.com', '123-456-7892', 'http://org3.com', NOW()),
    ('00000000-0000-0000-0000-000000000004', 'Organization4', 'Description4', 'Mission4', 'org4@example.com', '123-456-7893', 'http://org4.com', NOW()),
    ('00000000-0000-0000-0000-000000000005', 'Organization5', 'Description5', 'Mission5', 'org5@example.com', '123-456-7894', 'http://org5.com', NOW());

-- Вставка данных в таблицу bonuses
INSERT INTO public.bonuses (id, name, description, organization_id, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Bonus1', 'Description1', '00000000-0000-0000-0000-000000000001', NOW(), NOW()),
    (gen_random_uuid(), 'Bonus2', 'Description2', '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
    (gen_random_uuid(), 'Bonus3', 'Description3', '00000000-0000-0000-0000-000000000003', NOW(), NOW()),
    (gen_random_uuid(), 'Bonus4', 'Description4', '00000000-0000-0000-0000-000000000004', NOW(), NOW()),
    (gen_random_uuid(), 'Bonus5', 'Description5', '00000000-0000-0000-0000-000000000005', NOW(), NOW());

-- Вставка данных в таблицу polls
INSERT INTO public.polls (id, title, description, category_id, created_by, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Poll1', 'Description1', '3422b448-2460-4fd2-9183-8000de6f8343', '00000000-0000-0000-0000-000000000001', NOW(), NOW()),
    (gen_random_uuid(), 'Poll2', 'Description2', '3422b448-2460-4fd2-9183-8000de6f8344', '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
    (gen_random_uuid(), 'Poll3', 'Description3', '3422b448-2460-4fd2-9183-8000de6f8345', '00000000-0000-0000-0000-000000000003', NOW(), NOW()),
    (gen_random_uuid(), 'Poll4', 'Description4', '3422b448-2460-4fd2-9183-8000de6f8346', '00000000-0000-0000-0000-000000000004', NOW(), NOW()),
    (gen_random_uuid(), 'Poll5', 'Description5', '3422b448-2460-4fd2-9183-8000de6f8347', '00000000-0000-0000-0000-000000000005', NOW(), NOW());

-- Вставка данных в таблицу questions
INSERT INTO public.questions (id, question, poll_id)
VALUES
    (gen_random_uuid(), 'Question1', (SELECT id FROM public.polls LIMIT 1 OFFSET 0)),
    (gen_random_uuid(), 'Question2', (SELECT id FROM public.polls LIMIT 1 OFFSET 1)),
    (gen_random_uuid(), 'Question3', (SELECT id FROM public.polls LIMIT 1 OFFSET 2)),
    (gen_random_uuid(), 'Question4', (SELECT id FROM public.polls LIMIT 1 OFFSET 3)),
    (gen_random_uuid(), 'Question5', (SELECT id FROM public.polls LIMIT 1 OFFSET 4));

-- Вставка данных в таблицу poll_results
INSERT INTO public.poll_results (id, answer, poll_id, user_id, created_at, submitted_at, updated_at)
VALUES
    (gen_random_uuid(), '{"answer": "Answer1"}', (SELECT id FROM public.polls LIMIT 1 OFFSET 0), '00000000-0000-0000-0000-000000000001', NOW(), NOW(), NOW()),
    (gen_random_uuid(), '{"answer": "Answer2"}', (SELECT id FROM public.polls LIMIT 1 OFFSET 1), '00000000-0000-0000-0000-000000000002', NOW(), NOW(), NOW()),
    (gen_random_uuid(), '{"answer": "Answer3"}', (SELECT id FROM public.polls LIMIT 1 OFFSET 2), '00000000-0000-0000-0000-000000000003', NOW(), NOW(), NOW()),
    (gen_random_uuid(), '{"answer": "Answer4"}', (SELECT id FROM public.polls LIMIT 1 OFFSET 3), '00000000-0000-0000-0000-000000000004', NOW(), NOW(), NOW()),
    (gen_random_uuid(), '{"answer": "Answer5"}', (SELECT id FROM public.polls LIMIT 1 OFFSET 4), '00000000-0000-0000-0000-000000000005', NOW(), NOW(), NOW());

