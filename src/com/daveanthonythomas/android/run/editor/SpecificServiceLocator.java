package com.daveanthonythomas.android.run.editor;

import com.android.tools.idea.model.MergedManifest;
import com.android.tools.idea.run.activity.ActivityLocatorUtils;
import com.android.tools.idea.run.activity.DefaultActivityLocator;
import com.daveanthonythomas.android.run.ServiceRunBundle;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;
import org.w3c.dom.Element;

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
        PsiClass serviceClass = facade.findClass(AndroidUtils.SERVICE_CLASS_NAME, ProjectScope.getAllScope(project));
        if (serviceClass == null) {
            throw new Exception(ServiceRunBundle.message("cant.find.service.class.error"));
        }

        PsiClass c = JavaExecutionUtil.findMainClass(project, serviceName, GlobalSearchScope.projectScope(project));
        Element element = serviceDeclared(module, serviceName);
        if (c == null || !c.isInheritor(serviceClass, true)) {
            if (element == null) {
                throw new Exception(ServiceRunBundle.message("not.service.subclass.error", serviceName));
            }
        } else {
            // check whether service is declared in the manifest
            if(element == null) {
                throw new Exception(ServiceRunBundle.message("service.not.declared.in.manifest", c.getName()));
            }
        }

        DefaultActivityLocator.ActivityWrapper activity = DefaultActivityLocator.ActivityWrapper.get(element);
        Boolean exported = activity.getExported();

        // if the activity is not explicitly exported, and it doesn't have an intent filter, then it cannot be launched
        if (!Boolean.TRUE.equals(exported) && !activity.hasIntentFilter()) {
            throw new Exception(ServiceRunBundle.message("specific.service.not.launchable.error"));
        }
    }

    public Element serviceDeclared(Module module, String serviceName) {
        for (Element e : MergedManifest.get(module).getServices()) {
            if (serviceName.equals(ActivityLocatorUtils.getQualifiedName(e))) {
                return e;
            }
        }
        return null;
    }

    private static boolean doesPackageContainMavenProperty(AndroidFacet facet) {
        final Manifest manifest = facet.getManifest();

        if (manifest == null) {
            return false;
        }
        final String aPackage = manifest.getPackage().getStringValue();
        return aPackage != null && aPackage.contains("${");
    }
}
