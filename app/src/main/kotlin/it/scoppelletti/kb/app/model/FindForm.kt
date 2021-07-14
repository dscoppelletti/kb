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

import it.scoppelletti.kb.app.adapter.TagListStringConverter
import it.scoppelletti.kb.domain.model.ArticleFindModel
import javafx.beans.property.*
import javafx.collections.ObservableList

class FindForm {
    private val _pageSize = SimpleIntegerProperty()
    private val _tags = SimpleListProperty<String>()
    private val _tagsEdit = SimpleStringProperty()

    init {
        _tagsEdit.bindBidirectional(_tags, TagListStringConverter)
    }

    fun pageSizeProperty(): IntegerProperty = _pageSize

    var pageSize: Int
        get() = _pageSize.intValue()
        set(value) {
            _pageSize.set(value)
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

    fun validate(): Boolean = (pageSize > 0)

    fun toModel(): ArticleFindModel =
        ArticleFindModel(tags = tags?.toList() ?: emptyList())

    companion object {

        fun copy(source: FindForm) = FindForm().apply {
            pageSize = source.pageSize
            tags = source.tags
        }
    }
}