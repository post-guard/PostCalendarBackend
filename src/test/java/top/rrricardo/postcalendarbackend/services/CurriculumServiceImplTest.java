package top.rrricardo.postcalendarbackend.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.rrricardo.postcalendarbackend.dtos.CurriculumLoginDTO;
import top.rrricardo.postcalendarbackend.models.Course;
import top.rrricardo.postcalendarbackend.services.Impl.CurriculumServiceImpl;

public class CurriculumServiceImplTest {
    @Test
    void testParseWeeksString1() {
        var input = "1-16[周]";

        var result = Course.parseWeeksString(input);
        for (var i = 1; i <= 16; i++) {
            Assertions.assertEquals(i, result[i - 1]);
        }
    }

    @Test
    void testParseWeekString2() {
        var input = "1,4,8,15-16[周]";

        var result = Course.parseWeeksString(input);
        Assertions.assertEquals(1, result[0]);
        Assertions.assertEquals(4, result[1]);
        Assertions.assertEquals(8, result[2]);
        Assertions.assertEquals(15, result[3]);
        Assertions.assertEquals(16, result[4]);
    }

    @Test
    void testParseTimeString1() {
        var input = "[01-02]节";

        var result = Course.parseTimeString(input);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result[0]);
        Assertions.assertEquals(2, result[1]);
    }

    @Test
    void testParseTimeString2() {
        var input = "[09-10-11]节";

        var result = Course.parseTimeString(input);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(9, result[0]);
        Assertions.assertEquals(11, result[1]);
    }
}
