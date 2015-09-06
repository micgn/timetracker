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

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mg.holidays.model.Holiday;

public class HolidayAPITest {

    @BeforeClass
    public static void setUp() {
        ConfigResolver.setTestScope();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void test() {
        HolidayAPI testee = new HolidayAPI();
        List<Holiday> hs = testee.get(2015, 1);
        assertEquals(1, hs.size());
        assertEquals(new Date(115, 0, 1), hs.get(0).getDate(2015));

        hs = testee.get(2016, 12);
        assertEquals(1, hs.size());
        assertEquals(new Date(116, 11, 25), hs.get(0).getDate(2016));

        hs = testee.get(2015, 10);
        assertEquals(1, hs.size());
        assertEquals(new Date(115, 9, 1), hs.get(0).getDate(2015));

        hs = testee.get(2020, 10);
        assertEquals(1, hs.size());
        assertEquals(new Date(120, 9, 1), hs.get(0).getDate(2020));
    }

    @Test
    public void test2() {
        HolidayAPI testee = new HolidayAPI();
        assertEquals(21, testee.getAmountWorkingDays(2015, 1));
    }

    @Test
    public void test3() {
        HolidayAPI testee = new HolidayAPI();
        System.out.println(testee.getRemainingWorkingDays(2015, 4));
        System.out.println(testee.getRemainingWorkingDays(2015, 7));
        System.out.println(testee.getRemainingWorkingDays(2015, 8));
    }
}
