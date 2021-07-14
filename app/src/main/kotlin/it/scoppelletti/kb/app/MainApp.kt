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

import it.scoppelletti.kb.app.inject.DaggerKbAppComponent
import it.scoppelletti.kb.app.inject.KbAppComponent
import javafx.application.Application
import javafx.stage.Stage

class MainApp : Application() {
    private lateinit var appComponent: KbAppComponent

    override fun start(primaryStage: Stage) {
        appComponent = DaggerKbAppComponent.factory().create(primaryStage)

        primaryStage.apply {
            title = appComponent.appMessages().appName()
            scene = appComponent.mainSceneBuilder().build()
            show()
        }
    }

    companion object {
        const val STAGE_PRIMARY = "primaryStage"

        @JvmStatic
        fun main(args: Array<String>) {
            launch(MainApp::class.java, *args)
        }
    }
}