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
import it.scoppelletti.kb.app.model.FindForm
import it.scoppelletti.kb.domain.model.PageRequest
import java.lang.Exception
import java.time.LocalDate
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javax.inject.Inject
import javax.inject.Named

class MainSceneBuilder @Inject constructor(
    @Named(MainApp.STAGE_PRIMARY)
    private val stage: Stage,

    private val viewModelFactory: MainViewModelFactory,
    private val articleStageBuilder: ArticleStageBuilder,
    private val findDlgBuilder: FindDialogBuilder,
    private val exDlgBuilder: ExceptionDialogBuilder,
    private val appMessages: AppMessages
) {

    fun build(): Scene {
        val viewModel = viewModelFactory.create()

        val table = buildTable()
        val pager = buildPagination()
        val menuBar = buildMenuBar(viewModel, table, pager)

        pager.currentPageIndexProperty().addListener { _, _, newValue ->
            if (pager.userData == null) {
                list(viewModel, table, pager, newValue.toInt())
            }
        }

        val vBox = VBox(menuBar, HBox(table).apply {
            styleClass.add("table")
        }, pager)

        return Scene(vBox).apply {
            stylesheets.add("styles.css")
        }
    }

    private fun buildPagination() = Pagination().apply {
        pageCount = 1
        currentPageIndex = 0
        maxPageIndicatorCount = 5
        styleClass.add("pagination")
    }

    private fun buildTable(): TableView<ArticleForm> {
        val colTitle = TableColumn<ArticleForm, String>(
            appMessages.labelTitle()
        ).apply {
            cellValueFactory = PropertyValueFactory("title")
        }

        val colAuthors = TableColumn<ArticleForm, String>(
            appMessages.labelAuthors()
        ).apply {
            cellValueFactory = PropertyValueFactory("authorsEdit")
        }

        val colPublishedDate = TableColumn<ArticleForm, LocalDate>(
            appMessages.labelPublishedDate()
        ).apply {
            cellValueFactory = PropertyValueFactory("publishedDateEdit")
        }

        val colUrl = TableColumn<ArticleForm, String>(
            appMessages.labelUrl()
        ).apply {
            cellValueFactory = PropertyValueFactory("url")
        }

        val colFile = TableColumn<ArticleForm, String>(
            appMessages.labelFile()
        ).apply {
            cellValueFactory = PropertyValueFactory("file")
        }

        val colRemark = TableColumn<ArticleForm, String>(
            appMessages.labelRemark()
        ).apply {
            cellValueFactory = PropertyValueFactory("remark")
        }

        val colTags = TableColumn<ArticleForm, String>(
            appMessages.labelTags()
        ).apply {
            cellValueFactory = PropertyValueFactory("tagsEdit")
        }

        return TableView<ArticleForm>().apply {
            setSortPolicy { false }
            columns.addAll(
                colTitle, colAuthors, colPublishedDate,
                colUrl, colFile, colRemark, colTags
            )

            setRowFactory {
                TableRow<ArticleForm>().apply {
                    setOnMouseClicked { event ->
                        if (event.clickCount == 2 && !this.isEmpty) {
                            this.item.articleId?.let {
                                read(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildMenuBar(
        viewModel: MainViewModel,
        table: TableView<ArticleForm>,
        pager: Pagination
    ): MenuBar {
        val findMenuItem = MenuItem(appMessages.commandFind()).apply {
            setOnAction {
                findDlgBuilder.build(viewModel).apply {
                    initOwner(stage)
                }.showAndWait().ifPresent { findForm ->
                    viewModel.findForm = FindForm.copy(findForm)
                    list(viewModel, table, pager, 0)
                }
            }
        }

        val addMenuItem = MenuItem(appMessages.commandAdd()).apply {
            setOnAction {
                add()
            }
        }

        val articleMenu = Menu(appMessages.commandArticle()).apply {
            items.addAll(findMenuItem, addMenuItem)
        }

        return MenuBar().apply {
            menus.add(articleMenu)
        }
    }

    private fun add() {
        articleStageBuilder.build(null).apply {
            initOwner(stage)
            show()
        }
    }

    private fun read(articleId: String) {
        articleStageBuilder.build(articleId).apply {
            initOwner(stage)
            show()
        }
    }

    private fun list(
        viewModel: MainViewModel,
        tableView: TableView<ArticleForm>,
        pager: Pagination,
        pageIdx: Int
    ) {
        val pageReq = PageRequest(pageIdx, viewModel.page.pageSize)

        try {
            viewModel.list(pageReq)
        } catch (ex: Exception) {
            exDlgBuilder.build(ex, appMessages.commandFind()).apply {
                initOwner(stage)
                showAndWait()
            }

            return
        }

        tableView.items = viewModel.articles
        tableView.scrollToColumnIndex(0)
        if (viewModel.page.content.isNotEmpty()) {
            tableView.scrollTo(0)
        }

        with(pager) {
            userData = this

            try {
                pageCount = viewModel.page.pageCount
                maxPageIndicatorCount = 5
                currentPageIndex = pageIdx
            } finally {
                userData = null
            }
        }
    }
}