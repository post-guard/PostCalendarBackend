package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.dtos.CurriculumLoginDTO;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.SemesterDTO;
import top.rrricardo.postcalendarbackend.exceptions.CurriculumServiceException;
import top.rrricardo.postcalendarbackend.services.CurriculumService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/curriculum")
public class CurriculumController extends ControllerBase {
    private final CurriculumService curriculumService;
    private final Logger logger;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;

        logger = LoggerFactory.getLogger(getClass());
    }

    @GetMapping("/semesters")
    public ResponseEntity<ResponseDTO<List<SemesterDTO>>> getSemesters() {
        return ok(curriculumService.getSemester());
    }

    @PostMapping("/{semester}")
    public ResponseEntity<ResponseDTO<SemesterDTO>> getCurriculum(
            @RequestBody CurriculumLoginDTO loginDTO,
            @PathVariable String semester) {
        logger.info("用户{}尝试获得课程表", loginDTO.getUserId());

        try {
            curriculumService.getCurriculums(semester, loginDTO);
            return ok();
        } catch (CurriculumServiceException exception) {
            return badRequest(exception.getMessage());
        }
    }
}
