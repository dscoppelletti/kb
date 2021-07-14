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

package it.scoppelletti.kb.app.model

import it.scoppelletti.kb.app.adapter.AuthorListStringConverter
import it.scoppelletti.kb.app.adapter.TagListStringConverter
import it.scoppelletti.kb.app.i18n.AppMessages
import javafx.collections.ObservableList
import javafx.util.converter.LocalDateStringConverter
import it.scoppelletti.kb.domain.model.ArticleModel
import java.time.LocalDate
import java.time.format.FormatStyle
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections

class ArticleForm {

    private val _title = SimpleStringProperty()
    private val _authors = SimpleListProperty<String>()
    private val _authorsEdit = SimpleStringProperty()
    private val _publishedDate = SimpleObjectProperty<LocalDate?>()
    private val _publishedDateEdit = SimpleStringProperty()
    private val _url = SimpleStringProperty()
    private val _file = SimpleStringProperty()
    private val _remark = SimpleStringProperty()
    private val _tags = SimpleListProperty<String>()
    private val _tagsEdit = SimpleStringProperty()
    private val _changed = SimpleBooleanProperty()

    init {
        _title.addListener(this::onChanged)
        _authors.addListener(this::onChanged)
        _publishedDate.addListener(this::onChanged)
        _url.addListener(this::onChanged)
        _file.addListener(this::onChanged)
        _remark.addListener(this::onChanged)
        _tags.addListener(this::onChanged)

        _publishedDateEdit.bindBidirectional(_publishedDate,
            LocalDateStringConverter(FormatStyle.SHORT))
        _authorsEdit.bindBidirectional(_authors, AuthorListStringConverter)
        _tagsEdit.bindBidirectional(_tags, TagListStringConverter)
    }

    var articleId: String? = null

    fun titleProperty() = _title

    var title: String?
        get() = _title.value
        set(value) {
            _title.value = value
        }

    fun authorsProperty(): ListProperty<String> = _authors

    var authors: ObservableList<String>?
        get() = _authors.value
        set(value) {
            _authors.value = value
        }

    fun authorsEditProperty(): StringProperty = _authorsEdit

    var authorsEdit: String?
        get() = _authorsEdit.value
        set(value) {
            _authorsEdit.value = value
        }

    fun publishedDateProperty(): ObjectProperty<LocalDate?> = _publishedDate

    var publishedDate: LocalDate?
        get() = _publishedDate.value
        set(value) {
            _publishedDate.value = value
        }

    fun publishedDateEditProperty(): StringProperty = _publishedDateEdit

    var publishedDateEdit: String?
        get() = _publishedDateEdit.value
        set(value) {
            _publishedDateEdit.value = value
        }

    fun urlProperty(): StringProperty = _url

    var url: String?
        get() = _url.value
        set(value) {
            _url.value = value
        }

    fun fileProperty(): StringProperty = _file

    var file: String?
        get() = _file.value
        set(value) {
            _file.value = value
        }

    fun remarkProperty(): StringProperty = _remark

    var remark: String?
        get() = _remark.value
        set(value) {
            _remark.value = value
        }

    fun tagsProperty(): ListProperty<String> = _tags

    var tags: ObservableList<String>?
        get() = _tags.value
        set(value) {
            _tags.value = value
        }

    fun tagsEditProperty(): StringProperty = _tagsEdit

    var tagsEdit: String?
        get() = _tagsEdit.value
        set(value) {
            _tagsEdit.value = value
        }

    fun changedProperty(): BooleanProperty = _changed

    var changed: Boolean
        get() = _changed.value
        set(value) {
            _changed.value = value
        }

    @Suppress("UNUSED_PARAMETER")
    private fun <T> onChanged(observable: ObservableValue<out T>, oldValue: T,
                              newValue: T) {
        if (newValue != oldValue) {
            changed = true
        }
    }

    fun copy(source: ArticleModel) {
        articleId = source.id
        title = source.title
        authors = FXCollections.observableList(source.authors)
        publishedDate = source.publishedDate
        url = source.url
        file = source.file.orEmpty()
        remark = source.remark.orEmpty()
        tags = FXCollections.observableList(source.tags)
        changed = false
    }

    fun toModel(appMessages: AppMessages) = ArticleModel(
        id = articleId,
        title = checkNotNullOrBlank(title) { appMessages.errorTitleRequired() },
        authors = checkNotNullOrEmpty(authors?.toList()) {
            appMessages.errorAuthorsRequired()
        },
        publishedDate = checkNotNull(publishedDate) {
            appMessages.errorPublishedDateRequired()
        },
        url = checkNotNullOrBlank(url) { appMessages.errorUrlRequired() },
        file = if (file.isNullOrBlank()) null else file,
        remark = if (remark.isNullOrBlank()) null else remark,
        tags = checkNotNullOrEmpty(tags?.toList()) {
            appMessages.errorTagsRequired()
        }
    )
}