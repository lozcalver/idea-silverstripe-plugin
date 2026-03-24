package com.kinglozzer.silverstripe.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.kinglozzer.silverstripe.SilverstripeFileType;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.psi.search.GlobalSearchScope.allScope;

public class SilverstripeFileUtil {
    public static List<SilverstripePsiFile> findIncludeTemplate(Project project, String key) {
        List<SilverstripePsiFile> result = new ArrayList<>();
        if (key.equals("")) {
            return result;
        }

        List<SilverstripePsiFile> files = findValidTemplates(project);

        key = "/" + key;
        for (SilverstripePsiFile file : files) {
            String url = file.getVirtualFile().getUrl();
            String[] templatePathFragments = url.split("/templates", 2);
            if (templatePathFragments.length < 2) {
                continue;
            }

            String templatePath = templatePathFragments[1];
            // In SS4+, we have to make sure the entire path matches. In SS3, just the file name matching is enough
            // Special case - "Includes" directory at any level
            if (SilverstripeVersionUtil.isSilverstripe4OrMore(project)) {
                if (templatePath.equals(key)) {
                    result.add(file);
                } else {
                    // In SS4+, <% include Foo %> should still match "templates/Includes/Foo.ss" without including
                    // the full path - so we manually inject "Includes/" and check again
                    int lastSlash = key.lastIndexOf("/");
                    String includeKey = key.substring(0, lastSlash) + "/Includes/" + key.substring(lastSlash + 1);
                    if (templatePath.equals(includeKey)) {
                        result.add(file);
                    }
                }
            } else if (templatePath.endsWith(key)) {
                result.add(file);
            }
        }

        return result;
    }

    public static List<SilverstripePsiFile> findValidTemplates(Project project) {
        return CachedValuesManager.getManager(project).getCachedValue(project, () ->
            CachedValueProvider.Result.create(
                computeValidTemplates(project),
                PsiManager.getInstance(project).getModificationTracker()
            )
        );
    }

    private static List<SilverstripePsiFile> computeValidTemplates(Project project) {
        final List<SilverstripePsiFile> result = new ArrayList<>();
        final Collection<VirtualFile> files = FileTypeIndex.getFiles(SilverstripeFileType.INSTANCE, allScope(project));
        for (VirtualFile file : files) {
            if (file.getUrl().contains("/templates/")) {
                var psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile instanceof SilverstripePsiFile template) {
                    result.add(template);
                }
            }
        }

        return result;
    }
}
