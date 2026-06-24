package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.pickedClassDTO;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.MonHoc;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Mapper.MonHocMapper;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.MonHocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LopHocPhanService {
    LopHocPhanRepository lopHocPhanRepository;
    MonHocRepository monHocRepository;
    private final MonHocService monHocService;
    private final LopHocPhanMapper lopHocPhanMapper;

    public LopHocPhan createLPH(LopHocPhan lopHocPhan, int idMonHoc){
        MonHoc monHoc = monHocRepository.findById(idMonHoc)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));

        int soLopHienCo = lopHocPhanRepository.countByMonHoc_IdMon(idMonHoc);

        String maLopHP = vietTat(monHoc.getTenMonHoc()) + "-"+ lopHocPhan.getKhoa()+ "-L" + String.format("%02d", soLopHienCo + 1);

        lopHocPhan.setMaLopHP(maLopHP);
        lopHocPhan.setMonHoc(monHoc);

        return lopHocPhanRepository.save(lopHocPhan);
    }
    public String vietTat(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (String word : text.trim().split("\\s+")) {
            result.append(Character.toUpperCase(word.charAt(0)));
        }

        return result.toString();
    }
    public List<String> importExcel(MultipartFile file){
        List <String> errors=new ArrayList<>();
        try{
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet=workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for(int i=1;i<=sheet.getLastRowNum();i++)
            {
                Row row=sheet.getRow(i);
                if(row==null) continue;
                else
                {
                    try{

                        String tenMon = formatter.formatCellValue(row.getCell(0));

                        int soTinChi = Integer.parseInt(formatter.formatCellValue(row.getCell(1)).trim());
                        if(soTinChi<=0) throw new RuntimeException("Số tín chỉ phải lớn hơn 0");

                        String giangVien = formatter.formatCellValue(row.getCell(2)).trim();
                        if(giangVien.isBlank()) throw new RuntimeException("Giảng viên không được để trống");

                        String phongHoc = formatter.formatCellValue(row.getCell(3)).trim();

                        int thu = Integer.parseInt(formatter.formatCellValue(row.getCell(4)).trim());
                        if(thu<2 || thu>8) throw new RuntimeException("Thứ phải từ thứ 2 đến thứ 8");
                        LocalTime gioBatDau = LocalTime.parse(formatter.formatCellValue(row.getCell(5)).trim());
                        LocalTime gioKetThuc = LocalTime.parse(formatter.formatCellValue(row.getCell(6)).trim());
                        if(!gioBatDau.isBefore(gioKetThuc)) throw new RuntimeException("Giờ bắt đầu phải nhỏ hơn giờ kết thức");

                        LocalDate ngayBatDau = LocalDate.parse(formatter.formatCellValue(row.getCell(7)).trim());
                        LocalDate ngayKetThuc = LocalDate.parse(formatter.formatCellValue(row.getCell(8)).trim());
                        if(!ngayBatDau.isBefore(ngayKetThuc)) throw  new RuntimeException("Ngày bắt đầu phải nhỏ hơn nhày kết thúc");

                        int siSoToiDa = Integer.parseInt(formatter.formatCellValue(row.getCell(9)).trim());
                        if(siSoToiDa<=0) throw  new RuntimeException("Sĩ số tối đa phải lớn hơn 0");

                        int hocKy = Integer.parseInt(formatter.formatCellValue(row.getCell(10)).trim());
                        if(hocKy<1 || hocKy>2) throw new RuntimeException("Chỉ có học kỳ 1 hoặc học kỳ 2");

                        String namHoc=formatter.formatCellValue(row.getCell(11)).trim();
                        if (!namHoc.matches("\\d{4}-\\d{4}")) {
                            throw new RuntimeException("Năm học phải có dạng 20XX-20XX");
                        }

                        String khoa = formatter.formatCellValue(row.getCell(12)).trim();

                        String nganh=formatter.formatCellValue(row.getCell(13));
                        if(monHocRepository.findBytenMonHoc(tenMon) == null)
                        {
                            MonHoc mh = new MonHoc(soTinChi,tenMon);
                            monHocService.createMonHoc(mh);
                        }
                        LopHocPhan lopHocPhan= new LopHocPhan(giangVien,phongHoc,thu, gioBatDau,gioKetThuc,ngayBatDau
                                ,ngayKetThuc,siSoToiDa,hocKy,khoa,monHocRepository.findBytenMonHoc(tenMon),nganh,namHoc);
                        createLPH(lopHocPhan,monHocRepository.findBytenMonHoc(tenMon).getIdMon());

                    }catch(Exception o) {
                        errors.add("Dòng " + (i + 1) + " lỗi: " + o.getMessage());

                    }
                }
            }

        }catch(Exception e)
        {
           throw new RuntimeException("Không thể đọc file excel");
        }
        return errors;
    }


    public pickedClassDTO pickedClass(String maMon, String khoa, String nganh){
        List<LopHocPhan> lopHocPhanList = lopHocPhanRepository.findByNgayBatDauAfterAndKhoaAndNganhAndMonHoc_MaMon(
                LocalDate.now(),khoa,nganh,maMon);
        List<LopHocPhanDTO> lopHocPhanDTOList=new ArrayList<>();
        for(LopHocPhan lopHocPhan:lopHocPhanList)
        {
            LopHocPhanDTO lopHocPhanDTO=lopHocPhanMapper.entityToDto(lopHocPhan);
            lopHocPhanDTOList.add(lopHocPhanDTO);
        }
        return new pickedClassDTO(maMon,lopHocPhanDTOList);
    }
}
