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

import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.event.ItemClickEvent
import com.vaadin.event.ItemClickEvent.ItemClickListener
import com.vaadin.ui.Button.{ClickEvent, ClickListener}
import com.vaadin.ui._
import org.vaadin.addons.tuningdatefield.TuningDateField
import org.vaadin.addons.tuningdatefield.event.{DateChangeEvent, DateChangeListener}

/**
 * Created by gnatz on 12/28/14.
 */
object ListenerUtils {

  var globalListenerMethods: List[() => Unit] = Nil

  def listener[PROPERTY](field: AbstractField[PROPERTY], valueChangeFunction: PROPERTY => Unit) = {
    field.addValueChangeListener(new ValueChangeListener {
      override def valueChange(valueChangeEvent: ValueChangeEvent): Unit = {
        valueChangeFunction(valueChangeEvent.getProperty.getValue.asInstanceOf[PROPERTY])
        globalListenerMethods.foreach(method => method())
      }
    })
  }

  def listener(btn: Button, clickFunction: => Unit) = {
    btn.addClickListener(new ClickListener {
      override def buttonClick(clickEvent: ClickEvent): Unit = {
        clickFunction
        globalListenerMethods.foreach(method => method())
      }
    })
  }

  def tableListener(t: Table, clickFunction: Long => Unit) = {
    t.addItemClickListener(new ItemClickListener {
      override def itemClick(itemClickEvent: ItemClickEvent): Unit = {
        clickFunction(itemClickEvent.getItemId.asInstanceOf[Long])
        globalListenerMethods.foreach(method => method())
      }
    })
  }

  def dateFieldListener(d: TuningDateField, valueChangeFunction: Date => Unit) = {
    d.addDateChangeListener(new DateChangeListener {
      override def dateChange(dateChangeEvent: DateChangeEvent): Unit = {
        valueChangeFunction(dateChangeEvent.getLocalDate().toDate)
        globalListenerMethods.foreach(method => method())
      }
    })
  }

}
