# Các phần đã cải tiến

## 1. Giảm code lặp
- Tách logic kiểm tra trùng lịch vào `Util/LichHocValidator.java`.
- `LichAoService`, `DangKyLopHocPhanService`, `ThoiKhoaBieuService` dùng chung validator này.

## 2. Service rõ trách nhiệm hơn
- Controller chỉ nhận request và trả response.
- Logic kiểm tra dữ liệu, tìm entity, tính GPA, kiểm tra lịch được đưa vào service.

## 3. Bỏ reflection trong thời khóa biểu
- `ThoiKhoaBieuService` không còn dùng `java.lang.reflect.Method`.
- Code dễ đọc hơn vì dùng trực tiếp getter của `LopHocPhan`.

## 4. GPA an toàn hơn
- Nếu sinh viên chưa có điểm hoặc chưa có tín chỉ thì GPA trả về `0` thay vì lỗi chia cho 0.
- Thêm `ScoreSummaryDTO` thay cho anonymous object trong controller.

## 5. Xử lý lỗi gọn hơn
- Thêm `GlobalExceptionHandler` để gom lỗi chung.
- Controller không cần lặp quá nhiều đoạn `try/catch` hoặc response lỗi.

## 6. Đổi mật khẩu sạch hơn
- Đổi tên hàm `changePws` thành `changePassword`.
- Sửa route common thành `/api/common` rõ ràng hơn.

## 7. Mapper rõ ràng hơn
- `ThoiKhoaBieuMapper` map `monHoc.tenMonHoc -> tenMonHoc` để tránh lỗi thiếu `tenMonHoc`.
