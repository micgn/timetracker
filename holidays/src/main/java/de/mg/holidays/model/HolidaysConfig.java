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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.mg.holidays.model.Holiday.InvalidDate;

@XmlRootElement(name = "HolidaysConfig")
public class HolidaysConfig {

    @XmlElement(name = "holiday")
    private List<Holiday> holidays = new ArrayList<Holiday>();

    public List<Holiday> get(int year, int month) {
        if (year < 2000 || month < 1 || month > 12) {
            throw new IllegalArgumentException();
        }

        List<Holiday> result = new ArrayList<Holiday>();
        for (Holiday h : holidays) {
            Date d = h.getDate(year);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            if (cal.get(Calendar.YEAR) == year
                    && cal.get(Calendar.MONTH) + 1 == month) {
                result.add(h);
            }
        }
        return result;
    }

    void validate() throws InvalidDate {
        for (Holiday h : holidays) {
            h.validate();
        }
    }

}
