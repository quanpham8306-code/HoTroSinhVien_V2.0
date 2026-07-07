package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.LichAo;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LichAoRepository extends JpaRepository<LichAo,Integer> {
    LichAo findBySinhVien_MaSv(String maSv);
    LichAo findBySinhVien (SinhVien sinhVien);
}
