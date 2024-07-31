package org.openrewrite.maven;

import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.maven.tree.ProfileActivation;

import java.util.List;
import java.util.stream.Collectors;

public interface WithProfiles<P extends WithProfiles.Profile> {
    interface Profile {
        @Nullable
        String getId();

        @Nullable
        ProfileActivation getActivation();

        /**
         * Returns true if this profile was activated either by the supplied active profiles
         * or by activation property, <i>but not solely by activeByDefault</i>.
         */
        default boolean isActive(Iterable<String> userSpecifiedProfiles) {
            if (getId() != null) {
                for (String activeProfile : userSpecifiedProfiles) {
                    if (activeProfile.trim().equals(getId())) {
                        return true;
                    }
                }
            }
            return getActivation() != null && getActivation().isActive();
        }
    }

    List<P> listProfiles();

    default List<P> activeProfiles(final Iterable<String> userSpecifiedProfiles) {
        final List<P> profiles = listProfiles();

        final List<P> explicitActiveProfiles =
                profiles.stream()
                        .filter(p -> p.isActive(userSpecifiedProfiles))
                        .collect(Collectors.toList());

        // activeByDefault profiles should be active even if they don't exist
        // in userSpecifiedProfiles _unless_ a profile was activated by the
        // user or is activated by its activation value (except for 'activeByDefault')
        if (!explicitActiveProfiles.isEmpty()) {
            return explicitActiveProfiles;
        }

        return profiles.stream()
                .filter(p -> p.getActivation() != null && Boolean.TRUE.equals(p.getActivation().getActiveByDefault()))
                .collect(Collectors.toList());
    }
}
