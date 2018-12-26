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
package de.mg.tt.ui.utils

import java.util.Date

import com.vaadin.shared.Registration
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.v7.data.Property.ValueChangeEvent
import com.vaadin.v7.event.ItemClickEvent
import com.vaadin.v7.ui.{AbstractField, Table}
import org.vaadin.addons.tuningdatefield.TuningDateField
import org.vaadin.addons.tuningdatefield.event.DateChangeEvent

/**
  * Created by gnatz on 12/28/14.
  */
object ListenerUtils {

  var globalListenerMethods: List[() => Unit] = Nil

  def listenerField[PROPERTY](field: AbstractField[PROPERTY], valueChangeFunction: PROPERTY => Unit): Unit = {
    field.addValueChangeListener((valueChangeEvent: ValueChangeEvent) => {
      valueChangeFunction(valueChangeEvent.getProperty.getValue.asInstanceOf[PROPERTY])
      globalListenerMethods.foreach(method => method())
    })
  }

  def listenerBtn(btn: Button, clickFunction: => Unit): Registration = {
    btn.addClickListener((_: ClickEvent) => {
      clickFunction
      globalListenerMethods.foreach(method => method())
    })
  }

  def tableListener(t: Table, clickFunction: Long => Unit): Unit = {
    t.addItemClickListener((itemClickEvent: ItemClickEvent) => {
      clickFunction(itemClickEvent.getItemId.asInstanceOf[Long])
      globalListenerMethods.foreach(method => method())
    })
  }

  def dateFieldListener(d: TuningDateField, valueChangeFunction: Date => Unit): Unit = {
    d.addDateChangeListener((dateChangeEvent: DateChangeEvent) => {
      valueChangeFunction(DateUtils.toDate(dateChangeEvent.getLocalDate))
      globalListenerMethods.foreach(method => method())
    })
  }

}
