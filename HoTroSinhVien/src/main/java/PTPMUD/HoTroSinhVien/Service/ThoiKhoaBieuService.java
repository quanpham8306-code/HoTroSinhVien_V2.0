package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.ThoiKhoaBieuDTO;
import PTPMUD.HoTroSinhVien.Entity.*;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Repository.*;
import PTPMUD.HoTroSinhVien.Util.LichHocValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThoiKhoaBieuService {

    SinhVienRepository sinhVienRepository;
    DangKyLopHocPhanRepository dangKyRepository;
    LopHocPhanRepository lopHocPhanRepository;
    LopHocPhanMapper lopHocPhanMapper;



    @Transactional(readOnly = true)
    public List<ThoiKhoaBieuDTO> getScheduleByMaSv(String maSv) {
        List<ThoiKhoaBieuDTO> thoiKhoaBieuDTOS=new ArrayList<>();
        List<DangKyLopHocPhan> dangKyLopHocPhanList=dangKyRepository.findBySinhVien_MaSv(maSv);
        for(DangKyLopHocPhan dangKyLopHocPhan: dangKyLopHocPhanList)
        {
            LopHocPhan lopHocPhan=dangKyLopHocPhan.getLopHocPhan();
            int hocKy=lopHocPhan.getHocKy();
            String namHoc=lopHocPhan.getNamHoc();
            if(checkList(thoiKhoaBieuDTOS,hocKy,namHoc)==true)
            {
                ThoiKhoaBieuDTO thoiKhoaBieuDTO=findList(thoiKhoaBieuDTOS,hocKy,namHoc);
                thoiKhoaBieuDTO.getLopHocPhanDTOList().add(lopHocPhanMapper.entityToDto(lopHocPhan));
            }
            else
            {
                LopHocPhanDTO lopHocPhanDTO=lopHocPhanMapper.entityToDto(lopHocPhan);
                List<LopHocPhanDTO> lopHocPhanDTOList=new ArrayList<>();
                lopHocPhanDTOList.add(lopHocPhanDTO);
                ThoiKhoaBieuDTO thoiKhoaBieuDTO=new ThoiKhoaBieuDTO(hocKy,namHoc,lopHocPhanDTOList);
                thoiKhoaBieuDTOS.add(thoiKhoaBieuDTO);
            }

        }
        return thoiKhoaBieuDTOS;

    }

    public boolean checkList(List<ThoiKhoaBieuDTO> thoiKhoaBieuDTOS,int hocKy,String namHoc)
    {
      for(ThoiKhoaBieuDTO thoiKhoaBieuDTO: thoiKhoaBieuDTOS)
          if(thoiKhoaBieuDTO.getKy()==hocKy && thoiKhoaBieuDTO.getNamHoc().equalsIgnoreCase(namHoc))
              return true;
      return false;
    }

    public ThoiKhoaBieuDTO findList(List<ThoiKhoaBieuDTO> thoiKhoaBieuDTOS,int hocKy,String namHoc)
    {
        for(ThoiKhoaBieuDTO thoiKhoaBieuDTO: thoiKhoaBieuDTOS)
            if(thoiKhoaBieuDTO.getKy()==hocKy && thoiKhoaBieuDTO.getNamHoc().equalsIgnoreCase(namHoc))
                return thoiKhoaBieuDTO;
        return null;
    }

    public ThoiKhoaBieuDTO getScheduleByKyAndNamHoc(String maSv, int hocKy, String namHoc)
    {
        ThoiKhoaBieuDTO thoiKhoaBieuDTO=new ThoiKhoaBieuDTO();
        thoiKhoaBieuDTO.setNamHoc(namHoc);
        thoiKhoaBieuDTO.setKy(hocKy);

        List<DangKyLopHocPhan>  dangKyLopHocPhanList=dangKyRepository.findBySinhVien_MaSvAndLopHocPhan_HocKyAndLopHocPhan_NamHoc(maSv,hocKy,namHoc);
        for(DangKyLopHocPhan dangKyLopHocPhan: dangKyLopHocPhanList)
        {
            LopHocPhanDTO lopHocPhanDTO=lopHocPhanMapper.entityToDto(dangKyLopHocPhan.getLopHocPhan());
            thoiKhoaBieuDTO.getLopHocPhanDTOList().add(lopHocPhanDTO);
        }

        return thoiKhoaBieuDTO;
    }

    public ThoiKhoaBieuDTO getScheduleByDate(String maSv, LocalDate date) {
        LocalDate weekEnd = date.plusDays(6);

        List<DangKyLopHocPhan> dangKyList =
                dangKyRepository.findBySinhVien_MaSv(maSv);

        List<LopHocPhan> lopHocPhanList = dangKyList.stream()
                .map(DangKyLopHocPhan::getLopHocPhan)
                .filter(lhp -> isInWeek(lhp, date, weekEnd))
                .toList();

        ThoiKhoaBieuDTO dto = new ThoiKhoaBieuDTO();
        dto.setLopHocPhanDTOList(lopHocPhanMapper.entityToDTO(lopHocPhanList));

        if (!lopHocPhanList.isEmpty()) {
            LopHocPhan first = lopHocPhanList.get(0);
            dto.setKy(first.getHocKy());
            dto.setNamHoc(first.getNamHoc());
        }

        return dto;
    }

    private boolean isInWeek(LopHocPhan lhp, LocalDate weekStart, LocalDate weekEnd) {
        LocalDate ngayBatDau = lhp.getNgayBatDau();
        LocalDate ngayKetThuc = lhp.getNgayKetThuc();

        return !ngayBatDau.isAfter(weekEnd)
                && !ngayKetThuc.isBefore(weekStart);
    }

//    public ThoiKhoaBieuDTO getScheduleByDate(String maSv, LocalDate date) {
//        List<DangKyLopHocPhan> dangKyList =
//                dangKyRepository.findBySinhVien_MaSv(maSv);
//
//        List<LopHocPhan> lopHocPhanList = dangKyList.stream()
//                .map(DangKyLopHocPhan::getLopHocPhan)
//                .filter(lhp -> isInDate(lhp, date))
//                .toList();
//
//        ThoiKhoaBieuDTO dto = new ThoiKhoaBieuDTO();
//        dto.setLopHocPhanDTOList(lopHocPhanMapper.entityToDTO(lopHocPhanList));
//
//        if (!lopHocPhanList.isEmpty()) {
//            LopHocPhan first = lopHocPhanList.get(0);
//            dto.setKy(first.getHocKy());
//            dto.setNamHoc(first.getNamHoc());
//        }
//
//        return dto;
//    }

    private boolean isInDate(LopHocPhan lhp, LocalDate date) {
        boolean inRange = !date.isBefore(lhp.getNgayBatDau())
                && !date.isAfter(lhp.getNgayKetThuc());

        if (!inRange) {
            return false;
        }

        int javaDay = date.getDayOfWeek().getValue();
        // Java: Monday = 1, Tuesday = 2, ..., Sunday = 7

        int thu = javaDay + 1;
        // Hệ của bạn thường là: Chủ nhật = 1, Thứ 2 = 2, ..., Thứ 7 = 7

        if (thu == 8) {
            thu = 1;
        }

        return lhp.getThu() == thu;
    }

}
