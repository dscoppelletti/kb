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

package it.scoppelletti.kb.app.inject

import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.kb.app.ArticleStageBuilder
import it.scoppelletti.kb.app.MainApp
import it.scoppelletti.kb.app.MainSceneBuilder
import it.scoppelletti.kb.app.i18n.AppMessages
import it.scoppelletti.kb.database.inject.KbDatabaseModule
import it.scoppelletti.kb.storage.inject.KbStorageModule
import javafx.stage.Stage
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [ KbDatabaseModule::class, KbStorageModule::class ])
interface KbAppComponent {

    @Named(MainApp.STAGE_PRIMARY)
    fun primaryStage(): Stage

    fun mainSceneBuilder(): MainSceneBuilder

    fun articleStageBuilder(): ArticleStageBuilder

    fun appMessages(): AppMessages

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance
                   @Named(MainApp.STAGE_PRIMARY) primaryStage: Stage
        ): KbAppComponent
    }
}