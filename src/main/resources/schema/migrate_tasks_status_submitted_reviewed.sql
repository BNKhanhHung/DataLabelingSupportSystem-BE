-- Migration: Cho phép tasks.status = SUBMITTED và REVIEWED
-- Chạy script này trong Supabase SQL Editor nếu bảng tasks đã tồn tại với constraint cũ (chỉ OPEN, IN_PROGRESS, COMPLETED).

-- Xóa constraint cũ (tên có thể là tasks_status_check hoặc tương tự)
ALTER TABLE public.tasks DROP CONSTRAINT IF EXISTS tasks_status_check;

-- Thêm lại constraint với đủ 5 trạng thái: OPEN, IN_PROGRESS, SUBMITTED, REVIEWED, COMPLETED
ALTER TABLE public.tasks ADD CONSTRAINT tasks_status_check
  CHECK (status::text = ANY (ARRAY[
    'OPEN'::character varying,
    'IN_PROGRESS'::character varying,
    'SUBMITTED'::character varying,
    'REVIEWED'::character varying,
    'COMPLETED'::character varying
  ]::text[]));
