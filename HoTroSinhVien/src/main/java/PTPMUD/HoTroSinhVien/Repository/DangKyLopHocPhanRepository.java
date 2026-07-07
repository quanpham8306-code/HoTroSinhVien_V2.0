package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DangKyLopHocPhanRepository extends JpaRepository<DangKyLopHocPhan,Integer> {
    DangKyLopHocPhan findBySinhVien_IdSvAndLopHocPhan_IdLopHP(
                    int idSv,
                    int idLopHP
            );
    DangKyLopHocPhan findBySinhVien_MaSvAndLopHocPhan_MaLopHP(
            String maSv,
            String maLopHP
    );
    int countByLopHocPhan_IdLopHP(int idLopHP);

    boolean existsBySinhVien_IdSvAndLopHocPhan_IdLopHP(int idSv, int idLopHP);

    boolean existsBySinhVien_MaSvAndLopHocPhan_MaLopHP(String maSv,String maLopHP);

    boolean existsByLopHocPhan_MaLopHP(String maLopHP);

    List<DangKyLopHocPhan> findByLopHocPhan_MaLopHP(String maLopHP);

    List<DangKyLopHocPhan> findBySinhVien_IdSv(int idSv);

    List<DangKyLopHocPhan> findBySinhVien_MaSvAndLopHocPhan_HocKyAndLopHocPhan_NamHoc(String maSv, int hocKy, String namHoc);

    List<DangKyLopHocPhan> findBySinhVien_MaSv(String maSv);

    List<DangKyLopHocPhan> findBySinhVien_MaSvAndLopHocPhan_Thu(String maSv,int thu);


}
