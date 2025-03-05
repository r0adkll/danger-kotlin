package com.r0adkll.danger.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.BrowseFolderDescriptor.Companion.withPathToTextConvertor
import com.intellij.openapi.ui.BrowseFolderDescriptor.Companion.withTextToPathConvertor
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.getCanonicalPath
import com.intellij.openapi.ui.getPresentablePath
import com.intellij.ui.dsl.builder.*
import com.r0adkll.danger.DangerBundle
import com.r0adkll.danger.services.danger
import kotlin.io.path.absolutePathString

fun Project.settings(): DangerSettings = service()

@Service(Service.Level.PROJECT)
@State(name = "DangerPluginSettings", storages = [Storage("danger-kotlin.xml")])
class DangerSettings : SimplePersistentStateComponent<DangerSettings.State>(State()) {

  var customDangerJarSourcePath: String?
    get() = state.customDangerJarSourcePath
    set(value) {
      state.customDangerJarSourcePath = value
    }

  class State : BaseState() {
    var customDangerJarSourcePath by string(null)
  }
}

class DangerSettingsConfigurable(private val project: Project) :
  BoundConfigurable(displayName = DangerBundle.message("settings.configurable.title")) {

  private val settings: DangerSettings
    get() = project.service()

  override fun createPanel(): DialogPanel {
    lateinit var panel: DialogPanel

    panel = panel {
      project.danger().dangerVersion?.let { dangerService ->
        row {
          label(DangerBundle.message("settings.configurable.version.title"))
          textField().text(project.danger().dangerVersion ?: "<unknown>").enabled(false)
        }

        row {
            label(DangerBundle.message("settings.configurable.location.title"))
            textField().text(project.danger().dangerLocation ?: "<unknown>").enabled(false)
          }
          .bottomGap(BottomGap.SMALL)
      }
      project.danger().dangerConfig?.let { loadedConfig ->
        row {
          label(DangerBundle.message("settings.configurable.loaded-jar.title"))
          textField()
            .text(loadedConfig.classPath.absolutePathString())
            .enabled(false)
            .align(AlignX.FILL)
        }
      }
      row {
        label(DangerBundle.message("settings.configurable.custom-jar.title"))
        cell(TextFieldWithBrowseButton())
          .applyToComponent {
            @Suppress("DEPRECATION", "removal")
            addBrowseFolderListener(
              null,
              null,
              null,
              FileChooserDescriptor(true, false, true, true, false, false)
                //              .withExtensionFilter("*.jar") // TODO 243+
                .withPathToTextConvertor(::getPresentablePath)
                .withTextToPathConvertor(::getCanonicalPath),
            )
          }
          .bindText(
            getter = { settings.customDangerJarSourcePath ?: "" },
            setter = { settings.customDangerJarSourcePath = it },
          )
          .align(AlignX.FILL)
      }
      row {
        button(DangerBundle.message("settings.configurable.customJar.action.clear")) {
            settings.customDangerJarSourcePath = null
            panel.reset()
            project.danger().reloadAsync()
          }
          .align(AlignX.RIGHT)
      }
    }

    return panel
  }

  override fun apply() {
    super.apply()
    project.danger().reloadAsync()
  }
}
