package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Entity.TaiKhoan;
import PTPMUD.HoTroSinhVien.Mapper.TaiKhoanMapper;
import PTPMUD.HoTroSinhVien.Repository.TaiKhoanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.math3.analysis.function.Sinh;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/admin/account")
public class AdminTaiKhoanController {

    TaiKhoanRepository taiKhoanRepository;
    TaiKhoanMapper taiKhoanMapper;
    PasswordEncoder passwordEncoder;

    @GetMapping
    ResponseEntity<ResponseObject> getAllTaiKhoan() {
        List<?> result = taiKhoanRepository.findAll()
                .stream()
                .map(taiKhoanMapper::entityToDto)
                .toList();

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query accounts successfully", result)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getTaiKhoanById(@PathVariable int id) {
        return taiKhoanRepository.findById(id)
                .map(taiKhoan -> ResponseEntity.ok(
                        new ResponseObject("ok", "Query account successfully", taiKhoanMapper.entityToDto(taiKhoan))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Can not found account with id : " + id, "")
                ));
    }

    @GetMapping("/username/{username}")
    ResponseEntity<ResponseObject> getTaiKhoanByUsername(@PathVariable String username) {
        return taiKhoanRepository.findByUsername(username)
                .map(taiKhoan -> ResponseEntity.ok(
                        new ResponseObject("ok", "Query account successfully", taiKhoanMapper.entityToDto(taiKhoan))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Can not found account with username : " + username, "")
                ));
    }

    @PostMapping
    ResponseEntity<ResponseObject> insertTaiKhoan(@RequestBody TaiKhoan request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();

        if (username.isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject("failed", "Username is required", "")
            );
        }

        if (taiKhoanRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject("failed", "Account with username " + username + " already exists", "")
            );
        }

        request.setUsername(username);
        TaiKhoan savedTaiKhoan = taiKhoanRepository.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("ok", "Insert account successfully", taiKhoanMapper.entityToDto(savedTaiKhoan))
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateTaiKhoan(
            @PathVariable int id,
            @RequestBody TaiKhoan request
    ) {
        return taiKhoanRepository.findById(id)
                .map(oldTaiKhoan -> {
                    taiKhoanMapper.updateTaiKhoan(oldTaiKhoan, request);
                    TaiKhoan savedTaiKhoan = taiKhoanRepository.save(oldTaiKhoan);

                    return ResponseEntity.ok(
                            new ResponseObject("ok", "Update account successfully", taiKhoanMapper.entityToDto(savedTaiKhoan))
                    );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Can not found account with id : " + id, "")
                ));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteTaiKhoan(@PathVariable int id) {
        if (!taiKhoanRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found account with id : " + id, "")
            );
        }

        taiKhoanRepository.deleteById(id);
        return ResponseEntity.ok(
                new ResponseObject("ok", "Delete account successfully", "")
        );
    }

    @PutMapping("/reset-password/{username}")
    ResponseEntity<ResponseObject> resetPassword(@PathVariable String username){

       Optional<TaiKhoan> taiKhoan= taiKhoanRepository.findByUsername(username);
        if(taiKhoan.isEmpty())
            return ResponseEntity.badRequest().body(
                    new ResponseObject("false","Thất bại","")
            );
        else {
            TaiKhoan tk=taiKhoan.get();
            tk.setPassword(passwordEncoder.encode("1"));
            taiKhoanRepository.save(tk);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Reset thành công","")
            );
        }
    }

}
