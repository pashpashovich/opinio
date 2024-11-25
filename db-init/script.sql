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
    question_id uuid NOT NULL, -- Вопрос, на который дан ответ
    poll_id uuid NOT NULL, -- Опрос, к которому относится ответ
    user_id uuid, -- Пользователь, который дал ответ (может быть NULL, если анонимный)
    FOREIGN KEY (question_id) REFERENCES public.questions (id) ON DELETE CASCADE,
    FOREIGN KEY (poll_id) REFERENCES public.polls (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES public.abstract_users (id) ON DELETE CASCADE
    );
INSERT INTO public.categories (id, name) VALUES
                                             (gen_random_uuid(), 'Городская инфраструктура'),
                                             (gen_random_uuid(), 'Здравоохранение'),
                                             (gen_random_uuid(), 'Образование'),
                                             (gen_random_uuid(), 'Культура'),
                                             (gen_random_uuid(), 'Продукт'),
                                             (gen_random_uuid(), 'Услуги')
ON CONFLICT DO NOTHING;