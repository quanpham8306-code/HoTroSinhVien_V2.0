package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.MonHoc;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LopHocPhanService {
    LopHocPhanRepository lopHocPhanRepository;
    MonHocRepository monHocRepository;
    private final MonHocService monHocService;

    public LopHocPhan createLPH(LopHocPhan lopHocPhan, int idMonHoc){
        MonHoc monHoc = monHocRepository.findById(idMonHoc)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));

        int soLopHienCo = lopHocPhanRepository.countByMonHoc_IdMon(idMonHoc);

        String maLopHP = monHoc.getTenMonHoc() + "-N" + String.format("%02d", soLopHienCo + 1);

        lopHocPhan.setMaLopHP(maLopHP);
        lopHocPhan.setMonHoc(monHoc);

        return lopHocPhanRepository.save(lopHocPhan);
    }

        public void importExcel(MultipartFile file){
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

                          String tenMon=formatter.formatCellValue(row.getCell(0));
                          int soTinChi=Integer.parseInt(formatter.formatCellValue(row.getCell(1)));
                          String giangVien=formatter.formatCellValue(row.getCell(2));
                          String phongHoc=formatter.formatCellValue(row.getCell(3));
                          String thu=formatter.formatCellValue(row.getCell(4));
                          LocalTime gioBatDau=LocalTime.parse(formatter.formatCellValue(row.getCell(5)));
                          LocalTime gioKetThuc=LocalTime.parse(formatter.formatCellValue(row.getCell(6)));
                          LocalDate ngayBatDau=LocalDate.parse(formatter.formatCellValue(row.getCell(7)));
                          LocalDate ngayKetThuc=LocalDate.parse(formatter.formatCellValue(row.getCell(8)));
                          int siSoToiDa =Integer.parseInt(formatter.formatCellValue(row.getCell(9)));
                          String hocKy=formatter.formatCellValue(row.getCell(10));
                          String namHoc=formatter.formatCellValue(row.getCell(11));
                          if(monHocRepository.findBytenMonHoc(tenMon)==null)
                          {
                              MonHoc mh=new MonHoc(soTinChi,tenMon);
                              monHocService.createMonHoc(mh);
                          }
                          LopHocPhan lopHocPhan= new LopHocPhan(giangVien,phongHoc,thu, gioBatDau,gioKetThuc,ngayBatDau
                                  ,ngayKetThuc,siSoToiDa,hocKy,namHoc,monHocRepository.findBytenMonHoc(tenMon));
                          createLPH(lopHocPhan,monHocRepository.findBytenMonHoc(tenMon).getIdMon());

                      }catch(Exception o) {
                          System.out.println("Lỗi dòng " + (i + 1));
                          o.printStackTrace();

                      }
                   }
                }
                workbook.close();
            }catch(Exception e)
            {

                e.printStackTrace();
            }

        }


}
