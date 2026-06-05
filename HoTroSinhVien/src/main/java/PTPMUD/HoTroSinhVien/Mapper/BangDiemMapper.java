package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Respone.BangDiemDTO;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BangDiemMapper {
    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.monHoc.maMon", target = "maMon")
    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.monHoc.tenMonHoc", target = "tenMon")
    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.monHoc.soTinChi", target = "soTin")
    @Mapping(source = "diemHocPhan", target = "diemHocPhan")
    @Mapping(source = "diemChu", target = "diemChu")
    @Mapping(source = "trangThai", target = "trangThai")
    BangDiemDTO entityToDTO(Diem diem);

}
