-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, role, enabled)
VALUES ('admin', 'admin@officesupply.com', '$2a$10$slYQmyNdGzin7olVnq/Fe.0kwtsbVgror.kj5T0C3xNaEZ3gCc3m', 'Admin User', 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample employee user (password: emp123)
INSERT INTO users (username, email, password, full_name, role, enabled)
VALUES ('employee1', 'employee1@officesupply.com', '$2a$10$Vd93wq5EkKzFVwkgVKCXDOEzwGbCp7uE.qF4yL3F4K9nP2M1R8jZC', 'Employee One', 'EMPLOYEE', TRUE)
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample inventory items
INSERT INTO inventory (item_name, quantity, reorder_level, created_at, updated_at)
VALUES 
  ('Printer Paper (A4)', 500, 50, NOW(), NOW()),
  ('Ballpoint Pens', 200, 20, NOW(), NOW()),
  ('Markers', 150, 15, NOW(), NOW()),
  ('Sticky Notes', 100, 10, NOW(), NOW()),
  ('Notebooks', 75, 10, NOW(), NOW())
ON DUPLICATE KEY UPDATE id=id;
