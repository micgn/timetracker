/* 
 * Copyright 2015 Michael Gnatz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mg.holidays;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.mg.holidays.model.Holiday;
import de.mg.holidays.model.HolidaysConfig;

public class HolidayAPI {

    private static final String CONFIG_FILE = "holidays.xml";
    private HolidaysConfig config = null;

    public HolidayAPI() {
        File f = ConfigResolver.getFile(CONFIG_FILE);
        if (f == null) {
            config = new HolidaysConfig();
            return;
        }
        try {
            JAXBContext jaxbContext = JAXBContext
                    .newInstance(HolidaysConfig.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            config = (HolidaysConfig) jaxbUnmarshaller.unmarshal(f);

        } catch (JAXBException e) {
            String fname = (f != null) ? f.getAbsolutePath() : "null";
            throw new RuntimeException(
                    "configuration problem of holiday calendar for file: "
                    + fname, e);
        }
    }

    /**
     * @param year >2000
     * @param month 1-12
     */
    public List<Holiday> get(int year, int month) {
        validate(year, month);
        return config.get(year, month);
    }

    /**
     * @param year >2000
     * @param month 1-12
     */
    public int getAmountWorkingDays(int year, int month) {
        validate(year, month);
        final List<Holiday> holidays = config.get(year, month);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int days = 0;
        while (cal.get(Calendar.MONTH) == month - 1) {
            int weekDay = cal.get(Calendar.DAY_OF_WEEK);
            if (weekDay != Calendar.SATURDAY && weekDay != Calendar.SUNDAY
                    && !isHoliday(cal.getTime(), holidays)) {
                days++;
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    /**
     * @param year >2000
     * @param month 1-12
     */
    public int getRemainingWorkingDays(int year, int month) {
        validate(year, month);
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        int currentMonth = today.get(Calendar.MONTH) + 1;
        if (month < currentMonth) {
            return 0;
        } else if (month > currentMonth) {
            return getAmountWorkingDays(year, month);
        } else {
            // month == currentMonth

            final List<Holiday> holidays = config.get(year, month);
            int days = 0;
            Calendar cal = (Calendar) today.clone();
            while (cal.get(Calendar.MONTH) == month - 1) {
                int weekDay = cal.get(Calendar.DAY_OF_WEEK);
                if (weekDay != Calendar.SATURDAY && weekDay != Calendar.SUNDAY
                        && !isHoliday(cal.getTime(), holidays)) {
                    days++;
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            return days;
        }
    }

    /**
     * @param year >2000
     * @param month 1-12
     */
    public int getPassedWorkingDay(int year, int month) {
        validate(year, month);
        return getAmountWorkingDays(year, month) - getRemainingWorkingDays(year, month);
    }

    private boolean isHoliday(Date date, List<Holiday> holidays) {
        for (Holiday h : holidays) {
            if (h.equalsDate(date)) {
                return true;
            }
        }
        return false;
    }

    static void validate(int year, int month) {
        if (year < 2000 || month < 1 || month > 12) {
            throw new IllegalArgumentException("invalid year and/or month: year=" + year + " , month=" + month);
        }
    }
}
