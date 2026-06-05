package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.ThoiKhoaBieuDTO;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.ThoiKhoaBieu;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ThoiKhoaBieuMapper {


    ThoiKhoaBieuDTO entityToDto(ThoiKhoaBieu entity);

    @Mapping(source = "monHoc.tenMonHoc", target = "tenMonHoc")
    LopHocPhanDTO lopHocPhanToDTO(LopHocPhan lopHocPhan);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idTKB", ignore = true)
    @Mapping(target = "sinhVien", ignore = true)

    void updateThoiKhoaBieu(@MappingTarget ThoiKhoaBieu oldEntity, ThoiKhoaBieu newEntity);
}
