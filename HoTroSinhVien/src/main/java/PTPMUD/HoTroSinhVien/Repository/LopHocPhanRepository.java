package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LopHocPhanRepository extends JpaRepository<LopHocPhan,Integer> {
    int countByMonHoc_IdMon(int idMon);
    List<LopHocPhan> findByMaLopHP(String maLop);

    List<LopHocPhan> findByDangKyLopHocPhan_SinhVien_MaSvAndNgayKetThucGreaterThanEqual(
                String maSv,
                LocalDate today
        );
    boolean sameMaLopHp(String maLopHp);
}
