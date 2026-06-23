package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Request.CheckLichAoRequest;
import PTPMUD.HoTroSinhVien.DTO.Respone.CheckLichAoResponse;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Util.LichHocValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LichAoService {

    LopHocPhanRepository lopHocPhanRepository;
    LopHocPhanMapper lopHocPhanMapper;

    public CheckLichAoResponse checkThemLop(CheckLichAoRequest request) {
        LopHocPhan lopMoi = findLopHocPhan(request.getNewMaLop());
        List<LopHocPhan> dsDaChon = findDanhSachLopDaChon(request.getSelectedMaLopHPs());

        LopHocPhan lopTrungMon = timLopTrungMon(dsDaChon, lopMoi);
        if (lopTrungMon != null) {
            return invalid("Bạn đã chọn môn này ở lớp " + lopTrungMon.getMaLopHP());
        }

        LopHocPhan lopTrungLich = LichHocValidator.timLopBiTrung(dsDaChon, lopMoi);
        if (lopTrungLich != null) {
            return invalid("Trùng lịch với lớp " + lopTrungLich.getMaLopHP());
        }

        return new CheckLichAoResponse(
                true,
                "Có thể thêm lớp này",
                lopHocPhanMapper.entityToDto(lopMoi)
        );
    }

    private LopHocPhan findLopHocPhan(String maLopHP) {
        if (maLopHP == null) {
            throw new IllegalArgumentException("Thiếu mã lớp học phần cần thêm");
        }
        LopHocPhan lopHocPhan=lopHocPhanRepository.findByMaLopHP(maLopHP);

        if(lopHocPhan==null)
            throw new RuntimeException("Không tìm thấy lớp học phần với mã lớp: " + maLopHP);
        return lopHocPhan;
    }

    private List<LopHocPhan> findDanhSachLopDaChon(List<String> selectedMaLopHPs) {
        if (selectedMaLopHPs == null || selectedMaLopHPs.isEmpty()) {
            return Collections.emptyList();
        }
        return lopHocPhanRepository.findByMaLopHPIn(selectedMaLopHPs);
    }

    private LopHocPhan timLopTrungMon(List<LopHocPhan> dsDaChon, LopHocPhan lopMoi) {
        return dsDaChon.stream()
                .filter(lop -> lop.getMaLopHP().equalsIgnoreCase( lopMoi.getMaLopHP())
                        || lop.getMonHoc().getMaMon().equalsIgnoreCase( lopMoi.getMonHoc().getMaMon()))
                .findFirst()
                .orElse(null);
    }

    private CheckLichAoResponse invalid(String message) {
        return new CheckLichAoResponse(false, message, null);
    }
}
