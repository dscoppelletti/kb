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

import javax.inject.Inject
import it.scoppelletti.kb.domain.ArticleUseCase
import it.scoppelletti.kb.app.model.FindForm
import it.scoppelletti.kb.domain.model.ArticleModel
import javafx.collections.ObservableList
import it.scoppelletti.kb.app.model.ArticleForm
import it.scoppelletti.kb.domain.model.Page
import it.scoppelletti.kb.domain.model.PageRequest
import javafx.collections.FXCollections

class MainViewModel(
    private val useCase: ArticleUseCase
) {

    private var _findForm = FindForm()
    private var _page: Page<ArticleModel> = Page.empty(1)
    private var _articles: ObservableList<ArticleForm> =
        FXCollections.emptyObservableList()

    var findForm: FindForm
        get() = _findForm
        set(value) {
            _findForm = value
            _page = Page.empty(_findForm.pageSize)
        }

    val page: Page<ArticleModel>
        get() = _page

    val articles: ObservableList<ArticleForm>
        get() = _articles

    fun list(pageReq: PageRequest) {
        _page = useCase.list(findForm.toModel(), pageReq)
        _articles = FXCollections.observableArrayList(
            _page.content
                .map {
                    ArticleForm().apply {
                        copy(it)
                    }
                })
    }
}

class MainViewModelFactory @Inject constructor(
    private val useCase: ArticleUseCase
) {

    fun create() = MainViewModel(useCase)
}