-- Schema bảng data_items cho Supabase / PostgreSQL
-- Chạy trong Supabase SQL Editor nếu cần tạo/cập nhật bảng cho khớp với backend.

-- Bảng datasets phải tồn tại trước (FK từ data_items -> datasets)
-- Nếu chưa có, tạo projects trước, rồi datasets.

-- Tạo bảng data_items (chạy nếu bảng chưa có)
CREATE TABLE IF NOT EXISTS public.data_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dataset_id UUID NOT NULL REFERENCES public.datasets(id) ON DELETE CASCADE,
    content_url VARCHAR(1000) NOT NULL,
    metadata TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    CONSTRAINT uq_dataitem_url_dataset UNIQUE (dataset_id, content_url)
);

-- Chỉ mục cho truy vấn theo dataset và status
CREATE INDEX IF NOT EXISTS idx_data_items_dataset_id ON public.data_items(dataset_id);
CREATE INDEX IF NOT EXISTS idx_data_items_status ON public.data_items(status);
CREATE INDEX IF NOT EXISTS idx_data_items_created_at ON public.data_items(created_at DESC);

-- RLS (Row Level Security) tùy chọn cho Supabase – bật nếu dùng Supabase Auth
-- ALTER TABLE public.data_items ENABLE ROW LEVEL SECURITY;

COMMENT ON TABLE public.data_items IS 'Data items thuộc dataset, dùng cho gán nhãn (annotation).';
COMMENT ON COLUMN public.data_items.status IS 'NEW | ASSIGNED | ANNOTATED | REVIEWED';

-- Nếu bảng đã tồn tại, có thể chạy các lệnh sau để chỉnh cho khớp backend:
-- ALTER TABLE public.data_items ALTER COLUMN content_url TYPE VARCHAR(1000);
-- ALTER TABLE public.data_items ALTER COLUMN status TYPE VARCHAR(50);
-- ALTER TABLE public.data_items ALTER COLUMN metadata DROP NOT NULL;  -- nếu đang NOT NULL
-- CREATE UNIQUE INDEX IF NOT EXISTS uq_dataitem_url_dataset ON public.data_items(dataset_id, content_url);
