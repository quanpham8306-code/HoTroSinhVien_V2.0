package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.MonHoc;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;

public interface MonHocRepository extends JpaRepository<MonHoc, Integer> {
    public MonHoc findBytenMonHoc(String tenMonHoc);
    MonHoc findByMaMon(String maMon);
    //List<MonHoc> findByLopHocPhan_NganhAndLopHocPhan_Khoa (String nganh,String khoa);
    List<MonHoc> findByLopHocPhans_NganhAndLopHocPhans_KhoaAndLopHocPhans_NgayBatDauAfter(
            String nganh,
            String khoa,
            LocalDate ngay
    );
}
