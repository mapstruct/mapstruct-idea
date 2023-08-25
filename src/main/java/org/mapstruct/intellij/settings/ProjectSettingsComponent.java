/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.settings;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import org.mapstruct.intellij.MapStructBundle;

/**
 * @author Filip Hrisafov
 */
public class ProjectSettingsComponent {

    private final JPanel mainPanel;
    private final JBCheckBox preferSourceBeforeTargetInMapping;
    private final JBCheckBox ignoreWitherInMapping;

    public ProjectSettingsComponent() {
        this.preferSourceBeforeTargetInMapping = new JBCheckBox( MapStructBundle.message(
            "plugin.settings.quickFix.preferSourceBeforeTargetInMapping" ), false );
        this.ignoreWitherInMapping = new JBCheckBox( MapStructBundle.message(
            "plugin.settings.quickFix.ignoreWitherInMapping" ), false );
        JPanel quickFixProperties = new JPanel( new BorderLayout() );
        quickFixProperties.setBorder( IdeBorderFactory.createTitledBorder( MapStructBundle.message(
            "plugin.settings.quickFix.title" ), false ) );

        quickFixProperties.add( this.preferSourceBeforeTargetInMapping, BorderLayout.NORTH );
        quickFixProperties.add( this.ignoreWitherInMapping, BorderLayout.SOUTH );
        this.mainPanel = FormBuilder.createFormBuilder()
            .addComponent( quickFixProperties )
            .addComponentFillVertically( new JPanel(), 0 )
            .getPanel();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return preferSourceBeforeTargetInMapping;
    }

    public boolean getPreferSourceBeforeTargetInMapping() {
        return preferSourceBeforeTargetInMapping.isSelected();
    }

    public void setPreferSourceBeforeTargetInMapping(boolean newState) {
        preferSourceBeforeTargetInMapping.setSelected( newState );
    }

    public boolean getIgnoreWitherInMapping() {
        return ignoreWitherInMapping.isSelected();
    }

    public void setIgnoreWitherInMapping(boolean newState) {
        ignoreWitherInMapping.setSelected( newState );
    }

}
