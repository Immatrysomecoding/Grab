# Changelog

## [Đầu tiên] - DD/MM/YYYY

### Thiết lập dự án
- Tạo dự án Android mới với Empty Activity
- Cấu hình Gradle để thêm các thư viện cần thiết:
    - Google Maps và Location Services
    - Firebase Authentication
    - Retrofit cho API calls
    - Room Database
    - Glide cho tải hình ảnh
- Giảm minSdk xuống 24 để tăng độ tương thích
- Thêm Google Services plugin

  com.example.myapplication/
  ├── data/
  │   ├── api/        // API interfaces & services
  │   ├── models/     // POJO classes
  │   ├── local/      // Room DB & SharedPreferences
  │   └── repository/ // Repository pattern implementations
  ├── ui/
  │   ├── auth/       // Đăng nhập/Đăng ký screens
  │   ├── customer/   // Customer-specific screens
  │   ├── driver/     // Driver-specific screens
  │   └── common/     // Shared UI components
  ├── utils/          // Utility classes
  └── MainActivity.java