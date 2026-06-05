package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Respone.MonHocDTO;
import PTPMUD.HoTroSinhVien.Entity.MonHoc;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MonHocMapper {
    MonHoc dtoToEntity(MonHocDTO monHocDTO);
    MonHocDTO entityToDto(MonHoc monHoc);
    void updateMonHoc(@MappingTarget MonHoc old, MonHocDTO monHocDTO);
}
