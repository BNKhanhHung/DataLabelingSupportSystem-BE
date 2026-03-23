package com.anotation.config;

import com.anotation.dataitem.DataItem;
import com.anotation.dataitem.DataItemRepository;
import com.anotation.dataitem.DataItemStatus;
import com.anotation.dataset.Dataset;
import com.anotation.dataset.DatasetRepository;
import com.anotation.label.Label;
import com.anotation.label.LabelRepository;
import com.anotation.project.Project;
import com.anotation.project.ProjectRepository;
import com.anotation.role.Role;
import com.anotation.role.RoleRepository;
import com.anotation.user.SystemRole;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import com.anotation.userrole.UserRole;
import com.anotation.userrole.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * {@link org.springframework.boot.CommandLineRunner} chạy sau khi context Spring khởi động: seed dữ liệu demo nếu chưa có user {@code admin}.
 * Tạo user mẫu (admin, manager, annotator1, reviewer1) với {@link org.springframework.security.crypto.password.PasswordEncoder}, role hệ thống tương ứng.
 * Khởi tạo role nghiệp vụ trong DB (MANAGER, ANNOTATOR, REVIEWER), một project, gán {@link com.anotation.userrole.UserRole}, nhãn mẫu, dataset và vài {@link com.anotation.dataitem.DataItem} (URL ảnh minh họa).
 * Nếu {@code admin} đã tồn tại thì ghi log và thoát sớm, tránh trùng lặp dữ liệu khi restart.
 * Mục đích: môi trường dev/demo có sẵn luồng gán nhãn và review mà không cần nhập tay toàn bộ.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectRepository projectRepository;
    private final DatasetRepository datasetRepository;
    private final DataItemRepository dataItemRepository;
    private final LabelRepository labelRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,
            ProjectRepository projectRepository, DatasetRepository datasetRepository,
            DataItemRepository dataItemRepository, LabelRepository labelRepository,
            UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.projectRepository = projectRepository;
        this.datasetRepository = datasetRepository;
        this.dataItemRepository = dataItemRepository;
        this.labelRepository = labelRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("Dữ liệu hệ thống đã tồn tại, bỏ qua DataInitializer.");
            return;
        }

        log.info("=== BẮT ĐẦU KHỞI TẠO MOCK DATA DEMO ===");

        // 1. Khởi tạo System Users
        User admin = createUser("admin", "admin@system.com", "adminpassword", SystemRole.ADMIN);
        User manager = createUser("manager", "manager@system.com", "managerpassword", SystemRole.MANAGER);
        User annotator1 = createUser("annotator1", "anno1@system.com", "123456", SystemRole.USER);
        User reviewer1 = createUser("reviewer1", "rev1@system.com", "123456", SystemRole.USER);

        log.info("1. Đã tạo Users (admin, manager, annotator1, reviewer1)");

        // 2. Khởi tạo Roles trong Database
        Role rManager = createRole("MANAGER", "Quản lý dự án");
        Role rAnnotator = createRole("ANNOTATOR", "Người gán nhãn");
        Role rReviewer = createRole("REVIEWER", "Người kiểm duyệt");

        log.info("2. Đã tạo DB Roles (MANAGER, ANNOTATOR, REVIEWER)");

        // 3. Khởi tạo Project
        Project project = new Project();
        project.setName("Dự án Demo - Phân loại Ảnh Chó Mèo");
        project.setDescription("Dự án hệ thống phân loại học có giám sát.");
        projectRepository.save(project);

        log.info("3. Đã tạo Project: " + project.getName());

        // 4. Phân Quyền Vô Project (UserRoles)
        assignUserToProject(manager, rManager);
        assignUserToProject(annotator1, rAnnotator);
        assignUserToProject(reviewer1, rReviewer);

        log.info("4. Đã phân quyền Manager, Annotator, Reviewer vào Project");

        // 5. Tạo Labels cho Project
        Label labelDog = new Label();
        labelDog.setName("Chó");
        labelDog.setDescription("Động vật thuộc loài chó");
        labelDog.setColor("#FF5733");
        labelDog.setProject(project);

        Label labelCat = new Label();
        labelCat.setName("Mèo");
        labelCat.setDescription("Động vật thuộc loài mèo");
        labelCat.setColor("#33AFFF");
        labelCat.setProject(project);

        labelRepository.save(labelDog);
        labelRepository.save(labelCat);

        log.info("5. Đã tạo Labels (Chó, Mèo)");

        // 6. Tạo Dataset
        Dataset dataset = new Dataset();
        dataset.setName("Dataset Hình Ảnh Chó Mèo");
        dataset.setDescription("Tập dữ liệu huấn luyện phân loại");
        dataset.setProject(project);
        datasetRepository.save(dataset);

        log.info("6. Đã tạo Dataset");

        // 7. Tạo Data Items (Mock URL images)
        createDataItem(dataset, "https://images.unsplash.com/photo-1543466835-00a7907e9de1"); // Dog
        createDataItem(dataset, "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba"); // Cat
        createDataItem(dataset, "https://images.unsplash.com/photo-1537151608804-ea2a14a14074"); // Dog
        createDataItem(dataset, "https://images.unsplash.com/photo-1495360010541-f48722b34f7d"); // Cat

        log.info("7. Đã tạo 4 Data Items chứa Image URL minh họa");

        log.info("=== HOÀN TẤT KHỞI TẠO MOCK DATA DEMO ===");
    }

    private User createUser(String username, String email, String password, SystemRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus("ACTIVE");
        user.setSystemRole(role);
        return userRepository.save(user);
    }

    private Role createRole(String name, String description) {
        Optional<Role> existingRole = roleRepository.findByName(name);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    private void assignUserToProject(User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    private void createDataItem(Dataset dataset, String url) {
        DataItem item = new DataItem();
        item.setDataset(dataset);
        item.setContentUrl(url);
        item.setStatus(DataItemStatus.NEW);
        dataItemRepository.save(item);
    }
}
