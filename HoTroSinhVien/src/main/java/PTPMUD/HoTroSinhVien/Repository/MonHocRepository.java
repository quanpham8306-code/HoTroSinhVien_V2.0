package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.MonHoc;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface MonHocRepository extends JpaRepository<MonHoc, Integer> {
    public MonHoc findBytenMonHoc(String tenMonHoc);
    MonHoc findByMaMon(String maMon);
}
