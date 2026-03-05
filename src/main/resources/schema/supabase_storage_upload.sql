-- =============================================================================
-- Cấu hình Supabase Storage để backend có thể upload file từ máy lên
-- Chạy toàn bộ script này trong Supabase SQL Editor (Dashboard → SQL Editor).
-- =============================================================================

-- 1. Tạo bucket "data-items" (tên phải trùng với SUPABASE_STORAGE_BUCKET trong backend)
--    public = true: file upload xong có thể truy cập qua URL public (để annotator xem ảnh)
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
  'data-items',
  'data-items',
  true,
  10485760,  -- 10MB, khớp với spring.servlet.multipart.max-file-size
  null      -- null = cho phép mọi loại file (ảnh, pdf, ...)
)
ON CONFLICT (id) DO UPDATE SET
  public = EXCLUDED.public,
  file_size_limit = EXCLUDED.file_size_limit,
  allowed_mime_types = EXCLUDED.allowed_mime_types;

-- 2. Policy: Cho phép mọi người đọc file trong bucket (cần cho URL public khi public = true)
DROP POLICY IF EXISTS "Public read data-items" ON storage.objects;
CREATE POLICY "Public read data-items"
ON storage.objects FOR SELECT
TO public
USING (bucket_id = 'data-items');

-- 3. Policy: Cho phép upload (INSERT) vào bucket data-items
--    Backend dùng service_role key nên thường bỏ qua RLS; policy này hỗ trợ khi gọi từ client hoặc API key khác
DROP POLICY IF EXISTS "Allow upload data-items" ON storage.objects;
CREATE POLICY "Allow upload data-items"
ON storage.objects FOR INSERT
TO public
WITH CHECK (bucket_id = 'data-items');

-- 4. (Tùy chọn) Policy: Cho phép cập nhật/xóa object trong bucket (upsert, xóa file)
DROP POLICY IF EXISTS "Allow update data-items" ON storage.objects;
CREATE POLICY "Allow update data-items"
ON storage.objects FOR UPDATE
TO public
USING (bucket_id = 'data-items')
WITH CHECK (bucket_id = 'data-items');

DROP POLICY IF EXISTS "Allow delete data-items" ON storage.objects;
CREATE POLICY "Allow delete data-items"
ON storage.objects FOR DELETE
TO public
USING (bucket_id = 'data-items');

-- =============================================================================
-- Sau khi chạy xong, cấu hình backend (application.yml hoặc biến môi trường):
--
--   SUPABASE_URL=https://xxxxx.supabase.co
--   SUPABASE_SERVICE_KEY=eyJhbGc...   (Project Settings → API → service_role secret)
--   SUPABASE_STORAGE_BUCKET=data-items
--
-- Rồi khởi động lại backend và thử upload file từ trang "Thêm Data Item".
-- =============================================================================
