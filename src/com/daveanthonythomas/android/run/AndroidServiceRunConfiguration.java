package com.daveanthonythomas.android.run;

import com.android.tools.idea.run.*;
import com.android.tools.idea.run.tasks.LaunchTask;
import com.android.tools.idea.run.util.LaunchStatus;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AndroidServiceRunConfiguration extends AndroidRunConfigurationBase {

    public AndroidServiceRunConfiguration(Project project, ConfigurationFactory factory) {
        super(project, factory, false);
    }

    @Override
    protected Pair<Boolean, String> supportsRunningLibraryProjects(@NotNull AndroidFacet androidFacet) {
        return Pair.create(Boolean.FALSE, AndroidBundle.message("android.cannot.run.library.project.error"));
    }

    @NotNull
    @Override
    protected List<ValidationError> checkConfiguration(@NotNull AndroidFacet androidFacet) {
        return null;
    }

    @NotNull
    @Override
    protected ApkProvider getApkProvider(@NotNull AndroidFacet androidFacet, @NotNull ApplicationIdProvider applicationIdProvider) {
        return null;
    }

    @NotNull
    @Override
    protected ConsoleProvider getConsoleProvider() {
        return (parent, handler, executor) -> {
            Project project = getConfigurationModule().getProject();
            TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
            ConsoleView console = builder.getConsole();
            console.attachToProcess(handler);
            return console;
        };
    }

    @Nullable
    @Override
    protected LaunchTask getApplicationLaunchTask(@NotNull ApplicationIdProvider applicationIdProvider, @NotNull AndroidFacet androidFacet, boolean b, @NotNull LaunchStatus launchStatus) {
        return null;
    }

    @Override
    protected boolean supportMultipleDevices() {
        return true;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return null;
    }
}
