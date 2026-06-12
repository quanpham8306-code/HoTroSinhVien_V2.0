package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LopHocPhanRepository extends JpaRepository<LopHocPhan,Integer> {
    int countByMonHoc_IdMon(int idMon);
    LopHocPhan findByMaLopHP(String maLop);
}
