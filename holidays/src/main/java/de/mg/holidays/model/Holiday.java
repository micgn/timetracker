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
package de.mg.holidays.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class Holiday {

    @XmlElement(name = "date")
    private String dateStr;

    @XmlElement(name = "desc")
    public String description;

    void validate() throws InvalidDate {
        if (getDate(2015) == null) {
            throw new InvalidDate(dateStr);
        }
    }

    public Date getDate(int year) {
        if (year < 2000) {
            throw new IllegalArgumentException("year must be >= 2000");
        }

        String str = dateStr;
        if (dateStr.endsWith("*")) {
            str = dateStr.substring(0, dateStr.length() - 1) + year;
        }

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return df.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    @SuppressWarnings("serial")
    public class InvalidDate extends Exception {

        public final String invalid;

        InvalidDate(String invalid) {
            this.invalid = invalid;
        }
    }

    public boolean equalsDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(getDate(y));
        int y2 = cal.get(Calendar.YEAR);
        int m2 = cal.get(Calendar.MONTH);
        int d2 = cal.get(Calendar.DAY_OF_MONTH);

        return y == y2 && m == m2 && d == d2;
    }

}
