package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.ThoiKhoaBieu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThoiKhoaBieuRepository extends JpaRepository<ThoiKhoaBieu, Integer> {
    List<ThoiKhoaBieu> findBySinhVien_IdSv(int idSv);

    List<ThoiKhoaBieu> findBySinhVien_MaSv(String maSv);

    List<ThoiKhoaBieu> findBySinhVien_IdSvAndLoaiLich(int idSv, String loaiLich);

    Optional<ThoiKhoaBieu> findFirstBySinhVien_MaSvAndLoaiLich(String maSv, String loaiLich);
}
