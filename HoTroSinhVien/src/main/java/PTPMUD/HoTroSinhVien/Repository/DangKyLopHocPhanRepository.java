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
    int countByLopHocPhan_IdLopHP(int idLopHP);

    boolean existsBySinhVien_IdSvAndLopHocPhan_IdLopHP(int idSv, int idLopHP);

    List<DangKyLopHocPhan> findBySinhVien_IdSv(int idSv);

    List<DangKyLopHocPhan> findBySinhVien_MaSvAndLopHocPhan_HocKy(String maSv, int hocKy);

    List<DangKyLopHocPhan> findBySinhVien_MaSv(String maSv);

    List<DangKyLopHocPhan> findBySinhVien_MaSvAndLopHocPhan_Thu(String maSv,int thu);


}
