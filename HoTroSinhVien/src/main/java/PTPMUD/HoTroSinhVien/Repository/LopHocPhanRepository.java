package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LopHocPhanRepository extends JpaRepository<LopHocPhan,Integer> {
    int countByMonHoc_IdMon(int idMon);
    LopHocPhan findByMaLopHP(String maLop);
    List<LopHocPhan> findByHocKyAndMonHoc_TenMonHoc(int hocKy,String tenMonHoc);
    @Query("SELECT MAX(l.hocKy) FROM LopHocPhan l")
    Integer findMaxHocKy();
    List<LopHocPhan> findByNgayBatDauAfterAndKhoaAndNganhAndMonHoc_MaMon(LocalDate ngay,String khoa,String nganh,String mamMon);
}
