package ru.caramel.juniperbot.utils;

import lombok.Getter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class TimeSequenceParser {

    public enum FieldType {
        MONTH       (Calendar.MONTH,        11,     compile("^месяц(а|ев)?$"),      compile("^months?$")),
        WEEK        (Calendar.WEEK_OF_YEAR, 31,     compile("^недел[юиь]$"),        compile("^weeks?$")),
        DAY         (Calendar.DAY_OF_YEAR,  6,      compile("^день|дн(я|ей)$"),     compile("^days?$")),
        HOUR        (Calendar.HOUR_OF_DAY,  23,     compile("^час(а|ов)?$"),        compile("^hours?$")),
        MINUTE      (Calendar.MINUTE,       59,     compile("^минут[уы]?$"),        compile("^minutes?$")),
        SECOND      (Calendar.SECOND,       59,     compile("^секунд[уы]?$"),       compile("^seconds?$")),
        MILLISECOND (Calendar.MILLISECOND,  999,    compile("^миллисекунд[уы]?$"),  compile("^milliseconds?$"));

        @Getter
        private final int type;

        @Getter
        private final int maxUnits;

        @Getter
        private final Pattern[] patterns;

        FieldType(int type, int maxUnits, Pattern... patterns) {
            this.type = type;
            this.maxUnits = maxUnits;
            this.patterns = patterns;
        }

        public static FieldType find(String value) {
            for (FieldType type : values()) {
                for (Pattern pattern : type.patterns) {
                    if (pattern.matcher(value).find()) {
                        return type;
                    }
                }
            }
            return null;
        }
    }
    private final static Pattern PART_PATTERN = Pattern.compile("(\\d+)\\s+([a-zA-Zа-яА-Я]+)");

    public Long parse(String string) {
        Matcher m = PART_PATTERN.matcher(string);

        Map<FieldType, Integer> values = new HashMap<>();
        while(m.find()) {
            int units = Integer.parseInt(m.group(1));
            FieldType type = FieldType.find(m.group(2));
            if (units == 0 || type == null) {
                return null; // unknown type
            }
            if (values.containsKey(type)) {
                return null; // double declaration? invalid
            }
            if (values.keySet().stream().anyMatch(e -> e.ordinal() >= type.ordinal())) {
                return null; // invalid sequence
            }
            values.put(type, units);
        }
        if (values.size() > 1 && values.entrySet().stream().anyMatch(e -> e.getValue() > e.getKey().maxUnits)) {
            return null; // strict sequence
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        values.forEach((type, units) -> calendar.add(type.type, units));
        return values.isEmpty() ? null : calendar.getTimeInMillis() - currentDate.getTime();
    }
}
