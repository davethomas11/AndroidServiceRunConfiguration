//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.daveanthonythomas.android.run.editor;

import com.android.tools.idea.run.ValidationError;
import com.android.tools.idea.run.activity.SpecificActivityLocator;
import com.android.tools.idea.run.activity.StartActivityFlagsProvider;
import com.android.tools.idea.run.activity.ActivityLocator.ActivityLocatorException;
import com.android.tools.idea.run.editor.*;
import com.android.tools.idea.run.tasks.LaunchTask;
import com.daveanthonythomas.android.run.tasks.SpecificServiceLaunchTask;
import com.google.common.collect.ImmutableList;
import com.intellij.openapi.project.Project;
import java.util.List;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpecificServiceLaunch extends LaunchOption<SpecificServiceLaunch.State> {
    public static final SpecificServiceLaunch INSTANCE = new SpecificServiceLaunch();
    public static final String ID = "specific_service";

    @Override
    @NotNull
    public String getId() {
        return ID;
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return "Specified Activity";
    }

    @Override
    @NotNull
    public SpecificServiceLaunch.State createState() {
        return new SpecificServiceLaunch.State();
    }

    @Override
    @NotNull
    public LaunchOptionConfigurable<State> createConfigurable(@NotNull Project project,
                                                              @NotNull LaunchOptionConfigurableContext context) {
        return new SpecificServiceConfigurable(project, context);
    }

    public static final class State extends LaunchOptionState {
        public String SERVICE_CLASS = "";

        public State() {
        }

        @Nullable
        public LaunchTask getLaunchTask(@NotNull String applicationId,
                                        @NotNull AndroidFacet facet,
                                        @NotNull StartActivityFlagsProvider startActivityFlagsProvider,
                                        @NotNull ProfilerState profilerState) {
            return new SpecificServiceLaunchTask(applicationId, this.SERVICE_CLASS, startActivityFlagsProvider);
        }

        @NotNull
        public List<ValidationError> checkConfiguration(@NotNull AndroidFacet facet) {
            try {
                this.getActivityLocator(facet).validate();
                return ImmutableList.of();
            } catch (ActivityLocatorException var3) {
                return ImmutableList.of(ValidationError.warning(var3.getMessage()));
            }
        }

        @NotNull
        private SpecificActivityLocator getActivityLocator(@NotNull AndroidFacet facet) {
            return new SpecificActivityLocator(facet, this.SERVICE_CLASS);
        }
    }
}
