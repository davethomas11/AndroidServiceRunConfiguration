package com.daveanthonythomas.android.run.editor;

import com.android.tools.idea.run.activity.ActivityLocatorUtils;
import com.android.tools.idea.run.editor.LaunchOptionConfigurable;
import com.android.tools.idea.run.editor.LaunchOptionConfigurableContext;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.ExecutionBundle;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.ProjectScope;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LanguageTextField;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SpecificServiceConfigurable implements LaunchOptionConfigurable<SpecificServiceLaunch.State> {
        private final Project myProject;
        private final LaunchOptionConfigurableContext myContext;
        private JPanel myPanel;
        private ComponentWithBrowseButton<EditorTextField> myActivityField;

        public SpecificServiceConfigurable(@NotNull final Project project, @NotNull final LaunchOptionConfigurableContext context) {
            myProject = project;
            myContext = context;
            myActivityField.addActionListener(e -> {
                if (project.isInitialized()) {
                    JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
                    PsiClass activityBaseClass = facade.findClass("android.app.Activity", ProjectScope.getAllScope(project));
                    if (activityBaseClass == null) {
                        Messages.showErrorDialog(project, AndroidBundle.message("cant.find.activity.class.error"), "Specific Activity Launcher");
                    } else {
                        Module module = context.getModule();
                        if (module == null) {
                            Messages.showErrorDialog(project, ExecutionBundle.message("module.not.specified.error.text"), "Specific Activity Launcher");
                        } else {
                            PsiClass initialSelection = facade.findClass(myActivityField.getChildComponent().getText(), module.getModuleWithDependenciesScope());
                            TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project)
                                    .createInheritanceClassChooser("Select Activity Class", module.getModuleWithDependenciesScope(), activityBaseClass, initialSelection, null);
                            chooser.showDialog();
                            PsiClass selClass = chooser.getSelected();
                            if (selClass != null) {
                                myActivityField.getChildComponent().setText(ActivityLocatorUtils.getQualifiedActivityName(selClass));
                            }

                        }
                    }
                }
            });
        }

        private void createUIComponents() {
            EditorTextField editorTextField = new LanguageTextField(PlainTextLanguage.INSTANCE, myProject, "") {
                protected EditorEx createEditor() {
                    EditorEx editor = super.createEditor();
                    PsiFile file = PsiDocumentManager.getInstance(myProject).getPsiFile(editor.getDocument());
                    if (file != null) {
                        DaemonCodeAnalyzer.getInstance(myProject).setHighlightingEnabled(file, false);
                    }

                    editor.putUserData(LaunchOptionConfigurableContext.KEY, myContext);
                    return editor;
                }
            };
            myActivityField = new ComponentWithBrowseButton(editorTextField, null);
        }

        @Nullable
        public JComponent createComponent() {
            return myPanel;
        }

        public void resetFrom(@NotNull SpecificServiceLaunch.State state) {
            myActivityField.getChildComponent().setText(StringUtil.notNullize(state.SERVICE_CLASS));
        }

        public void applyTo(@NotNull SpecificServiceLaunch.State state) {
            state.SERVICE_CLASS = StringUtil.notNullize(myActivityField.getChildComponent().getText());
        }

}
