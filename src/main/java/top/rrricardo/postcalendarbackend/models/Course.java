package top.rrricardo.postcalendarbackend.models;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Course {
    private final String name;
    private final String teacher;
    private final String place;
    private final int[] weeks;
    private final LocalTime beginTime;
    private final LocalTime endTime;
    private final int dayOfWeek;
    private final int length;

    private static final LocalTime[] beginTimeList = new LocalTime[] {
            LocalTime.of(8, 0),
            LocalTime.of(8, 50),
            LocalTime.of(9, 50),
            LocalTime.of(10, 40),
            LocalTime.of(11, 30),
            LocalTime.of(13, 0),
            LocalTime.of(13, 50),
            LocalTime.of(14, 45),
            LocalTime.of(15, 40),
            LocalTime.of(16, 35),
            LocalTime.of(17, 25),
            LocalTime.of(18, 35),
            LocalTime.of(19, 20),
            LocalTime.of(20, 10),
    };

    public Course(String name,
                  String teacher,
                  String place,
                  int[] weeks,
                  int beginClass,
                  int endClass,
                  int dayOfWeek) {
        this.name = name;
        this.teacher = teacher;
        this.place = place;
        this.weeks = weeks;
        this.dayOfWeek = dayOfWeek;

        beginTime = beginTimeList[beginClass - 1];
        endTime = beginTimeList[endClass - 1].plusMinutes(45);
        length = endClass - beginClass;
    }

    public List<TimeSpanEvent> toTimeSpanEvent(LocalDate semesterBeginDay, int userId) {
        var result = new ArrayList<TimeSpanEvent>();

        for (var week : weeks) {
            var event = new TimeSpanEvent();
            event.setName(name);
            event.setDetails(teacher + "\n" + place);
            event.setPlaceId(0);
            event.setGroupId(0);
            event.setUserId(userId);

            var date = semesterBeginDay.plusDays((week - 1) * 7L + dayOfWeek - 1);
            event.setBeginDateTime(LocalDateTime.of(date, beginTime));
            event.setEndDateTime(LocalDateTime.of(date, endTime));

            result.add(event);
        }

        return result;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append(name).append('-').append(teacher).append('\n');
        builder.append(place).append('-').append(beginTime).append(endTime).append('\n');

        for (var week : weeks) {
            builder.append(week).append('-');
        }

        return builder.toString();
    }

    @NotNull
    public static int[] parseWeeksString(String input) {
        var list = new ArrayList<Integer>();
        var pattern = Pattern.compile("^(\\d+)-(\\d+).*");

        input = input.split("\\[")[0];

        var numbers = input.split(",");

        for (var number : numbers) {
            try {
                var result = Integer.parseInt(number);
                list.add(result);
            } catch (NumberFormatException exception) {
                var matcher = pattern.matcher(number);

                if (matcher.find()) {
                    var begin = Integer.parseInt(matcher.group(1));
                    var end = Integer.parseInt(matcher.group(2));

                    for (var i = begin; i <= end; i++) {
                        list.add(i);
                    }
                }
            }
        }

        var result = new int[list.size()];

        for (var i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }

        return result;
    }

    @NotNull
    public static int[] parseTimeString(String input) {
        var pattern = Pattern.compile("\\[(.*)]节");
        var matcher = pattern.matcher(input);

        if (matcher.find()) {
            var numbers = matcher.group(1).split("-");

            return new int[] {
                    Integer.parseInt(numbers[0]),
                    Integer.parseInt(numbers[numbers.length - 1])
            };
        }

        throw new IllegalArgumentException("无法解析事件字符串：" + input);
    }

    public int getLength() {
        return length;
    }
}
