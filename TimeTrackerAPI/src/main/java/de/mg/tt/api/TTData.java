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
package de.mg.tt.api;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TTData {

    private List<TTActivity> activities;
    private List<String> availableCategories;
    private Long weekMinutes;

    public List<TTActivity> getActivities() {
        return activities;
    }

    public List<String> getAvailableCategories() {
        return availableCategories;
    }

    public void setActivities(List<TTActivity> activities) {
        this.activities = activities;
    }

    public void setAvailableCategories(List<String> availableCategories) {
        this.availableCategories = availableCategories;
    }

    public Long getWeekMinutes() {
        return weekMinutes;
    }

    public void setWeekMinutes(Long weekMinutes) {
        this.weekMinutes = weekMinutes;
    }
}
