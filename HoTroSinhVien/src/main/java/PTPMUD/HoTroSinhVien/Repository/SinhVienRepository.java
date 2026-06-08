package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SinhVienRepository extends JpaRepository<SinhVien,Integer> {
    public SinhVien findByMaSv(String maSv);
    public boolean existsSinhVienByCccd(String cccd);
    public boolean existsSinhVienByEmail(String email);
    boolean existsByMaSv(String maSv);
    Optional<SinhVien> findTopByMaSvStartingWithOrderByMaSvDesc(String prefix);


}
