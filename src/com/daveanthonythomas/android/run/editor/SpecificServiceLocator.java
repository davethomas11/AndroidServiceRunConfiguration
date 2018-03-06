package com.daveanthonythomas.android.run.editor;

import com.android.tools.idea.model.MergedManifest;
import com.daveanthonythomas.android.run.ServiceRunBundle;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.android.util.AndroidUtils;

public class SpecificServiceLocator {

    private final AndroidFacet facet;
    private final String serviceName;

    public SpecificServiceLocator(AndroidFacet facet, String serviceName) {
        this.facet = facet;
        this.serviceName = serviceName;
    }

    public void validate() throws Exception {
        if (serviceName == null || serviceName.isEmpty()) {
            throw new Exception(ServiceRunBundle.message("service.class.not.specified.error"));
        }

        if (doesPackageContainMavenProperty(facet)) {
            return;
        }

        Module module = facet.getModule();
        Project project = module.getProject();
        final JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        PsiClass activityClass = facade.findClass(AndroidUtils.SERVICE_CLASS_NAME, ProjectScope.getAllScope(project));
        if (activityClass == null) {
            throw new Exception(ServiceRunBundle.message("cant.find.service.class.error"));
        }

        PsiClass c = JavaExecutionUtil.findMainClass(project, serviceName, GlobalSearchScope.projectScope(project));
        Element element;
        if (c == null || !c.isInheritor(activityClass, true)) {
            element = MergedManifest.get(module).findActivityAlias(serviceName);
            if (element == null) {
                throw new Exception(ServiceRunBundle.message("not.service.subclass.error", serviceName));
            }
        }
        else {
            // check whether activity is declared in the manifest
            element = MergedManifest.get(module).findActivity(ActivityLocatorUtils.getQualifiedActivityName(c));
            if (element == null) {
                throw new ActivityLocatorException(AndroidBundle.message("activity.not.declared.in.manifest", c.getName()));
            }
        }

        DefaultActivityLocator.ActivityWrapper activity = DefaultActivityLocator.ActivityWrapper.get(element);
        Boolean exported = activity.getExported();

        // if the activity is not explicitly exported, and it doesn't have an intent filter, then it cannot be launched
        if (!Boolean.TRUE.equals(exported) && !activity.hasIntentFilter()) {
            throw new ActivityLocatorException(AndroidBundle.message("specific.activity.not.launchable.error"));
        }
    }

    private static boolean doesPackageContainMavenProperty(@NotNull AndroidFacet facet) {
        final Manifest manifest = facet.getManifest();

        if (manifest == null) {
            return false;
        }
        final String aPackage = manifest.getPackage().getStringValue();
        return aPackage != null && aPackage.contains("${");
    }
}
