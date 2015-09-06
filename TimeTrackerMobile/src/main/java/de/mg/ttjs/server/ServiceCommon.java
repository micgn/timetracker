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
package de.mg.ttjs.server;

import de.mg.tt.api.TTActivity;
import de.mg.tt.api.TTData;
import de.mg.ttjs.shared.model.Activity;
import de.mg.ttjs.shared.model.DayModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.naming.InitialContext;

public class ServiceCommon {

    //static final String baseUrl = "http://localhost.localdomain:8080/timetracker-1.0.0-SNAPSHOT/rest";
    static String getBaseUrl() {
        try {
            InitialContext env = new InitialContext();
            String url = (String) env.lookup("java:comp/env/resturl");
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static String createDatePathPattern(Date date) {
        DateFormat df = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);
        return "/" + df.format(date) + "/";
    }

    static TTData convertToApi(DayModel model) {
        TTData result = new TTData();
        List<TTActivity> ttas = new ArrayList();
        for (Activity a : model.getActivities()) {
            TTActivity tta = new TTActivity();
            tta.setId(a.getId());
            tta.setFrom(a.getFrom());
            tta.setTo(a.getTo());
            tta.setCategories(a.getCategories());
            ttas.add(tta);
        }
        result.setActivities(ttas);
        // not setting available categories here

        return result;
    }

    static DayModel convertFromApi(TTData data, Date date) {
        DayModel result = new DayModel();
        result.setDate(date);
        if (data.getActivities() != null) {
            for (TTActivity tta : data.getActivities()) {
                Activity a = new Activity();
                a.setId(tta.getId());
                a.setFrom(tta.getFrom());
                a.setTo(tta.getTo());
                if (tta.getCategories() != null) {
                    a.getCategories().addAll(tta.getCategories());
                }
                result.getActivities().add(a);
            }
        }
        if (data.getAvailableCategories() != null) {
            result.getAvailableCategories().addAll(data.getAvailableCategories());
        }
        return result;
    }
}
