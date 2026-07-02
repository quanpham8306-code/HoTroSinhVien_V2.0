package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.Entity.ChiTietThoiKhoaBieu;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.ThoiKhoaBieu;
import PTPMUD.HoTroSinhVien.Repository.ChiTietThoiKhoaBieuRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChiTietThoiKhoaBieuService {

    ChiTietThoiKhoaBieuRepository chiTietThoiKhoaBieuRepository;

    public void add (ThoiKhoaBieu thoiKhoaBieu, LopHocPhan lopHocPhan)
    {
        chiTietThoiKhoaBieuRepository.save(new ChiTietThoiKhoaBieu(thoiKhoaBieu,lopHocPhan));
    }

    public List<LopHocPhan> getLopHocPhans(ThoiKhoaBieu thoiKhoaBieu){
        return chiTietThoiKhoaBieuRepository.findByThoiKhoaBieu(thoiKhoaBieu)
                .stream()
                .map(ChiTietThoiKhoaBieu::getLopHocPhan)
                .toList();
    }

    public void deleteTKB(ThoiKhoaBieu thoiKhoaBieu){
        chiTietThoiKhoaBieuRepository.deleteByThoiKhoaBieu(thoiKhoaBieu);
    }
}
