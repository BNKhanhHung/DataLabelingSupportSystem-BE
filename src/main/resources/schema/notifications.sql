-- Bảng notifications cho Supabase / PostgreSQL
-- Chạy trong Supabase SQL Editor (Dashboard → SQL Editor → New query).

-- Bảng users phải tồn tại trước (FK notifications -> users)
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    type VARCHAR(80) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    related_entity_type VARCHAR(50),
    related_entity_id UUID,
    read BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON public.notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON public.notifications(read);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON public.notifications(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_user_read ON public.notifications(user_id, read);

COMMENT ON TABLE public.notifications IS 'Thông báo cho user: task phân công, trạng thái task, trễ deadline.';
COMMENT ON COLUMN public.notifications.type IS 'TASK_ASSIGNED | TASK_STATUS_UPDATED | DEADLINE_OVERDUE_TASK | DEADLINE_OVERDUE_PROJECT';
COMMENT ON COLUMN public.notifications.related_entity_type IS 'TASK | PROJECT';
