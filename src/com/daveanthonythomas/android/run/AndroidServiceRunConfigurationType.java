package com.daveanthonythomas.android.run;

import com.android.tools.idea.run.AndroidRunConfiguration;
import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.facet.ProjectFacetManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import icons.AndroidIcons;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AndroidServiceRunConfigurationType implements ConfigurationType {
    private final ConfigurationFactory myFactory = new AndroidServiceRunConfigurationFactory(this);

    @Nls
    @Override
    public String getDisplayName() {
        return ServiceRunBundle.message("android.service.run.configuration.type.name");
    }

    @Nls
    @Override
    public String getConfigurationTypeDescription() {
        return ServiceRunBundle.message("android.service.run.configuration.type.description");
    }

    @Override
    public Icon getIcon() {
        return AndroidIcons.AndroidModule;
    }

    @NotNull
    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{this.myFactory};
    }

    public ConfigurationFactory getFactory() {
        return this.myFactory;
    }

    public static class AndroidServiceRunConfigurationFactory extends ConfigurationFactory {
        AndroidServiceRunConfigurationFactory(@NotNull ConfigurationType type) {
            super(type);
        }

        @NotNull
        public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new AndroidServiceRunConfiguration(project, this);
        }

        public boolean canConfigurationBeSingleton() {
            return false;
        }

        public boolean isApplicable(@NotNull Project project) {
            return ProjectFacetManager.getInstance(project).hasFacets(AndroidFacet.ID);
        }

        public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
            if (CompileStepBeforeRun.ID.equals(providerID)) {
                task.setEnabled(false);
            }
        }
    }
}
