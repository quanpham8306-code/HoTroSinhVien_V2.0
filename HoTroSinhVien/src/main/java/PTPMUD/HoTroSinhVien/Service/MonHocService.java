package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.Entity.MonHoc;
import PTPMUD.HoTroSinhVien.Repository.MonHocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MonHocService {
    MonHocRepository monHocRepository;
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
}
