package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger;
    public SystemClockController(SystemClockService systemClockService) {
        this.systemClockService = systemClockService;
        this.logger = LoggerFactory.getLogger(SystemClockController.class);
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
            logger.info("时间加速失败，放大的倍数必须大于等于1");
            return badRequest("放大的倍数必须大于或者等于1");
        }

        systemClockService.speedUp(timeDTO.getTime());
        var result = new SystemTimeDTO();
        result.setTime(systemClockService.getTime());
        result.setNow(systemClockService.getNow());

        logger.info("时间加速成功");
        return ok(result);
    }

    @PostMapping("/speedDown")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> setSpeedDown(@RequestBody SystemTimeDTO timeDTO) {
        if (timeDTO.getTime() < 1) {
            logger.info("时间减速失败，缩小的倍数必须大于等于1");
            return badRequest("缩小的倍数必须大于或者等于1");
        }

        systemClockService.speedDown(timeDTO.getTime());
        var result = new SystemTimeDTO();
        result.setTime(systemClockService.getTime());
        result.setNow(systemClockService.getNow());

        logger.info("时间减速成功");
        return ok(result);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> setDateTime(@RequestBody SystemTimeDTO timeDTO) {
        systemClockService.setNow(timeDTO.getNow());

        var result = new SystemTimeDTO();
        result.setNow(systemClockService.getNow());
        result.setTime(systemClockService.getTime());

        logger.info("设置时间成功");
        return ok(result);
    }

    @PostMapping("/reset")
    public ResponseEntity<ResponseDTO<SystemTimeDTO>> resetTime() {
        systemClockService.reset();

        var result = new SystemTimeDTO();
        result.setNow(systemClockService.getNow());
        result.setTime(systemClockService.getTime());

        logger.info("重置时间成功");
        return ok(result);
    }
}
