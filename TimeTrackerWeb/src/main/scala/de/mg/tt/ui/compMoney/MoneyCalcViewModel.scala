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
package de.mg.tt.ui.compMoney

import com.vaadin.ui.{Button, TextField, Window}

/**
 * Created by gnatz on 7/26/15.
 */
class MoneyCalcViewModel {
  val moneyCalcW = new Window
  val moneyHours = new TextField
  val rate = new TextField
  val tax = new TextField
  val moneyWithTax = new TextField
  val moneyWithoutTax = new TextField
  val moneyCalcBtn = new Button
}
