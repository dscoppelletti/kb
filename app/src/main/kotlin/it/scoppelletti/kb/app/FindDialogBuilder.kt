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

@file:Suppress("RemoveRedundantQualifierName")

package it.scoppelletti.kb.app

import it.scoppelletti.kb.app.i18n.AppMessages
import it.scoppelletti.kb.app.model.FindForm
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.Modality
import javafx.stage.StageStyle
import javafx.util.StringConverter
import javafx.util.converter.NumberStringConverter
import javax.inject.Inject

class FindDialogBuilder @Inject constructor(
    private val appMessages: AppMessages
) {

    fun build(viewModel: MainViewModel): Dialog<FindForm?> {
        val form: FindForm = FindForm.copy(viewModel.findForm)

        val dlg = Dialog<FindForm?>().apply {
            initModality(Modality.APPLICATION_MODAL)
            initStyle(StageStyle.DECORATED)
            title = appMessages.commandFind()
            headerText = null
        }

        val txtPageSize = TextField().apply {
            textProperty().bindBidirectional(form.pageSizeProperty(),
                FindDialogBuilder.intConverter)
        }

        val txtAuthors = TextField().apply {
            textProperty().bindBidirectional(form.authorsEditProperty())
        }

        val txtPublishedDateMin = TextField().apply {
            textProperty().bindBidirectional(
                form.publishedDateMinEditProperty())
        }

        val txtPublishedDateMax = TextField().apply {
            textProperty().bindBidirectional(
                form.publishedDateMaxEditProperty())
        }

        val txtRequiredTags = TextField().apply {
            textProperty().bindBidirectional(form.requiredTagsEditProperty())
        }

        val txtOptionalTags = TextField().apply {
            textProperty().bindBidirectional(form.optionalTagsEditProperty())
        }

        val grid = GridPane().apply {
            add(Label(appMessages.labelPageSize()), 0, 0, 1, 1)
            add(txtPageSize, 1, 0, 1, 1)
            add(Label(appMessages.labelAuthors()), 0, 1, 1, 1)
            add(txtAuthors, 1, 1, 1, 1)
            add(Label(appMessages.labelPublishedDateMin()), 0, 2, 1, 1)
            add(txtPublishedDateMin, 1, 2, 1, 1)
            add(Label(appMessages.labelPublishedDateMax()), 0, 3, 1, 1)
            add(txtPublishedDateMax, 1, 3, 1, 1)
            add(Label(appMessages.labelRequiredTags()), 0, 4, 1, 1)
            add(txtRequiredTags, 1, 4, 1, 1)
            add(Label(appMessages.labelOptionalTags()), 0, 5, 1, 1)
            add(txtOptionalTags, 1, 5, 1, 1)
            styleClass.add("form")
        }

        dlg.dialogPane.apply {
            content = grid
            buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
            stylesheets.add("styles.css")
        }

        val cmdOK = dlg.dialogPane.lookupButton(ButtonType.OK).apply {
            isDisable = !form.validate()
        }

        val validator = ChangeListener<Any?> { _, _, _ ->
            cmdOK.isDisable = !form.validate()
        }

        form.pageSizeProperty().addListener(validator)
        form.authorsProperty().addListener(validator)
        form.publishedDateMinProperty().addListener(validator)

        form.publishedDateMinEditProperty().addListener(InvalidationListener {
            cmdOK.isDisable = !form.publishedDateMinEdit.isNullOrBlank()
        })

        form.publishedDateMaxProperty().addListener(validator)

        form.publishedDateMaxEditProperty().addListener(InvalidationListener {
            cmdOK.isDisable = !form.publishedDateMaxEdit.isNullOrBlank()
        })

        form.requiredTagsProperty().addListener(validator)
        form.optionalTagsProperty().addListener(validator)

        dlg.setResultConverter { buttonType ->
            if (buttonType == ButtonType.OK) form else null
        }

        return dlg
    }

    companion object {

        private val intConverter: StringConverter<Number> =
            NumberStringConverter()
    }
}