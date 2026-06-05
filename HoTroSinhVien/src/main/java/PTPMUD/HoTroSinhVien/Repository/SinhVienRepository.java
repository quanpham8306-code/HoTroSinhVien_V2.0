package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinhVienRepository extends JpaRepository<SinhVien,Integer> {
    public SinhVien findByMaSv(String maSv);
    public boolean existsSinhVienByCccd(String cccd);
    public boolean existsSinhVienByEmail(String email);
}
