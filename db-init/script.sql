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
                                              description text NOT NULL,
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

-- Создание таблицы связей бонусов и опросов
CREATE TABLE IF NOT EXISTS public.bonus_polls (
                                                  bonus_id uuid NOT NULL,
                                                  poll_id uuid NOT NULL,
                                                  FOREIGN KEY (bonus_id) REFERENCES public.bonuses (id)
                                                      MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
                                                  FOREIGN KEY (poll_id) REFERENCES public.polls (id)
                                                      MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Создание таблицы связей пользователей и категорий
CREATE TABLE IF NOT EXISTS public.user_categories (
                                                      user_id uuid NOT NULL,
                                                      category_id uuid NOT NULL,
                                                      FOREIGN KEY (category_id) REFERENCES public.categories (id)
                                                          MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
                                                      FOREIGN KEY (user_id) REFERENCES public.abstract_users (id)
                                                          MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
