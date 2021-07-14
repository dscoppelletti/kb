/*
 * Copyright (C) 2021 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.scoppelletti.kb.app.adapter

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.StringConverter

object TagListStringConverter : StringConverter<ObservableList<String>?>() {

    override fun toString(obj: ObservableList<String>?): String =
        obj?.joinToString(" ") ?: ""

    override fun fromString(value: String?): ObservableList<String>? =
        if (value.isNullOrBlank()) FXCollections.observableArrayList() else
            FXCollections.observableArrayList(value.trim().lowercase()
                .split(Regex("\\s+")))
}