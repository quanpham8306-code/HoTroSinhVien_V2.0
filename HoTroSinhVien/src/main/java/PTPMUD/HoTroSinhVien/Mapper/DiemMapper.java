package PTPMUD.HoTroSinhVien.Mapper;


import PTPMUD.HoTroSinhVien.DTO.Request.CreateDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.BangDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.DiemAdminDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.DiemDTO;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiemMapper {
    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.monHoc.tenMonHoc", target = "mon")
    @Mapping(source = "dangKyLopHocPhan.sinhVien.maSv", target="maSv")
    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.maLopHP", target = "maLopHP")
    DiemAdminDTO entityDiemAdminDto(Diem diem);
    DiemDTO entityDiemDto(Diem diem);

    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.monHoc.tenMonHoc", target = "tenMon")
    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.monHoc.maMon", target = "maMon")
    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.monHoc.soTinChi", target = "soTin")
    BangDiemDTO entityDiemToBangDiemDTO(Diem diem);
    List<BangDiemDTO> entityDiemToBangDiemDTO(List<Diem> diems);
    void updateDiem(@MappingTarget Diem oldDiem, CreateDiemDTO newDiem);
}
