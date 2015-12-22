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
package de.mg.tt.util

/**
 * Created by gnatz on 7/1/15.
 */
object FloatHelper {

  def toFloat(s: String): Float = {

    def parse(s: String) : Option[Float] =
      try {
        Option(s.toFloat)
      }
      catch {
        case _:Throwable => None
      }

    if (parse(s).nonEmpty) parse(s).get
    else {
      val fOpt = parse(s.replace(".", ","))
      if (fOpt.nonEmpty) fOpt.get
      else {
        val fOpt2 = parse(s.replace(",", "."))
        if (fOpt2.nonEmpty) fOpt2.get else 0.0f
      }
    }
  }

}
