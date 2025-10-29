package com.r0adkll.danger.util;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.github.util.GHCompatibilityUtil;

/**
 * GHCompatibilityUtil was refactored to Kotlin from Java in ver. 251 --> 252.
 * So this breaks kotlin -> kotlin calls when building against 252 and running in 251
 * TODO: Remove when minBuild is 252+
 */
public class GHCompatibilityUtilHelper {

    @Nullable
    public static String getOrRequestToken(GithubAccount acct, Project project) {
        return GHCompatibilityUtil.getOrRequestToken(acct, project);
    }
}
