package PTPMUD.HoTroSinhVien.DTO.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateDiemDTO {
    private double diemQuaTrinh;
    private double diemCuoiKy;
    private double diemHocPhan;
    private String trangThai;

    public CreateDiemDTO(double diemQuaTrinh, double diemCuoiKy) {
        this.diemQuaTrinh = diemQuaTrinh;
        this.diemCuoiKy = diemCuoiKy;
        this.diemHocPhan = diemCuoiKy*0.7+diemQuaTrinh*0.3;
        this.setTrangThai();
    }
    private void setTrangThai(){
        if(diemHocPhan < 4)
            this.trangThai = "Trượt";
        else
            this.trangThai = "Qua môn";
    }
}
