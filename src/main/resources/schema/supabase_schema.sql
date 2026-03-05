-- Schema Supabase (PostgreSQL) – thứ tự đúng để chạy, khớp với backend.
-- Chạy trong Supabase SQL Editor. WARNING: Dùng cho context/migration; nếu DB đã có bảng thì dùng ALTER hoặc bỏ qua.

-- 1. Bảng không phụ thuộc FK
CREATE TABLE IF NOT EXISTS public.projects (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  name character varying NOT NULL,
  description character varying,
  created_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  CONSTRAINT projects_pkey PRIMARY KEY (id),
  CONSTRAINT projects_name_unique UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.users (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  username character varying NOT NULL,
  email character varying NOT NULL,
  password_hash character varying NOT NULL,
  status character varying NOT NULL,
  system_role character varying NOT NULL CHECK (system_role::text = ANY (ARRAY['USER'::character varying, 'ADMIN'::character varying, 'MANAGER'::character varying]::text[])),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.roles (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  name character varying NOT NULL,
  description character varying,
  CONSTRAINT roles_pkey PRIMARY KEY (id),
  CONSTRAINT roles_name_unique UNIQUE (name)
);

-- 2. Phụ thuộc projects
CREATE TABLE IF NOT EXISTS public.datasets (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  project_id uuid NOT NULL,
  name character varying NOT NULL,
  description character varying,
  created_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  CONSTRAINT datasets_pkey PRIMARY KEY (id),
  CONSTRAINT fk_datasets_project FOREIGN KEY (project_id) REFERENCES public.projects(id)
);

CREATE TABLE IF NOT EXISTS public.labels (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  project_id uuid NOT NULL,
  name character varying NOT NULL,
  description text,
  color character varying,
  created_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  updated_at timestamp without time zone,
  CONSTRAINT labels_pkey PRIMARY KEY (id),
  CONSTRAINT fk_labels_project FOREIGN KEY (project_id) REFERENCES public.projects(id)
);

-- 3. Phụ thuộc users, projects
CREATE TABLE IF NOT EXISTS public.tasks (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  project_id uuid NOT NULL,
  annotator_id uuid NOT NULL,
  reviewer_id uuid NOT NULL,
  status character varying NOT NULL CHECK (status::text = ANY (ARRAY['OPEN'::character varying, 'IN_PROGRESS'::character varying, 'SUBMITTED'::character varying, 'REVIEWED'::character varying, 'DENIED'::character varying, 'COMPLETED'::character varying]::text[])),
  created_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  CONSTRAINT tasks_pkey PRIMARY KEY (id),
  CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES public.projects(id),
  CONSTRAINT fk_tasks_annotator FOREIGN KEY (annotator_id) REFERENCES public.users(id),
  CONSTRAINT fk_tasks_reviewer FOREIGN KEY (reviewer_id) REFERENCES public.users(id)
);

-- 4. Phụ thuộc datasets
CREATE TABLE IF NOT EXISTS public.data_items (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  dataset_id uuid NOT NULL,
  content_url character varying NOT NULL,
  metadata text,
  status character varying NOT NULL DEFAULT 'NEW'::character varying CHECK (status::text = ANY (ARRAY['NEW'::character varying, 'ASSIGNED'::character varying, 'ANNOTATED'::character varying, 'REVIEWED'::character varying]::text[])),
  created_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  CONSTRAINT data_items_pkey PRIMARY KEY (id),
  CONSTRAINT fk_data_items_dataset FOREIGN KEY (dataset_id) REFERENCES public.datasets(id)
);

-- 5. Phụ thuộc tasks, data_items
CREATE TABLE IF NOT EXISTS public.task_items (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  task_id uuid NOT NULL,
  data_item_id uuid NOT NULL,
  CONSTRAINT task_items_pkey PRIMARY KEY (id),
  CONSTRAINT fk_task_items_task FOREIGN KEY (task_id) REFERENCES public.tasks(id),
  CONSTRAINT fk_task_items_data_item FOREIGN KEY (data_item_id) REFERENCES public.data_items(id)
);

-- 6. Phụ thuộc users, task_items
CREATE TABLE IF NOT EXISTS public.annotations (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  task_item_id uuid NOT NULL,
  annotator_id uuid NOT NULL,
  content text NOT NULL,
  status character varying NOT NULL CHECK (status::text = ANY (ARRAY['SUBMITTED'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying]::text[])),
  created_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  updated_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  CONSTRAINT annotations_pkey PRIMARY KEY (id),
  CONSTRAINT annotations_task_item_id_unique UNIQUE (task_item_id),
  CONSTRAINT fk_annotations_task_item FOREIGN KEY (task_item_id) REFERENCES public.task_items(id),
  CONSTRAINT fk_annotations_annotator FOREIGN KEY (annotator_id) REFERENCES public.users(id)
);

-- 7. Phụ thuộc annotations, users
CREATE TABLE IF NOT EXISTS public.review_feedbacks (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  annotation_id uuid NOT NULL,
  reviewer_id uuid NOT NULL,
  status character varying NOT NULL CHECK (status::text = ANY (ARRAY['APPROVED'::character varying, 'REJECTED'::character varying]::text[])),
  comment text,
  created_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  CONSTRAINT review_feedbacks_pkey PRIMARY KEY (id),
  CONSTRAINT review_feedbacks_annotation_id_unique UNIQUE (annotation_id),
  CONSTRAINT fk_review_feedbacks_annotation FOREIGN KEY (annotation_id) REFERENCES public.annotations(id),
  CONSTRAINT fk_review_feedbacks_reviewer FOREIGN KEY (reviewer_id) REFERENCES public.users(id)
);

-- 8. Phụ thuộc users, roles, projects (user_roles trong Supabase có project_id)
CREATE TABLE IF NOT EXISTS public.user_roles (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  role_id uuid NOT NULL,
  project_id uuid NOT NULL,
  assigned_at timestamp without time zone NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
  CONSTRAINT user_roles_pkey PRIMARY KEY (id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES public.users(id),
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES public.roles(id),
  CONSTRAINT fk_user_roles_project FOREIGN KEY (project_id) REFERENCES public.projects(id)
);

-- Chỉ mục gợi ý
CREATE INDEX IF NOT EXISTS idx_data_items_dataset_id ON public.data_items(dataset_id);
CREATE INDEX IF NOT EXISTS idx_data_items_status ON public.data_items(status);
CREATE INDEX IF NOT EXISTS idx_task_items_task_id ON public.task_items(task_id);
CREATE INDEX IF NOT EXISTS idx_annotations_task_item_id ON public.annotations(task_item_id);
CREATE INDEX IF NOT EXISTS idx_review_feedbacks_reviewer_id ON public.review_feedbacks(reviewer_id);
