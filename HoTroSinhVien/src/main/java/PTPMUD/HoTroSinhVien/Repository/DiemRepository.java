package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiemRepository extends JpaRepository<Diem, Integer> {
    List<Diem> findByDangKyLopHocPhan_SinhVien_IdSv(int idSv);
    List<Diem> findByDangKyLopHocPhan_SinhVien_IdSvAndDangKyLopHocPhan_LopHocPhan_HocKy(
            int idSv,
            int hocKy
    );

    List<Diem> findByDangKyLopHocPhan_SinhVien_MaSvAndDangKyLopHocPhan_LopHocPhan_HocKy(
            String maSv,
            int hocKy
    );
    List<Diem> findByDangKyLopHocPhan_SinhVien_MaSvAndDangKyLopHocPhan_LopHocPhan_MaLopHP(
            String maSv,
            String maLopHP
    );
    List<Diem> findByDangKyLopHocPhan_SinhVien_MaSvAndDangKyLopHocPhan_LopHocPhan_HocKyAndDangKyLopHocPhan_LopHocPhan_NamHoc(
            String maSv,
            int hocKy,
            String namHoc
    );
    List<Diem> findByDangKyLopHocPhan_SinhVien_MaSv(String maSv);
    Diem findByDangKyLopHocPhan(DangKyLopHocPhan dangKyHocPhan);
}
