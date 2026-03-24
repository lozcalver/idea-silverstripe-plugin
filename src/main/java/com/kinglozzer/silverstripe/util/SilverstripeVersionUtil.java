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
    public static final String SILVERSTRIPE_VERSION_3 = "3";
    public static final String SILVERSTRIPE_VERSION_4 = "4";
    public static final String SILVERSTRIPE_VERSION_5 = "5";
    public static final String SILVERSTRIPE_VERSION_6 = "6";

    private static final Pattern COMPOSER_LOCK_VERSION_PATTERN = Pattern.compile("\"version\":\\s*\"([0-9]+)");

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
        // Preferred: read the resolved version from composer.lock
        String versionFromLock = detectVersionFromComposerLock(project);
        if (versionFromLock != null) {
            return versionFromLock;
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
                    return SILVERSTRIPE_VERSION_4;
                }
            }
        }

        return SILVERSTRIPE_VERSION_3;
    }

    /**
     * Reads the resolved major version of silverstripe/framework from composer.lock.
     * The lock file contains exact installed versions (e.g. "5.2.3") rather than constraints,
     * so no constraint-parsing is needed. Returns null if no usable lock file is found.
     */
    @Nullable
    private static String detectVersionFromComposerLock(@NotNull Project project) {
        Collection<VirtualFile> candidates = FilenameIndex.getVirtualFilesByName("composer.lock", allScope(project));
        for (VirtualFile candidate : candidates) {
            if (candidate.getUrl().contains("/vendor/")) {
                continue;
            }

            try {
                String content = new String(candidate.contentsToByteArray(), StandardCharsets.UTF_8);

                // Find the exact package entry — closing quote prevents matching e.g. framework-cms
                int nameIndex = content.indexOf("\"name\": \"silverstripe/framework\"");
                if (nameIndex == -1) {
                    continue;
                }

                // "version" is the next field after "name" in composer.lock; 200 chars is ample
                String window = content.substring(nameIndex, Math.min(nameIndex + 200, content.length()));
                Matcher matcher = COMPOSER_LOCK_VERSION_PATTERN.matcher(window);
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
