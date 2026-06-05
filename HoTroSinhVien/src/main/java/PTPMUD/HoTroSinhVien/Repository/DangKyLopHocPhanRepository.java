package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
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
}
