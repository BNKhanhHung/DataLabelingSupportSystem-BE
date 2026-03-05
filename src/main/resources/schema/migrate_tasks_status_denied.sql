-- Migration: Thêm trạng thái DENIED cho tasks.status (Reviewer từ chối nhãn)
-- Chạy trong Supabase SQL Editor.

ALTER TABLE public.tasks DROP CONSTRAINT IF EXISTS tasks_status_check;

ALTER TABLE public.tasks ADD CONSTRAINT tasks_status_check
  CHECK (status::text = ANY (ARRAY[
    'OPEN'::character varying,
    'IN_PROGRESS'::character varying,
    'SUBMITTED'::character varying,
    'REVIEWED'::character varying,
    'DENIED'::character varying,
    'COMPLETED'::character varying
  ]::text[]));
