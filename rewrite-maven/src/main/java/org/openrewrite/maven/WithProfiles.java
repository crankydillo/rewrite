package org.openrewrite.maven;

import java.util.List;
import java.util.stream.Collectors;

public interface WithProfiles<P extends WithProfiles.Profile> {
    interface Profile {
        String id();
        boolean isActive();
        boolean isActiveByDefault();
    }

    List<P> listProfiles();

    default List<P> activeProfiles(final Iterable<String> userSpecifiedProfiles) {
        final List<P> profiles = listProfiles();

        final List<P> explicitActiveProfiles =
                profiles.stream()
                        .filter(p -> isActivated(p, userSpecifiedProfiles))
                        .collect(Collectors.toList());

        // activeByDefault profiles should be active even if they don't exist
        // in userSpecifiedProfiles _unless_ a profile was activated by the
        // user or is activated by its activation value (except for 'activeByDefault')
        if (!explicitActiveProfiles.isEmpty()) {
            return explicitActiveProfiles;
        }

        return profiles.stream()
                .filter(p -> Boolean.TRUE.equals(p.isActiveByDefault()))
                .collect(Collectors.toList());
    }

    default boolean isActivated(final P profile, final Iterable<String> userSpecifiedProfiles) {
        if (profile.id() != null) {
            for (String activeProfile : userSpecifiedProfiles) {
                if (activeProfile.trim().equals(profile.id())) {
                    return true;
                }
            }
        }
        return profile.isActive();
    }

}
