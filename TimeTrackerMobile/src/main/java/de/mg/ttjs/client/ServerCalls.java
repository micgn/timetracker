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
package de.mg.ttjs.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.mg.ttjs.shared.model.DayModel;

public class ServerCalls {

    private final ServerCallbacks controller;
    private final LoadServiceAsync loadService;
    private final SaveServiceAsync saveService;

    public ServerCalls(ServerCallbacks controller) {
        this.controller = controller;
        loadService = GWT.create(LoadService.class);
        saveService = GWT.create(SaveService.class);
    }

    public void load(Date date) {

        AsyncCallback<DayModel> callback = new AsyncCallback<DayModel>() {
            public void onFailure(Throwable caught) {
                controller.exceptionReceived(extractProblem(caught));
            }

            public void onSuccess(DayModel data) {
                controller.dataReceived(data);
            }
        };
        loadService.load(date, callback);
    }

    public void saveOrUpdate(DayModel data) {

        AsyncCallback<DayModel> callback = new AsyncCallback<DayModel>() {
            public void onFailure(Throwable caught) {
                controller.exceptionReceived(extractProblem(caught));
            }

            public void onSuccess(DayModel model) {
                controller.successfullySaved(model);
            }
        };
        saveService.saveOrUpdate(data, callback);
    }

    private String extractProblem(Throwable th) {
        while (th.getCause() != null) {
            th = th.getCause();
        }
        return th.getMessage();
    }

}
