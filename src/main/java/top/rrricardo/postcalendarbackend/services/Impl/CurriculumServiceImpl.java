package top.rrricardo.postcalendarbackend.services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.CurriculumLoginDTO;
import top.rrricardo.postcalendarbackend.dtos.SemesterDTO;
import top.rrricardo.postcalendarbackend.exceptions.CurriculumServiceException;
import top.rrricardo.postcalendarbackend.models.Course;
import top.rrricardo.postcalendarbackend.services.CurriculumService;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class CurriculumServiceImpl implements CurriculumService {
    private final static String baseUrl = "https://jwgl.bupt.edu.cn/jsxsd/";
    private final Logger logger;
    private final ObjectMapper objectMapper;
    private final TimeSpanEventService timeSpanEventService;
    private static final Map<String, LocalDate> semesterMap = Map.of(
            "2021-2022-1", LocalDate.of(2021, Month.APRIL, 30),
            "2021-2022-2", LocalDate.of(2022, Month.FEBRUARY, 28),
            "2022-2023-1", LocalDate.of(2022, Month.APRIL, 22),
            "2022-2023-2", LocalDate.of(2023, Month.FEBRUARY, 20)
    );
    private static final List<SemesterDTO> semesterList = new ArrayList<>();

    public CurriculumServiceImpl(TimeSpanEventService timeSpanEventService) {
        logger = LoggerFactory.getLogger(getClass());
        objectMapper = new ObjectMapper();
        this.timeSpanEventService = timeSpanEventService;

        for (var pair : semesterMap.entrySet()) {
            var semester = new SemesterDTO();
            semester.setSemester(pair.getKey());
            semester.setStartTime(LocalDateTime.of(pair.getValue(), LocalTime.MIN));
            semesterList.add(semester);
        }
    }

    @Override
    public LocalDate getSemesterBeginTime(String semesterString) {
        return semesterMap.get(semesterString);
    }

    @Override
    public void getCurriculums(String semesterString, CurriculumLoginDTO loginDTO)
            throws CurriculumServiceException {
        // 首先登录教务系统
        var client = new OkHttpClient.Builder()
                .followRedirects(false)
                .cookieJar(new CookieSave())
                .build();
        var loginBody = new FormBody.Builder()
                .add("userAccount", loginDTO.getUsername())
                .add("userPassword", "")
                .add("encoded",
                        getBase64String(loginDTO.getUsername()) + "%%%"
                                + getBase64String(loginDTO.getPassword()))
                .build();

        var loginRequest = new Request.Builder()
                .url(baseUrl + "xk/LoginToXk")
                .post(loginBody)
                .build();

        try (var response = client.newCall(loginRequest).execute()) {
            if (response.code() != 302) {
                logger.warn("登录教务系统失败，错误码{}", response.code());
                throw new CurriculumServiceException("账号或者密码错误");
            }
        } catch (IOException exception) {
            logger.warn("登录过程中发生网络错误", exception);
            throw new CurriculumServiceException();
        }

        // 登录成功之后获得对应学期的excel课表
        var downloadDto = new DownloadExcelDTO(semesterString);
        byte[] excelStream;
        try {
            var downloadRequest = new Request.Builder()
                    .url(baseUrl + downloadDto.getDownloadUrl())
                    .post(RequestBody.create(objectMapper.writeValueAsBytes(downloadDto)))
                    .build();

            try (var response = client.newCall(downloadRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    excelStream = response.body().bytes();
                } else {
                    logger.warn("下载课表失败");
                    throw new CurriculumServiceException();
                }

            } catch (IOException exception) {
                logger.warn("下载课表过程中发生网络问题", exception);
                throw new CurriculumServiceException();
            }
        } catch (JsonProcessingException exception) {
            logger.warn("格式化下载请求JSON异常", exception);
            throw new CurriculumServiceException();
        }

        try {
            var courses = analyseExcel(excelStream);

            if (courses != null) {
                for (var course : courses) {
                    for (var event : course.toTimeSpanEvent(getSemesterBeginTime(semesterString), loginDTO.getUserId())) {
                        timeSpanEventService.addUserEvent(event);
                    }
                }
            }
        } catch (Exception exception) {
            logger.error("解析并写入输入库出错", exception);
            throw new CurriculumServiceException();
        }
    }

    @Override
    public List<SemesterDTO> getSemester() {
        return semesterList;
    }

    /**
     * 获得base64格式化的字符串
     *
     * @param input 输入字符串
     * @return base64编码之后的字符串
     */
    private static String getBase64String(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    private static List<Course> parseSingleCell(String cellString, int dayOfWeek) {
        // 这里注意
        // lines的第一行是一个空字符串
        // 5行文字有6行
        var lines = cellString.split("\n");
        var courses = new ArrayList<Course>();

        int[] weeks, classes;
        Course course;

        switch (lines.length) {
            case 6 -> {
                // 只有一门课程的单元格
                // 没有分组
                weeks = Course.parseWeeksString(lines[3]);
                classes = Course.parseTimeString(lines[5]);
                course = new Course(
                        lines[1],
                        lines[2],
                        lines[4],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
            }
            case 7 -> {
                // 只有一门课程的单元格
                // 含有分组
                weeks = Course.parseWeeksString(lines[4]);
                classes = Course.parseTimeString(lines[6]);
                course = new Course(
                        lines[1],
                        lines[3] + lines[2],// 老师和分组合并显示
                        lines[5],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
            }
            case 11 -> {
                // 含有两门课程的单元格
                // 均没有分组
                weeks = Course.parseWeeksString(lines[3]);
                classes = Course.parseTimeString(lines[5]);
                course = new Course(
                        lines[1],
                        lines[2],
                        lines[4],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
                weeks = Course.parseWeeksString(lines[8]);
                classes = Course.parseTimeString(lines[10]);
                course = new Course(
                        lines[6],
                        lines[7],
                        lines[9],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
            }
            case 12 -> {
                // 含有两门课程的单元格
                // 有一门课程有分组
                // 通过第五行是否存在”节“来判断
                if (lines[4].contains("节")) {
                    // 第一门没有分组
                    weeks = Course.parseWeeksString(lines[3]);
                    classes = Course.parseTimeString(lines[5]);
                    course = new Course(
                            lines[1],
                            lines[2],
                            lines[4],
                            weeks,
                            classes[0],
                            classes[1],
                            dayOfWeek
                    );
                    courses.add(course);

                    // 第二门有分组
                    weeks = Course.parseWeeksString(lines[8]);
                    classes = Course.parseTimeString(lines[10]);
                    course = new Course(
                            lines[5],
                            lines[7] + lines[6],// 老师和分组合并显示
                            lines[9],
                            weeks,
                            classes[0],
                            classes[1],
                            dayOfWeek
                    );
                    courses.add(course);

                } else {
                    // 第一门有分组
                    weeks = Course.parseWeeksString(lines[4]);
                    classes = Course.parseTimeString(lines[6]);
                    course = new Course(
                            lines[1],
                            lines[3] + lines[2],// 老师和分组合并显示
                            lines[5],
                            weeks,
                            classes[0],
                            classes[1],
                            dayOfWeek
                    );
                    ;
                    courses.add(course);

                    // 第二门没有分组
                    weeks = Course.parseWeeksString(lines[9]);
                    classes = Course.parseTimeString(lines[11]);
                    course = new Course(
                            lines[7],
                            lines[8],
                            lines[10],
                            weeks,
                            classes[0],
                            classes[1],
                            dayOfWeek
                    );
                    courses.add(course);

                }
            }
            case 13 -> {
                // 含有两门课程的单元格
                // 均有分组
                weeks = Course.parseWeeksString(lines[4]);
                classes = Course.parseTimeString(lines[6]);
                course = new Course(
                        lines[1],
                        lines[3] + lines[2],// 老师和分组合并显示
                        lines[5],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
                weeks = Course.parseWeeksString(lines[10]);
                classes = Course.parseTimeString(lines[12]);
                course = new Course(
                        lines[7],
                        lines[9] + lines[8],// 老师和分组合并显示
                        lines[11],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
            }
            case 16 -> {
                // 含有三类型的的课程
                // 没有分组
                weeks = Course.parseWeeksString(lines[3]);
                classes = Course.parseTimeString(lines[5]);
                course = new Course(
                        lines[1],
                        lines[2],
                        lines[4],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
                weeks = Course.parseWeeksString(lines[8]);
                classes = Course.parseTimeString(lines[10]);
                course = new Course(
                        lines[6],
                        lines[7],
                        lines[9],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
                weeks = Course.parseWeeksString(lines[13]);
                classes = Course.parseTimeString(lines[15]);
                course = new Course(
                        lines[11],
                        lines[12],
                        lines[14],
                        weeks,
                        classes[0],
                        classes[1],
                        dayOfWeek
                );
                courses.add(course);
            }
            default -> throw new IllegalArgumentException("解析单元格失败");
        }

        return courses;
    }

    private static List<Course> analyseExcel(byte[] excelStream) {
        var courses = new ArrayList<Course>();
        var inputStream = new ByteArrayInputStream(excelStream);

        try (var workbook = WorkbookFactory.create(inputStream)) {
            var sheet = workbook.getSheetAt(0);

            var rows = sheet.getLastRowNum();

            for (var column = 1; column <= 7; column++) {
                for (var row = 4; row <= rows - 1; row++) {
                    var cell = sheet.getRow(row).getCell(column);

                    if (cell != null) {
                        var content = cell.getStringCellValue();

                        if (content.length() > 1) {
                            var result = parseSingleCell(content, column);
                            courses.addAll(result);

                            row = row + result.get(0).getLength();
                        }
                    }
                }
            }
        } catch (IOException exception) {
            return null;
        }

        return courses;
    }

    /**
     * 在内存中保存cookie
     */
    private static class CookieSave implements CookieJar {
        private static final List<Cookie> cookies = new ArrayList<>();

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
            if (httpUrl.host().equals("jwgl.bupt.edu.cn")) {
                return cookies;
            } else {
                return new ArrayList<>();
            }
        }

        @Override
        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            if (httpUrl.host().equals("jwgl.bupt.edu.cn")) {
                cookies.clear();
                cookies.addAll(list);
            }
        }
    }

    private static class DownloadExcelDTO {
        private final String xnxq01id;
        private final String zc;
        private final String kbjcmsid;

        DownloadExcelDTO(String semester) {
            xnxq01id = semester;
            zc = "";
            kbjcmsid = "9475847A3F3033D1E05377B5030AA94D";
        }

        public String getDownloadUrl() {
            return "xskb/xskb_print.do?xnxq01id=" + xnxq01id + "&zc=" + zc + "&kbjcmsid" + kbjcmsid;
        }
    }
}
