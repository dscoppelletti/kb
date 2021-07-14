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

package it.scoppelletti.kb.app

import it.scoppelletti.kb.app.i18n.AppMessages
import javafx.scene.control.*
import javax.inject.Inject

class SaveDialogBuilder @Inject constructor(
    private val appMessages: AppMessages
) {

    fun build(): Alert {
        val buttonSave = ButtonType(appMessages.commandSave())
        val buttonNotSave = ButtonType(appMessages.commandNotSave())

        return Alert(Alert.AlertType.CONFIRMATION).apply {
            title = appMessages.commandClose()
            headerText = null
            contentText = appMessages.promptSaveChanges()
            buttonTypes.setAll(buttonSave, buttonNotSave, ButtonType.CANCEL)
        }
    }
}