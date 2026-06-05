package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.ThoiKhoaBieuDTO;
import PTPMUD.HoTroSinhVien.Entity.ChiTietThoiKhoaBieu;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.ThoiKhoaBieu;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ThoiKhoaBieuMapper {

    @Mapping(source = "chiTietThoiKhoaBieus", target = "lopHocPhanDTOList")
    ThoiKhoaBieuDTO entityToDto(ThoiKhoaBieu entity);

    default LopHocPhanDTO chiTietToLopHocPhanDTO(ChiTietThoiKhoaBieu chiTiet) {
        return chiTiet == null ? null : lopHocPhanToDTO(chiTiet.getLopHocPhan());
    }

    @Mapping(source = "monHoc.tenMonHoc", target = "tenMonHoc")
    LopHocPhanDTO lopHocPhanToDTO(LopHocPhan lopHocPhan);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idTKB", ignore = true)
    @Mapping(target = "sinhVien", ignore = true)
    @Mapping(target = "chiTietThoiKhoaBieus", ignore = true)
    void updateThoiKhoaBieu(@MappingTarget ThoiKhoaBieu oldEntity, ThoiKhoaBieu newEntity);
}
