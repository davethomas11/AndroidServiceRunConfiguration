package com.daveanthonythomas.android.run.tasks;

import com.android.ddmlib.IDevice;
import com.android.tools.idea.run.ConsolePrinter;
import com.android.tools.idea.run.activity.StartActivityFlagsProvider;
import com.android.tools.idea.run.tasks.LaunchTask;
import com.android.tools.idea.run.tasks.LaunchTaskDurations;
import com.android.tools.idea.run.tasks.ShellCommandLauncher;
import com.android.tools.idea.run.util.LaunchStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SpecificServiceLaunchTask implements LaunchTask {

    private final String applicationId;
    private final String service;
    private final StartActivityFlagsProvider startActivityFlagsProvider;

    public SpecificServiceLaunchTask(@NotNull String applicationId,
                                     @NotNull String service,
                                     @NotNull StartActivityFlagsProvider startActivityFlagsProvider) {
        this.applicationId = applicationId;
        this.service = service;
        this.startActivityFlagsProvider = startActivityFlagsProvider;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Launching service";
    }

    @Override
    public int getDuration() {
        return LaunchTaskDurations.LAUNCH_ACTIVITY;
    }

    @Override
    public boolean perform(@NotNull IDevice iDevice,
                           @NotNull LaunchStatus launchStatus,
                           @NotNull ConsolePrinter consolePrinter) {
        final String activityPath = getLauncherServicePath(applicationId, service);
        String command = getStartServiceCommand(activityPath, startActivityFlagsProvider.getFlags(iDevice));

        // The timeout is quite large to accomodate ARM emulators.
        return ShellCommandLauncher.execute(command, iDevice, launchStatus, consolePrinter, 15, TimeUnit.SECONDS);
    }

    @NotNull
    private static String getStartServiceCommand(@NotNull String servicePath, @NotNull String extraFlags) {
        return "am startservice" +
                " -n \"" + servicePath + "\"" +
                (extraFlags.isEmpty() ? "" : " " + extraFlags);
    }

    @NotNull
    private static String getLauncherServicePath(@NotNull String packageName, @NotNull String serviceName) {
        return packageName + "/" + serviceName.replace("$", "\\$");
    }
}
