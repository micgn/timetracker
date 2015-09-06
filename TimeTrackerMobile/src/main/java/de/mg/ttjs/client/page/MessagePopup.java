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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.sksamuel.jqm4gwt.JQMContext;
import com.sksamuel.jqm4gwt.JQMDialog;
import com.sksamuel.jqm4gwt.JQMPage;
import com.sksamuel.jqm4gwt.Transition;

public class MessagePopup {

    private final JQMDialog dialog;
    private final Label label;

    public MessagePopup() {
        label = new Label("");
        label.setWordWrap(true);
        dialog = new JQMDialog(label);
        dialog.setCorners(true);
        dialog.withTransition(Transition.NONE);
    }

    public void show(final JQMPage page, String text) {
        label.setText(text);
        JQMContext.changePage(dialog);

        Timer t = new Timer() {
            @Override
            public void run() {
                JQMContext.changePage(page);
            }
        };
        t.schedule(2500);
    }
}
