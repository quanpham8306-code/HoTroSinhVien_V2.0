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
        LopHocPhan lopMoi = findLopHocPhan(request.getNewLopId());
        List<LopHocPhan> dsDaChon = findDanhSachLopDaChon(request.getSelectedLopIds());

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

    private LopHocPhan findLopHocPhan(Integer idLopHP) {
        if (idLopHP == null) {
            throw new IllegalArgumentException("Thiếu id lớp học phần cần thêm");
        }
        return lopHocPhanRepository.findById(idLopHP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần với id: " + idLopHP));
    }

    private List<LopHocPhan> findDanhSachLopDaChon(List<Integer> selectedLopIds) {
        if (selectedLopIds == null || selectedLopIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lopHocPhanRepository.findAllById(selectedLopIds);
    }

    private LopHocPhan timLopTrungMon(List<LopHocPhan> dsDaChon, LopHocPhan lopMoi) {
        return dsDaChon.stream()
                .filter(lop -> lop.getIdLopHP() == lopMoi.getIdLopHP()
                        || lop.getMonHoc().getIdMon() == lopMoi.getMonHoc().getIdMon())
                .findFirst()
                .orElse(null);
    }

    private CheckLichAoResponse invalid(String message) {
        return new CheckLichAoResponse(false, message, null);
    }
}
