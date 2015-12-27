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
package de.mg.tt.ui.compAction

import com.vaadin.ui.{Button, NativeSelect}

/**
 * Created by gnatz on 7/26/15.
 */
class ActionViewModel {
  val allBtn = new Button
  val noneBtn = new Button
  val invertBtn = new Button
  val catChooser = new NativeSelect
  val addCatBtn = new Button
  val deleteBtn = new Button
  val newBtn = new Button
  val newCatBtn = new Button
  val saveBtn = new Button
  val revertBtn = new Button
  val delCatChooser = new NativeSelect
  val delCatBtn = new Button
  val openMiscBtn = new Button
}
