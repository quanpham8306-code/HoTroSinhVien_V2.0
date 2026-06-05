package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.ChiTietThoiKhoaBieu;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietThoiKhoaBieuRepository extends JpaRepository<ChiTietThoiKhoaBieu,Integer> {
}
