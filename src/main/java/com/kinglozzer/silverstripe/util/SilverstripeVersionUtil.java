package com.kinglozzer.silverstripe.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.text.VersionComparatorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.psi.search.GlobalSearchScope.allScope;

public final class SilverstripeVersionUtil {
    public static final String SILVERSTRIPE_VERSION_4 = "4";
    public static final String SILVERSTRIPE_VERSION_5 = "5";
    public static final String SILVERSTRIPE_VERSION_6 = "6";

    @NotNull
    public static String getSilverstripeVersion(@NotNull Project project) {
        return CachedValuesManager.getManager(project).getCachedValue(project, () -> {
            final String resultSilverstripeVersion = computeSilverstripeVersion(project);
            return CachedValueProvider.Result.create(resultSilverstripeVersion, ProjectRootManager.getInstance(project));
        });
    }

    public static boolean isSilverstripe4OrMore(@NotNull Project project) {
        return VersionComparatorUtil.compare(getSilverstripeVersion(project), SILVERSTRIPE_VERSION_4) >= 0;
    }

    public static boolean isSilverstripe5OrMore(@NotNull Project project) {
        return VersionComparatorUtil.compare(getSilverstripeVersion(project), SILVERSTRIPE_VERSION_5) >= 0;
    }

    public static boolean isSilverstripe6OrMore(@NotNull Project project) {
        return VersionComparatorUtil.compare(getSilverstripeVersion(project), SILVERSTRIPE_VERSION_6) >= 0;
    }

    /**
     * Returns true if the detected Silverstripe version is >= minInclusive and < maxExclusive.
     * Useful for features that exist only in a specific version range (e.g. SS4–5 only).
     */
    public static boolean isSilverstripeVersionBetween(@NotNull Project project,
                                                        @NotNull String minInclusive,
                                                        @NotNull String maxExclusive) {
        String version = getSilverstripeVersion(project);
        return VersionComparatorUtil.compare(version, minInclusive) >= 0
            && VersionComparatorUtil.compare(version, maxExclusive) < 0;
    }

    @NotNull
    private static String computeSilverstripeVersion(@NotNull Project project) {
        // Preferred: read the major version from composer.json
        String versionFromComposer = detectVersionFromComposer(project);
        if (versionFromComposer != null) {
            return versionFromComposer;
        }

        // Fallback: presence of vendor/silverstripe/framework indicates SS4+
        Collection<VirtualFile> candidates = FilenameIndex.getVirtualFilesByName("framework", allScope(project));
        for (VirtualFile candidate : candidates) {
            if (!candidate.isDirectory()) {
                continue;
            }

            VirtualFile parent = candidate.getParent();
            if (parent != null && parent.isDirectory() && parent.getName().equals("silverstripe")) {
                parent = parent.getParent();
                if (parent != null && parent.isDirectory() && parent.getName().equals("vendor")) {
                    return "4";
                }
            }
        }

        return "3";
    }

    /**
     * Reads the major version of silverstripe/framework from the project's composer.json.
     * Returns null if no composer.json is found or it does not declare silverstripe/framework.
     */
    @Nullable
    private static String detectVersionFromComposer(@NotNull Project project) {
        Pattern pattern = Pattern.compile("\"silverstripe/framework\"\\s*:\\s*\"[^0-9]*([0-9]+)");

        Collection<VirtualFile> candidates = FilenameIndex.getVirtualFilesByName("composer.json", allScope(project));
        for (VirtualFile candidate : candidates) {
            // Skip composer.json files inside vendor/ — only inspect project-root ones
            if (candidate.getUrl().contains("/vendor/")) {
                continue;
            }

            try {
                String content = new String(candidate.contentsToByteArray(), StandardCharsets.UTF_8);
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            } catch (IOException e) {
                // Unreadable file — try the next candidate
            }
        }

        return null;
    }
}
