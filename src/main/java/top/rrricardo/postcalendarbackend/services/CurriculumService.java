package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.dtos.CurriculumLoginDTO;
import top.rrricardo.postcalendarbackend.dtos.SemesterDTO;
import top.rrricardo.postcalendarbackend.exceptions.CurriculumServiceException;

import java.time.LocalDate;
import java.util.List;

public interface CurriculumService {
    /**
     * 获得指定学期的开始
     * @param semesterString
     * @return
     */
    LocalDate getSemesterBeginTime(String semesterString);

    void getCurriculums(String semesterString, CurriculumLoginDTO loginDTO)
            throws CurriculumServiceException;

    List<SemesterDTO> getSemester();
}
