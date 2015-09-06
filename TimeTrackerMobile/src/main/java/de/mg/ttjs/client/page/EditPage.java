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
package de.mg.ttjs.client.page;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sksamuel.jqm4gwt.JQMPage;
import com.sksamuel.jqm4gwt.button.JQMButton;
import com.sksamuel.jqm4gwt.form.JQMFieldset;
import com.sksamuel.jqm4gwt.form.elements.JQMSelect;
import com.sksamuel.jqm4gwt.panel.JQMControlGroup;
import com.sksamuel.jqm4gwt.toolbar.JQMHeader;
import de.mg.ttjs.client.EventCallbacks;
import static de.mg.ttjs.client.page.TTJSPage.addIcon;
import java.util.ArrayList;
import java.util.List;

public class EditPage extends JQMPage {

    private final EventCallbacks handler;
    private final JQMButton saveBtn;
    private final JQMButton deleteBtn;
    private final JQMButton cancelBtn;
    private final JQMSelect fromHourSel;
    private final JQMSelect fromMinSel;
    private final JQMSelect toHourSel;
    private final JQMSelect toMinSel;
    private final MySelect catsSel;

    public EditPage(EventCallbacks ec) {
        this.handler = ec;

        withTheme("b");
        add(new JQMHeader("Time Tracker mobile"));

        JQMFieldset fromGrp = new JQMFieldset();
        fromHourSel = renderSelect("from");
        for (int i = 0; i < 24; i++) {
            fromHourSel.addOption(i + "");
        }
        fromGrp.add(fromHourSel);

        fromMinSel = renderSelect("");
        for (int i = 0; i < 60; i += 5) {
            fromMinSel.addOption(i + "");
        }
        fromGrp.add(fromMinSel);
        add(fromGrp);

        JQMControlGroup toGrp = new JQMControlGroup();
        toHourSel = renderSelect("from");
        for (int i = 0; i < 24; i++) {
            toHourSel.addOption(i + "");
        }
        toGrp.add(toHourSel);

        toMinSel = renderSelect("");
        for (int i = 0; i < 60; i += 5) {
            toMinSel.addOption(i + "");
        }
        toGrp.add(toMinSel);
        add(toGrp);

        catsSel = renderMySelect("categories");
        catsSel.setMulitple(true);
        add(catsSel);

        JQMControlGroup buttons = new JQMControlGroup();
        saveBtn = TTJSPage.renderButton("add");
        addIcon(saveBtn, "check");
        saveBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // validation
                boolean valid = (getFromHours() != null && getFromMins() != null);
                if (getToHours() != null || getToMins() != null) {
                    valid |= getToHours() != null && getToMins() != null;
                }
                // TODO
                valid = true;
                if (valid) {
                    handler.editSave();
                }
            }
        });
        buttons.add(saveBtn);

        deleteBtn = TTJSPage.renderButton("delete");
        addIcon(deleteBtn, "delete");
        deleteBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.editDelete();
            }
        });
        buttons.add(deleteBtn);

        cancelBtn = TTJSPage.renderButton("cancel");
        addIcon(cancelBtn, "back");
        cancelBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.editCancel();
            }
        });
        buttons.add(cancelBtn);

        add(buttons);
    }

    public void setFrom(int hours, int minutes) {
        fromHourSel.setValue(hours + "");
        fromMinSel.setValue(minutes + "");
    }

    public void setTo(int hours, int minutes) {
        toHourSel.setValue(hours + "");
        toMinSel.setValue(minutes + "");
    }

    public Integer getFromHours() {
        return getSelectValue(fromHourSel);
    }

    public Integer getFromMins() {
        return getSelectValue(fromMinSel);
    }

    public Integer getToHours() {
        return getSelectValue(toHourSel);
    }

    public Integer getToMins() {
        return getSelectValue(toMinSel);
    }

    private Integer getSelectValue(JQMSelect select) {
        System.out.println(select.getValue());
        if (select.getValue() == null || select.getValue().length() == 0) {
            return null;
        }
        try {
            return Integer.valueOf(select.getValue());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<String> getSelectedCategories() {
        if (catsSel.getSelectedValue() == null) {
            return new ArrayList();
        }
        String[] cats = catsSel.getSelectedValue().split(",");
        List<String> result = new ArrayList();
        for (String cat : cats) {
            result.add(cat);
        }
        return result;
    }

    public void setCategories(List<String> available, List<String> selected) {
        catsSel.clearSelection();
        catsSel.clear();
        for (String cat : available) {
            catsSel.addOption(cat);
        }
        String s = "";
        for (String cat : selected) {
            if (s.length() > 0) {
                s += ",";
            }
            s += cat;
        }
        catsSel.setValue(s);
    }

    private JQMSelect renderSelect(String text) {
        JQMSelect select = new JQMSelect(text);
        select.setCorners(true);
        select.setSelectInline(false);
        select.setNative(false);
        select.setMini(true);
        select.withTheme("b");
        return select;
    }

    private MySelect renderMySelect(String text) {
        MySelect select = new MySelect(text);
        select.setCorners(true);
        select.setNative(false);
        select.setMini(true);
        select.setMini(true);
        return select;
    }

}
