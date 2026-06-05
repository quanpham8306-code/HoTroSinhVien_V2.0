package PTPMUD.HoTroSinhVien.Mapper;


import PTPMUD.HoTroSinhVien.DTO.Request.CreateDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.DiemDTO;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DiemMapper {
    @Mapping(source = "dangKyLopHocPhan.lopHocPhan.monHoc.tenMonHoc", target = "mon")
    DiemDTO entityToDto(Diem diem);
    void updateDiem(@MappingTarget Diem oldDiem, CreateDiemDTO newDiem);
}
