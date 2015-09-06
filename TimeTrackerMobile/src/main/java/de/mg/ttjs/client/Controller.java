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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.sksamuel.jqm4gwt.JQMContext;
import com.sksamuel.jqm4gwt.JQMPage;
import com.sksamuel.jqm4gwt.Mobile;

import de.mg.ttjs.client.page.EditPage;
import de.mg.ttjs.client.page.MessagePopup;
import de.mg.ttjs.client.page.TTJSPage;
import de.mg.ttjs.client.page.TTJSPage.RowModel;
import de.mg.ttjs.shared.model.Activity;
import de.mg.ttjs.shared.model.DayModel;
import de.mg.ttjs.shared.model.ID;

@SuppressWarnings("deprecation")
public class Controller implements EventCallbacks, ServerCallbacks {

    private final TTJSPage page;
    private final EditPage editPage;
    private final MessagePopup messageBox;
    private JQMPage currentPage;

    private final ServerCalls serverCalls;

    private DayModel model;
    private Activity editedActivity = null;

    public Controller() {
        serverCalls = new ServerCalls((ServerCallbacks) this);
        page = new TTJSPage((EventCallbacks) this);
        editPage = new EditPage((EventCallbacks) this);
        messageBox = new MessagePopup();
        model = new DayModel();
        changePage(page);
        load();
    }

    @Override
    public final void load() {

        if (page.getSelectedDate() == null) {
            messageBox.show(currentPage, "Please select a date");
        }
        Mobile.showLoadingDialog("loading");
        serverCalls.load(page.getSelectedDate());
        model.setDate(page.getSelectedDate());
    }

    @Override
    public void dataReceived(DayModel data) {
        model = data;
        updateTable();
        Mobile.hideLoadingDialog();
    }

    private void updateTable() {
        page.resetTable();
        for (Activity a : model.getActivities()) {
            RowModel row = new RowModel();
            row.id = (ID) a;
            row.fromHours = a.getFrom().getHours();
            row.fromMins = a.getFrom().getMinutes();
            if (a.getTo() != null) {
                row.toHours = a.getTo().getHours();
                row.toMins = a.getTo().getMinutes();

                long diff = a.getTo().getTime() - a.getFrom().getTime();
                Long diffHours = diff / (1000 * 60 * 60);
                Long diffMinutes = (diff - diffHours * (1000 * 60 * 60))
                        / (1000 * 60);
                row.diffHours = diffHours.intValue();
                row.diffMins = diffMinutes.intValue();
            }

            row.categories.addAll(a.getCategories());
            page.addTableRow(row);
        }
    }

    @Override
    public void edit(ID id) {
        editedActivity = (Activity) id;

        editPage.setFrom(editedActivity.getFrom().getHours(), editedActivity
                .getFrom().getMinutes());
        if (editPage.getToHours() != null && editPage.getToMins() != null) {
            editPage.setTo(editedActivity.getTo().getHours(), editedActivity
                    .getTo().getMinutes());
        }

        editPage.setCategories(model.getAvailableCategories(), editedActivity.getCategories());

        changePage(editPage);
    }

    @Override
    public void submit() {
        Mobile.showLoadingDialog("saving");
        serverCalls.saveOrUpdate(model);
    }

    @Override
    public void successfullySaved(DayModel resultingModel) {
        model = resultingModel;
        updateTable();
        Mobile.hideLoadingDialog();
        messageBox.show(currentPage, "saved");
    }

    @Override
    public void add() {
        editedActivity = null;

        Date now = new Date();

        int hours = now.getHours();
        int minutes = now.getMinutes();
        minutes = minutes / 5 * 5;
        editPage.setFrom(hours, minutes);
        editPage.setTo(hours, minutes);

        editPage.setCategories(model.getAvailableCategories(), new ArrayList<String>());
        changePage(editPage);
    }

    @Override
    public void editSave() {
        if (!isEditValid()) {
            return;
        }
        Activity newA;
        if (editedActivity == null) {
            newA = new Activity();
            model.getActivities().add(newA);
        } else {
            newA = editedActivity;
        }
        newA.setFrom(toDate(editPage.getFromHours(), editPage.getFromMins()));
        newA.setTo(toDate(editPage.getToHours(), editPage.getToMins()));
        newA.getCategories().clear();
        newA.getCategories().addAll(editPage.getSelectedCategories());

        Collections.sort(model.getActivities());

        updateTable();
        changePage(page);
    }

    private boolean isEditValid() {
        if (editPage.getFromHours() != null && editPage.getFromMins() != null
                && editPage.getToHours() != null && editPage.getToMins() != null) {
            int fh = editPage.getFromHours();
            int fm = editPage.getFromMins();
            int th = editPage.getToHours();
            int tm = editPage.getToMins();

            return fh < th || (fh == th && fm <= tm);
        }
        return false;
    }

    @Override
    public void editDelete() {
        if (editedActivity != null) {
            model.getActivities().remove(editedActivity);
            updateTable();
        }

        changePage(page);
    }

    @Override
    public void editCancel() {
        changePage(page);
    }

    private Date toDate(Integer hours, Integer minutes) {
        if (hours == null || minutes == null) {
            return null;
        }
        Date result = new Date();
        result.setHours(hours);
        result.setMinutes(minutes);
        result.setSeconds(0);
        Date selected = (page.getSelectedDate() != null) ? page
                .getSelectedDate() : new Date();
        result.setYear(selected.getYear());
        result.setMonth(selected.getMonth());
        result.setDate(selected.getDate());
        return result;
    }

    @Override
    public void exceptionReceived(String message) {
        messageBox.show(currentPage, "Server Exception: " + message);
    }

    private void changePage(JQMPage newPage) {
        currentPage = newPage;
        JQMContext.changePage(newPage);
    }
}
