package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Respone.MonHocDTO;
import PTPMUD.HoTroSinhVien.Entity.MonHoc;
import PTPMUD.HoTroSinhVien.Mapper.MonHocMapper;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.MonHocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MonHocService {
    MonHocRepository monHocRepository;
    MonHocMapper monHocMapper;
    LopHocPhanRepository lopHocPhanRepository;
    private String generateMaMon(String tenMonHoc, int idMon) {
        String prefix = Arrays.stream(tenMonHoc.trim().split("\\s+"))
                .filter(word -> !word.isBlank())
                .map(word -> word.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());

        return prefix;
    }
    public MonHoc createMonHoc(MonHoc monHoc) {
        MonHoc savedMonHoc = monHocRepository.save(monHoc);

        String maMon = generateMaMon(
                savedMonHoc.getTenMonHoc(),
                savedMonHoc.getIdMon()
        );

        savedMonHoc.setMaMon(maMon);

        return monHocRepository.save(savedMonHoc);
    }

    public List<MonHocDTO> getMonByKhoa(String nganh, String khoa)
    {
        List<MonHoc> monHocList=monHocRepository.findByLopHocPhans_NganhAndLopHocPhans_KhoaAndLopHocPhans_NgayBatDauAfter(nganh,khoa,LocalDate.now());
        List<MonHocDTO> monHocDTOS=new ArrayList<>();
        for(MonHoc monHoc: monHocList)
        {

                MonHocDTO monHocDTO = monHocMapper.entityToDto(monHoc);
                monHocDTOS.add(monHocDTO);

        }
        return monHocDTOS;
    }
}
