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

import com.vaadin.server.Sizeable
import com.vaadin.ui.{Button, HorizontalLayout, VerticalLayout}

/**
 * Created by gnatz on 12/28/14.
 */
object LayoutUtils {

  val TZ = "Europe/Berlin"

  def vl(margin:Boolean=true, spacing:Boolean=true, border:Boolean=false) = {
    val l = new VerticalLayout
    l.setSpacing(spacing)
    l.setMargin(margin)
    if (border) l.setStyleName("border")
    l
  }

  def hl(margin:Boolean=true, spacing:Boolean=true, border:Boolean=false) = {
    val l = new HorizontalLayout
    l.setSpacing(spacing)
    l.setMargin(margin)
    if (border) l.setStyleName("border")
    l
  }

  def btn(b: Button, title: String, important:Boolean=false, small:Boolean=false) = {
    if (!small)
      b.setWidth(10, Sizeable.Unit.EM)
    else {
      b.setWidth(5, Sizeable.Unit.EM)
      b.setStyleName("small-btn")
    }
    b.setCaption(title)
    if (important) b.setStyleName("important")
  }

}
