package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.SystemTimeDTO;
import top.rrricardo.postcalendarbackend.services.SystemClockService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

@RestController
@RequestMapping("/clock")
public class SystemClockController extends ControllerBase {
    private final SystemClockService systemClockService;

    public SystemClockController(SystemClockService systemClockService) {
        this.systemClockService = systemClockService;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> getSystemTime() {
        var result = new SystemTimeDTO();
        result.setTime(systemClockService.getTime());
        result.setNow(systemClockService.getNow());

        return ok(result);
    }

    @PostMapping("/speedUp")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> setSpeedUp(@RequestBody SystemTimeDTO timeDTO) {
        if (timeDTO.getTime() < 1) {
            return badRequest("放大的倍数必须大于或者等于1");
        }

        systemClockService.speedUp(timeDTO.getTime());
        var result = new SystemTimeDTO();
        result.setTime(systemClockService.getTime());
        result.setNow(systemClockService.getNow());

        return ok(result);
    }

    @PostMapping("/speedDown")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> setSpeedDown(@RequestBody SystemTimeDTO timeDTO) {
        if (timeDTO.getTime() < 1) {
            return badRequest("放大的倍数必须大于或者等于1");
        }

        systemClockService.speedDown(timeDTO.getTime());
        var result = new SystemTimeDTO();
        result.setTime(systemClockService.getTime());
        result.setNow(systemClockService.getNow());

        return ok(result);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> setDateTime(@RequestBody SystemTimeDTO timeDTO) {
        systemClockService.setNow(timeDTO.getNow());

        var result = new SystemTimeDTO();
        result.setNow(systemClockService.getNow());
        result.setTime(systemClockService.getTime());

        return ok(result);
    }
}
