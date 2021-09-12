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
import it.scoppelletti.kb.domain.model.ArticleFindModel
import java.time.LocalDate
import java.time.format.FormatStyle
import javafx.beans.property.*
import javafx.collections.ObservableList
import javafx.util.converter.LocalDateStringConverter

class FindForm {
    private val _pageSize = SimpleIntegerProperty()
    private val _authors = SimpleListProperty<String>()
    private val _authorsEdit = SimpleStringProperty()
    private val _publishedDateMin = SimpleObjectProperty<LocalDate?>()
    private val _publishedDateMinEdit = SimpleStringProperty()
    private val _publishedDateMax = SimpleObjectProperty<LocalDate?>()
    private val _publishedDateMaxEdit = SimpleStringProperty()
    private val _requiredTags = SimpleListProperty<String>()
    private val _requiredTagsEdit = SimpleStringProperty()
    private val _optionalTags = SimpleListProperty<String>()
    private val _optionalTagsEdit = SimpleStringProperty()

    init {
        _authorsEdit.bindBidirectional(_authors, AuthorListStringConverter)
        _publishedDateMinEdit.bindBidirectional(_publishedDateMin,
            LocalDateStringConverter(FormatStyle.SHORT))
        _publishedDateMaxEdit.bindBidirectional(_publishedDateMax,
            LocalDateStringConverter(FormatStyle.SHORT))
        _requiredTagsEdit.bindBidirectional(_requiredTags,
            TagListStringConverter)
        _optionalTagsEdit.bindBidirectional(_optionalTags,
            TagListStringConverter)
        _pageSize.set(10)
    }

    fun pageSizeProperty(): IntegerProperty = _pageSize

    var pageSize: Int
        get() = _pageSize.intValue()
        set(value) {
            _pageSize.set(value)
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

    fun publishedDateMinProperty(): ObjectProperty<LocalDate?> =
        _publishedDateMin

    var publishedDateMin: LocalDate?
        get() = _publishedDateMin.value
        set(value) {
            _publishedDateMin.value = value
        }

    fun publishedDateMinEditProperty(): StringProperty = _publishedDateMinEdit

    var publishedDateMinEdit: String?
        get() = _publishedDateMinEdit.value
        set(value) {
            _publishedDateMinEdit.value = value
        }

    fun publishedDateMaxProperty(): ObjectProperty<LocalDate?> =
        _publishedDateMax

    var publishedDateMax: LocalDate?
        get() = _publishedDateMax.value
        set(value) {
            _publishedDateMax.value = value
        }

    fun publishedDateMaxEditProperty(): StringProperty = _publishedDateMaxEdit

    var publishedDateMaxEdit: String?
        get() = _publishedDateMaxEdit.value
        set(value) {
            _publishedDateMaxEdit.value = value
        }

    fun requiredTagsProperty(): ListProperty<String> = _requiredTags

    var requiredTags: ObservableList<String>?
        get() = _requiredTags.value
        set(value) {
            _requiredTags.value = value
        }

    fun requiredTagsEditProperty(): StringProperty = _requiredTagsEdit

    var requiredTagsEdit: String?
        get() = _requiredTagsEdit.value
        set(value) {
            _requiredTagsEdit.value = value
        }

    fun optionalTagsProperty(): ListProperty<String> = _optionalTags

    var optionalTags: ObservableList<String>?
        get() = _optionalTags.value
        set(value) {
            _optionalTags.value = value
        }

    fun optionalTagsEditProperty(): StringProperty = _optionalTagsEdit

    var optionalTagsEdit: String?
        get() = _optionalTagsEdit.value
        set(value) {
            _optionalTagsEdit.value = value
        }

    fun validate(): Boolean {
        if (pageSize <= 0) {
            return false
        }

        publishedDateMin?.let { min ->
            publishedDateMax?.let { max ->
                if (min.isAfter(max)) {
                    return false
                }
            }
        }

        return true
    }

    fun toModel(): ArticleFindModel =
        ArticleFindModel(
            authors = authors?.toList() ?: emptyList(),
            publishedDateMin = publishedDateMin,
            publishedDateMax = publishedDateMax,
            requiredTags = requiredTags?.toList() ?: emptyList(),
            optionalTags = optionalTags?.toList() ?: emptyList())

    companion object {

        fun copy(source: FindForm) = FindForm().apply {
            pageSize = source.pageSize
            authors = source.authors
            publishedDateMin = source.publishedDateMin
            publishedDateMax = source.publishedDateMax
            requiredTags = source.requiredTags
            optionalTags = source.optionalTags
        }
    }
}