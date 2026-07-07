package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Request.CheckLichAoRequest;
import PTPMUD.HoTroSinhVien.DTO.Respone.CheckLichAoResponse;
import PTPMUD.HoTroSinhVien.DTO.Respone.LichAoDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.Entity.LichAo;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Repository.LichAoRepository;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Util.LichHocValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LichAoService {

    LopHocPhanRepository lopHocPhanRepository;
    LopHocPhanMapper lopHocPhanMapper;
    SinhVienRepository sinhVienRepository;
    LichAoRepository lichAoRepository;

    public boolean check_lop_trung(LichAoDTO lichAoDTO)
    {
        List<LopHocPhanDTO> lopHocPhanDTOList=lichAoDTO.getLopHocPhanDTOList();
        if(lopHocPhanDTOList == null || lopHocPhanDTOList.size()<2)
            return false;
       for(int i=0;i<lopHocPhanDTOList.size()-1;i++)
           for(int j=i+1;j<lopHocPhanDTOList.size();j++)
           {
               LopHocPhan lopHocPhan1=lopHocPhanMapper.dtoToEntity(lopHocPhanDTOList.get(i));
               LopHocPhan lopHocPhan2=lopHocPhanMapper.dtoToEntity(lopHocPhanDTOList.get(j));
               if(LichHocValidator.trungLich(lopHocPhan1,lopHocPhan2)==true)
                   return true;

           }
       return false;
    }

    public String taoChuoiStringLopHP(LichAoDTO lichAoDTO){
        String chuoiStringLopHP="";

        for(LopHocPhanDTO lopHocPhanDTO: lichAoDTO.getLopHocPhanDTOList())
            chuoiStringLopHP=chuoiStringLopHP+lopHocPhanDTO.getMaLopHP()+",";
        return chuoiStringLopHP;
    }
//    public String taoChuoiStringLopHP(List<LopHocPhanDTO> lopHocPhanDTOList) {
//        return lopHocPhanDTOList.stream()
//                .map(LopHocPhanDTO::getMaLopHP)
//                .collect(Collectors.joining(","));
//    }


        public LichAoDTO convertStringToLichAoDTO(String s) {

            List<LopHocPhanDTO> lopHocPhanDTOList = new ArrayList<>();

            if (s == null || s.isBlank()) {
                return new LichAoDTO(lopHocPhanDTOList);
            }

            List<String> listMaLop = Arrays.asList(s.split(","));

            System.out.println(listMaLop);
            for (String maLop : listMaLop) {


                maLop = maLop.trim();
                if (maLop.isBlank()) {
                    continue;
                }

                LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLop);

                if (lopHocPhan != null) {
                    lopHocPhanDTOList.add(
                            lopHocPhanMapper.entityToDto(lopHocPhan)
                    );
                }
            }
            LichAoDTO lichAoDTO=new LichAoDTO();
            lichAoDTO.setLopHocPhanDTOList(lopHocPhanDTOList);

            return lichAoDTO;
        }

        public ByteArrayInputStream exportExcel(LichAoDTO lichAoDTO) {


            try {
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Sheet sheet = workbook.createSheet("Lịch học ảo");
                Row headerRow = sheet.createRow(0);

                String[] columns = {"Mã lớp HP", "Tên môn học", "Giảng viên", "Phòng học", "Thứ", "Giờ bắt đầu",
                        "Giờ kết thúc", "Ngày bắt đầu", "Ngày kết thúc", "Sĩ số tối đa"
                        , "Học kỳ", "Năm học", "Ngành", "Khóa"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }
                int j = 1;
                for (LopHocPhanDTO lopHocPhanDTO : lichAoDTO.getLopHocPhanDTOList()) {
                    Row row = sheet.createRow(j);
                    row.createCell(0).setCellValue(lopHocPhanDTO.getMaLopHP());
                    row.createCell(1).setCellValue(lopHocPhanDTO.getTenMonHoc());
                    row.createCell(2).setCellValue(lopHocPhanDTO.getGiangVien());
                    row.createCell(3).setCellValue(lopHocPhanDTO.getPhongHoc());
                    row.createCell(4).setCellValue(lopHocPhanDTO.getThu());
                    row.createCell(5).setCellValue(lopHocPhanDTO.getGioBatDau().toString());
                    row.createCell(6).setCellValue(lopHocPhanDTO.getGioKetThuc().toString());
                    row.createCell(7).setCellValue(lopHocPhanDTO.getNgayBatDau().toString());
                    row.createCell(8).setCellValue(lopHocPhanDTO.getNgayKetThuc().toString());
                    row.createCell(9).setCellValue(lopHocPhanDTO.getSiSoToiDa());
                    row.createCell(10).setCellValue(lopHocPhanDTO.getHocKy());
                    row.createCell(11).setCellValue(lopHocPhanDTO.getNamHoc());
                    row.createCell(12).setCellValue(lopHocPhanDTO.getNganh());
                    row.createCell(13).setCellValue(lopHocPhanDTO.getKhoa());
                    j++;
                }
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (Exception e) {
                throw new RuntimeException("Lỗi");
            }
        }


    public LichAoDTO importExcelLichAo(MultipartFile file) throws IOException {
        List<LopHocPhanDTO> lopHocPhanDTOList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell cell = row.getCell(0);
                if (cell == null) continue;
                String maLopHP = cell.getStringCellValue().trim();

                if (maLopHP.isBlank()) continue;

                LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLopHP);

                if (lopHocPhan == null) {
                    throw new RuntimeException("Không tìm thấy lớp học phần: " + maLopHP);
                }

                LopHocPhanDTO lopHocPhanDTO = lopHocPhanMapper.entityToDto(lopHocPhan);
                lopHocPhanDTOList.add(lopHocPhanDTO);
            }
        }

        LichAoDTO lichAoDTO = new LichAoDTO();
        lichAoDTO.setLopHocPhanDTOList(lopHocPhanDTOList);

        return lichAoDTO;
    }

    @Transactional
    public String delete(String maSv)
    {
        SinhVien sinhVien=sinhVienRepository.findByMaSv(maSv);
        if(sinhVien==null) return "Không có sinh viên này";
        LichAo lichAo= lichAoRepository.findBySinhVien_MaSv(maSv);
        if(lichAo==null) return "Sinh viên chưa đăng ký lịch";
        System.out.println(lichAo.getDsMaLopHP());
        lichAoRepository.delete(lichAo);
        return "Xóa thành công";
    }



}
