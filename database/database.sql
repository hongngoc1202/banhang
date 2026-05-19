-- Create Database
CREATE DATABASE IF NOT EXISTS banhang_db;
USE banhang_db;

-- Table: users (Người dùng)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255),
    role ENUM('admin', 'customer') DEFAULT 'customer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: categories (Danh mục sản phẩm)
CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: products (Sản phẩm)
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url VARCHAR(255),
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
);

-- Table: carts (Giỏ hàng)
CREATE TABLE carts (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_item (user_id, product_id)
);

-- Table: orders (Đơn hàng)
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('pending', 'confirmed', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
    payment_method VARCHAR(50),
    shipping_address VARCHAR(255),
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Table: order_details (Chi tiết đơn hàng)
CREATE TABLE order_details (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Table: warehouse (Kho hàng)
CREATE TABLE warehouse (
    warehouse_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    quantity INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY unique_warehouse (product_id)
);

-- Table: warehouse_logs (Nhật ký kho)
CREATE TABLE warehouse_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    log_type ENUM('import', 'export', 'adjustment') NOT NULL,
    quantity INT NOT NULL,
    reason VARCHAR(255),
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Table: revenue (Doanh thu)
CREATE TABLE revenue (
    revenue_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    revenue_date DATE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    INDEX idx_revenue_date (revenue_date)
);

-- Insert sample data
INSERT INTO categories (category_name, description) VALUES
('Áo thun', 'Áo thun nam nữ'),
('Áo sơ mi', 'Áo sơ mi công sở'),
('Quần jeans', 'Quần jeans thời trang'),
('Quần kaki', 'Quần kaki thoải mái'),
('Đầm váy', 'Đầm váy nữ cao cấp');

INSERT INTO users (username, password, email, full_name, phone, address, role) VALUES
('admin', SHA2('admin123', 256), 'admin@banhang.com', 'Admin Store', '0901234567', '123 Đường ABC, HCM', 'admin'),
('customer1', SHA2('pass123', 256), 'customer1@mail.com', 'Nguyễn Văn A', '0912345678', '456 Đường XYZ, HCM', 'customer'),
('customer2', SHA2('pass123', 256), 'customer2@mail.com', 'Trần Thị B', '0923456789', '789 Đường DEF, HCM', 'customer');

INSERT INTO products (category_id, product_name, description, price, stock_quantity, image_url, status) VALUES
(1, 'Áo thun nam trắng', 'Áo thun 100% cotton', 150000, 50, 'aothun_trang.jpg', 'active'),
(1, 'Áo thun nữ hồng', 'Áo thun nữ thời trang', 140000, 40, 'aothun_hong.jpg', 'active'),
(2, 'Áo sơ mi xanh', 'Áo sơ mi công sở chất lượng cao', 250000, 30, 'aosomi_xanh.jpg', 'active'),
(3, 'Quần jeans xanh đậm', 'Quần jeans nam bền đẹp', 350000, 25, 'quanjeans_xanh.jpg', 'active'),
(4, 'Quần kaki be', 'Quần kaki nam thoải mái', 280000, 35, 'quankaki_be.jpg', 'active'),
(5, 'Đầm váy xỏ', 'Đầm váy nữ dạo phố', 400000, 20, 'damvay_xo.jpg', 'active');

INSERT INTO warehouse (product_id, quantity) VALUES
(1, 50), (2, 40), (3, 30), (4, 25), (5, 35), (6, 20);
