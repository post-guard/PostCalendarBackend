package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.SystemTimeDTO;
import top.rrricardo.postcalendarbackend.services.SystemClockService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/clock")
public class SystemClockController extends ControllerBase {
    private final SystemClockService systemClockService;

    public SystemClockController(SystemClockService systemClockService) {
        this.systemClockService = systemClockService;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<LocalDateTime>> getSystemTime() {
        return ok(systemClockService.getNow());
    }

    @PostMapping("/speedUp")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> setSpeedUp(@RequestBody SystemTimeDTO timeDTO) {
        if (timeDTO.getTime() < 1) {
            return badRequest("放大的倍数必须大于或者等于1");
        }

        systemClockService.speedUp(timeDTO.getTime());
        var result = new SystemTimeDTO();
        result.setTime(timeDTO.getTime());

        return ok(result);
    }

    @PostMapping("/speedDown")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> setSpeedDown(@RequestBody SystemTimeDTO timeDTO) {
        if (timeDTO.getTime() < 1) {
            return badRequest("放大的倍数必须大于或者等于1");
        }

        systemClockService.speedDown(timeDTO.getTime());
        var result = new SystemTimeDTO();
        result.setTime(timeDTO.getTime());

        return ok(result);
    }
}
