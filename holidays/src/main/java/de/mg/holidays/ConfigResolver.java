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

public class ConfigResolver {

    private static final String[] locations = new String[]{"/home/gnatz",
        "/home/gnatz/config", "/tomcatdata",
        "/home_extension/dev/git/holidays/src/main/resources"};

    private static boolean test = false;

    public static File getFile(String fileName) {
        if (!test) {
            for (String l : locations) {
                String filePath = l + File.separatorChar + fileName;
                File file = new File(filePath);
                if (file.exists()) {
                    return file;
                }
            }
            return null;
        } else {
            return new File(ConfigResolver.class
                    .getResource("testHolidays.xml").getFile());
        }
    }

    public static void setTestScope() {
        test = true;
    }
}
