/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.settings;

import javax.swing.JComponent;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.mapstruct.intellij.MapStructBundle;

import static org.mapstruct.intellij.settings.ProjectSettings.isIgnoreWitherInMapping;
import static org.mapstruct.intellij.settings.ProjectSettings.isPreferSourceBeforeTargetInMapping;

/**
 * @author Filip Hrisafov
 */
public class ProjectSettingsPage implements Configurable {

    private ProjectSettingsComponent settingsComponent;

    private final Project myProject;

    public ProjectSettingsPage(Project project) {
        myProject = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return MapStructBundle.message( "plugin.settings.title" );
    }

    @Override
    public JComponent createComponent() {
        settingsComponent = new ProjectSettingsComponent();
        return settingsComponent.getPanel();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return settingsComponent.getPreferredFocusedComponent();
    }

    @Override
    public boolean isModified() {
        return settingsComponent.getPreferSourceBeforeTargetInMapping() !=
            isPreferSourceBeforeTargetInMapping( myProject ) ||
          settingsComponent.getIgnoreWitherInMapping() != isIgnoreWitherInMapping(myProject);
    }

    @Override
    public void apply() {
        ProjectSettings.setPreferSourceBeforeTargetInMapping(
            myProject,
            settingsComponent.getPreferSourceBeforeTargetInMapping()
        );
        ProjectSettings.setIgnoreWitherInMapping(myProject, settingsComponent.getIgnoreWitherInMapping());
    }

    @Override
    public void reset() {
        settingsComponent.setPreferSourceBeforeTargetInMapping( isPreferSourceBeforeTargetInMapping( myProject ) );
        settingsComponent.setIgnoreWitherInMapping( isIgnoreWitherInMapping( myProject ) );
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }
}
