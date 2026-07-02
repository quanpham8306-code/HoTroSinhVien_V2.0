package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.ChiTietThoiKhoaBieu;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.ThoiKhoaBieu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietThoiKhoaBieuRepository extends JpaRepository<ChiTietThoiKhoaBieu,Integer> {

    List<ChiTietThoiKhoaBieu> findByLopHocPhan(LopHocPhan lopHocPhan);
    List<ChiTietThoiKhoaBieu> findByThoiKhoaBieu(ThoiKhoaBieu thoiKhoaBieu);

    void deleteByThoiKhoaBieu(ThoiKhoaBieu thoiKhoaBieu);

    boolean existsByThoiKhoaBieuAndLopHocPhan(
            ThoiKhoaBieu thoiKhoaBieu,
            LopHocPhan lopHocPhan
    );
}
