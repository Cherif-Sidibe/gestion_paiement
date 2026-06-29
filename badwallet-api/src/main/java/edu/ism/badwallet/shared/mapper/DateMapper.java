package edu.ism.badwallet.shared.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class DateMapper {
    public String formatLocalDate(LocalDate date, String pattern)
    {
        if(date == null)
        {
            return null;
        }
        return date.format((DateTimeFormatter.ofPattern(pattern)));
    }
    public String formatLocalTime(LocalTime time, String pattern)
    {
        if(time == null)
        {
            return null;
        }
        return time.format((DateTimeFormatter.ofPattern(pattern)));
    }
    public String formatLocalDateTime(LocalDateTime dateTime, String pattern)
    {
        if(dateTime == null)
        {
            return null;
        }
        return dateTime.format((DateTimeFormatter.ofPattern(pattern)));
    }
}
