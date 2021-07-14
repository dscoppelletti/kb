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
import it.scoppelletti.kb.app.model.ArticleForm
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.*
import javax.inject.Inject

class ArticleStageBuilder @Inject constructor(
    private val viewModelFactory: ArticleViewModelFactory,
    private val saveDlgBuilder: SaveDialogBuilder,
    private val confirmDlgBuilder: DeleteDialogBuilder,
    private val exDlgBuilder: ExceptionDialogBuilder,
    private val appMessages: AppMessages
) {

    fun build(articleId: String?): Stage {
        val viewModel = viewModelFactory.create()
        val form = viewModel.form

        val stage = Stage()
        val grid = buildGrid(form)
        val menuBar = buildMenuBar(stage, articleId, viewModel)
        val vBox = VBox(menuBar, grid)

        return stage.apply {
            initModality(Modality.APPLICATION_MODAL)
            initStyle(StageStyle.DECORATED)
            title = appMessages.commandArticle()

            scene = Scene(vBox).apply {
                stylesheets.add("styles.css")
            }

            setOnShown {
                if (!articleId.isNullOrBlank() &&
                    form.articleId.isNullOrBlank()) {
                    readArticle(stage, articleId, viewModel)
                }
            }

            setOnCloseRequest { event ->
                if (form.changed) {
                    onClosing(this, event, viewModel)
                }
            }
        }
    }

    private fun buildGrid(form: ArticleForm): GridPane {
        val txtTitle = TextField().apply {
            textProperty().bindBidirectional(form.titleProperty())
        }

        val txtAuthors = TextField().apply {
            textProperty().bindBidirectional(form.authorsEditProperty())
        }

        val txtPublishedDate = TextField().apply {
            textProperty().bindBidirectional(form.publishedDateEditProperty())
        }

        val txtUrl = TextField().apply {
            textProperty().bindBidirectional(form.urlProperty())
        }

        val txtFile = TextField().apply {
            isDisable = true
            textProperty().bind(form.fileProperty())
        }

        val txtRemark = TextArea().apply {
            textProperty().bindBidirectional(form.remarkProperty())
        }

        val txtTags = TextField().apply {
            textProperty().bindBidirectional(form.tagsEditProperty())
        }

        return GridPane().apply {
            add(Label(appMessages.labelTitle()), 0, 0, 1, 1)
            add(txtTitle, 1, 0, 1, 1)
            add(Label(appMessages.labelAuthors()), 0, 1, 1, 1)
            add(txtAuthors, 1, 1, 1, 1)
            add(Label(appMessages.labelPublishedDate()), 0, 2, 1, 1)
            add(txtPublishedDate, 1, 2, 1, 1)
            add(Label(appMessages.labelUrl()), 0, 3, 1, 1)
            add(txtUrl, 1, 3, 1, 1)
            add(Label(appMessages.labelFile()), 0, 4, 1, 1)
            add(txtFile, 1, 4, 1, 1)
            add(Label(appMessages.labelRemark()), 0, 5, 1, 1)
            add(txtRemark, 1, 5, 1, 1)
            add(Label(appMessages.labelTags()), 0, 6, 1, 1)
            add(txtTags, 1, 6, 1, 1)
            styleClass.add("form")
        }
    }

    private fun buildMenuBar(
        stage: Stage,
        articleId: String?,
        viewModel: ArticleViewModel
    ): MenuBar {
        val form = viewModel.form

        val saveArticleItem = MenuItem(appMessages.commandSave()).apply {
            isDisable = !articleId.isNullOrBlank()
            setOnAction {
                saveArticle(stage, viewModel)
            }
        }

        val deleteArticleItem = MenuItem(appMessages.commandDelete()).apply {
            isDisable = articleId.isNullOrBlank()
            setOnAction {
                confirmDlgBuilder.build(appMessages.promptDeleteArticle())
                    .apply {
                        initOwner(stage)
                    }.showAndWait().ifPresent { button ->
                        if (button == ButtonType.OK) {
                            deleteArticle(stage, viewModel)
                        }
                    }
            }
        }

        val uploadFileItem = MenuItem(appMessages.commandUpload()).apply {
            isDisable = articleId.isNullOrBlank()
            setOnAction {
                uploadFile(stage, viewModel)
            }
        }

        val downloadFileItem = MenuItem(appMessages.commandDownload()).apply {
            isDisable = true
            setOnAction {
                downloadFile(stage, viewModel)
            }
        }

        val deleteFileItem = MenuItem(appMessages.commandDelete()).apply {
            isDisable = true
            setOnAction {
                confirmDlgBuilder.build(appMessages.promptDeleteFile())
                    .apply {
                        initOwner(stage)
                    }.showAndWait().ifPresent { button ->
                        if (button == ButtonType.OK) {
                            deleteFile(stage, viewModel)
                        }
                    }
            }
        }

        form.changedProperty().addListener { _, _, newValue ->
            // Enable if article is new or has been changed
            saveArticleItem.isDisable = !(form.articleId.isNullOrBlank() ||
                    newValue)

            // Enable if article exists and has not been changed
            deleteArticleItem.isDisable = !(!form.articleId.isNullOrBlank() &&
                    !newValue)
            uploadFileItem.isDisable = deleteArticleItem.isDisable
            deleteFileItem.isDisable = deleteArticleItem.isDisable ||
                    form.file.isNullOrBlank()
        }

        form.fileProperty().addListener { _, _, newValue ->
            downloadFileItem.isDisable = newValue.isNullOrBlank()
            deleteFileItem.isDisable = downloadFileItem.isDisable ||
                    !(!form.articleId.isNullOrBlank() && !form.changed)
        }

        val articleMenu = Menu(appMessages.commandArticle()).apply {
            items.addAll(saveArticleItem, SeparatorMenuItem(),
                deleteArticleItem)
        }

        val fileMenu = Menu(appMessages.commandFile()).apply {
            items.addAll(uploadFileItem, downloadFileItem, SeparatorMenuItem(),
                deleteFileItem)
        }

        return MenuBar().apply {
            menus.addAll(articleMenu, fileMenu)
        }
    }

    private fun readArticle(
        stage: Stage,
        articleId: String,
        viewModel: ArticleViewModel) {
        try {
            viewModel.readArticle(articleId)
        } catch (ex: Exception) {
            exDlgBuilder.build(ex, appMessages.commandRead()).apply {
                initOwner(stage)
                showAndWait()
            }

            viewModel.form.changed = false
            stage.close()
        }
    }

    private fun saveArticle(stage: Stage, viewModel: ArticleViewModel) {
        try {
            viewModel.saveArticle()
        } catch (ex: Exception) {
            exDlgBuilder.build(ex, appMessages.commandSave()).apply {
                initOwner(stage)
                showAndWait()
            }
        }
    }

    private fun deleteArticle(stage: Stage, viewModel: ArticleViewModel) {
        try {
            viewModel.deleteArticle()
        } catch (ex: Exception) {
            exDlgBuilder.build(ex, appMessages.commandDelete()).apply {
                initOwner(stage)
                showAndWait()
            }

            return
        }

        stage.close()
    }

    private fun uploadFile(stage: Stage, viewModel: ArticleViewModel) {
        val fileChooser = FileChooser().apply {
            title = appMessages.commandUpload()
        }

        fileChooser.showOpenDialog(stage)?.let { file ->
            try {
                viewModel.uploadFile(file)
            } catch (ex: Exception) {
                exDlgBuilder.build(ex, appMessages.commandDelete()).apply {
                    initOwner(stage)
                    showAndWait()
                }
            }
        }
    }

    private fun downloadFile(stage: Stage, viewModel: ArticleViewModel) {
        val fileChooser = FileChooser().apply {
            title = appMessages.commandDownload()
            viewModel.form.file?.let {
                initialFileName = it
            }
        }

        fileChooser.showSaveDialog(stage)?.let { file ->
            try {
                viewModel.downloadFile(file)
            } catch (ex: Exception) {
                exDlgBuilder.build(ex, appMessages.commandDelete()).apply {
                    initOwner(stage)
                    showAndWait()
                }
            }
        }
    }

    private fun deleteFile(stage: Stage, viewModel: ArticleViewModel) {
        try {
            viewModel.deleteFile()
        } catch (ex: Exception) {
            exDlgBuilder.build(ex, appMessages.commandDelete()).apply {
                initOwner(stage)
                showAndWait()
            }
        }
    }

    private fun onClosing(
        stage: Stage,
        event: WindowEvent,
        viewModel: ArticleViewModel
    ) {
        saveDlgBuilder.build().apply {
            initOwner(stage)
        }.showAndWait().ifPresent { button ->
            when (button.text) {
                appMessages.commandSave() -> {
                    viewModel.saveArticle()
                    stage.close()
                }
                appMessages.commandNotSave() -> {
                    // NOP
                }
                else -> {
                    event.consume()
                }
            }
        }
    }
}