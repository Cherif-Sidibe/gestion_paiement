package edu.ism.payment.shared.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class DateMapper {
    public String formatLocalDate(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        return date.format((DateTimeFormatter.ofPattern(pattern)));
    }
}
