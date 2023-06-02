package top.rrricardo.postcalendarbackend.exceptions;

/**
 * 教务系统获得课程服务异常
 */
public class CurriculumServiceException extends Exception {
    public CurriculumServiceException() {
        super();
    }

    public CurriculumServiceException(String message) {
        super(message);
    }
}
