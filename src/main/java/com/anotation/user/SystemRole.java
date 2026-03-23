package com.anotation.user;

/**
 * Vai trò cấp hệ thống (system-level) lưu trên entity {@link User}, tách biệt với vai trò theo dự án
 * (Manager/Annotator/Reviewer) trong bảng user-role.
 * <ul>
 *   <li>{@link #USER} — mặc định; có thể được gán thêm vai trò dự án qua {@code UserRole}.</li>
 *   <li>{@link #MANAGER} — quản lý dự án/tài nguyên nghiệp vụ gán nhãn.</li>
 *   <li>{@link #ADMIN} — quản trị tài khoản và phân quyền; không đảm nhiệm toàn bộ tài nguyên dự án theo thiết kế hiện tại.</li>
 * </ul>
 */
public enum SystemRole {
    /** Người dùng thông thường; có thể có các role dự án bổ sung. */
    USER,
    /** Quản lý cấp hệ thống/dự án theo nghiệp vụ ứng dụng. */
    MANAGER,
    /** Quản trị viên: tập trung quản lý user và role (theo mô tả nghiệp vụ API). */
    ADMIN
}
