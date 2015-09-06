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
import com.sksamuel.jqm4gwt.IconPos;
import com.sksamuel.jqm4gwt.JQMContext;
import com.sksamuel.jqm4gwt.JQMPage;
import com.sksamuel.jqm4gwt.Transition;
import com.sksamuel.jqm4gwt.button.JQMButton;
import com.sksamuel.jqm4gwt.html.Paragraph;
import com.sksamuel.jqm4gwt.layout.JQMTable;
import com.sksamuel.jqm4gwt.panel.JQMControlGroup;
import com.sksamuel.jqm4gwt.plugins.datebox.JQMCalBox;
import com.sksamuel.jqm4gwt.toolbar.JQMHeader;
import de.mg.ttjs.client.EventCallbacks;
import de.mg.ttjs.shared.model.ID;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TTJSPage extends JQMPage {

    private final EventCallbacks handler;
    private final JQMCalBox dateBox;
    private final JQMButton loadBtn;
    private final JQMButton submitBtn;
    private final JQMButton addBtn;
    private final JQMTable table;

    public TTJSPage(EventCallbacks ec) {

        this.handler = ec;

        withTheme("b");
        JQMContext.setDefaultTransition(Transition.NONE);

        add(new JQMHeader("Time Tracker mobile"));

        dateBox = new JQMCalBox("select");
        dateBox.withTheme("b");

        dateBox.setCorners(true);
        dateBox.setWidth("15em");
        dateBox.setDateFormat("%a %d.%m.%y");
        dateBox.setDate(new Date());
        loadBtn = renderButton("load");
        addIcon(loadBtn, "refresh");
        loadBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.load();
            }
        });
        add(dateBox);
        add(loadBtn);

        table = new JQMTable(5);
        add(table);

        JQMControlGroup buttonsGrp = new JQMControlGroup();
        addBtn = renderButton("add");
        addIcon(addBtn, "plus");
        addBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.add();
            }
        });

        submitBtn = renderButton("save");
        addIcon(submitBtn, "action");
        submitBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.submit();
            }
        });
        buttonsGrp.add(addBtn);
        buttonsGrp.add(submitBtn);

        add(buttonsGrp);
    }

    static JQMButton renderButton(String text) {
        JQMButton btn = new JQMButton(text);
        btn.setCorners(true);
        btn.setInline(true);
        return btn;
    }

    static void addIcon(JQMButton btn, String icon) {
        btn.setIconPos(IconPos.LEFT);
        btn.addStyleName("ui-btn-icon-left");
        btn.addStyleName("ui-icon-" + icon);
    }

    static Paragraph renderBold(String text) {
        Paragraph p = new Paragraph("from");
        p.setHTML("<b>" + text + "</b>");
        return p;
    }

    public Date getSelectedDate() {
        return dateBox.getDate();
    }

    public void resetTable() {
        table.clear();
        table.add(renderBold("from"));
        table.add(renderBold("to"));
        table.add(renderBold("length"));
        table.add(renderBold("categories"));
        table.add(renderBold(""));
    }

    public void addTableRow(final RowModel row) {
        table.add(new Paragraph(toTwoDigitsStr(row.fromHours) + ":" + toTwoDigitsStr(row.fromMins)));
        table.add(new Paragraph(toTwoDigitsStr(row.toHours) + ":" + toTwoDigitsStr(row.toMins)));
        table.add(new Paragraph(toTwoDigitsStr(row.diffHours) + ":" + toTwoDigitsStr(row.diffMins)));
        String cats = "";
        for (String cat : row.categories) {
            cats += cat + " ";
        }
        table.add(new Paragraph(cats));
        JQMButton editBtn = renderButton("edit");
        addIcon(editBtn, "edit");
        editBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.edit(row.id);
            }
        });
        table.add(editBtn);
    }

    private String toTwoDigitsStr(Integer i) {
        if (i == null) {
            return "";
        }
        if (i < 10) {
            return "0" + i;
        }
        return i + "";
    }

    public static class RowModel {

        public ID id;
        public Integer fromHours;
        public Integer fromMins;
        public Integer toHours;
        public Integer toMins;
        public Integer diffHours;
        public Integer diffMins;
        public final List<String> categories = new ArrayList();
    }
}
