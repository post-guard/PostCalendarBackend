package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.dtos.CurriculumLoginDTO;
import top.rrricardo.postcalendarbackend.exceptions.CurriculumServiceException;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDate;

public interface CurriculumService {
    /**
     * 获得指定学期的开始
     * @param semesterString
     * @return
     */
    LocalDate getSemesterBeginTime(String semesterString);

    CustomList<TimeSpanEvent> getCurriculums(String semesterString, CurriculumLoginDTO loginDTO)
            throws CurriculumServiceException;
}
