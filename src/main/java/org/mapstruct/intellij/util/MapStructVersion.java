/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import java.util.Comparator;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * @author Filip Hrisafov
 */
public class MapStructVersion implements Comparable<MapStructVersion> {

    public static final MapStructVersion DEFAULT_VERSION = fromVersionString( "1.2.0.Final" );

    private static final MapStructVersion BUILDER_SUPPORTED = fromVersionString( "1.3.0.Beta1" );
    private static final MapStructVersion CONSTRUCTOR_SUPPORTED = fromVersionString( "1.4.0.Beta1" );

    private final int major;
    private final int minor;
    private final int patch;
    private final ReleaseState releaseState;
    private final int releaseStateVersion;

    @Override
    public int compareTo(@NotNull MapStructVersion other) {
        return Comparator
                .comparingInt( (MapStructVersion v) -> v.major )
                .thenComparingInt( v -> v.minor )
                .thenComparingInt( v -> v.patch )
                .thenComparing( v -> v.releaseState )
                .thenComparingInt( v -> v.releaseStateVersion )
                .compare( this, other );
    }

    public enum ReleaseState {
        ALPHA,
        BETA,
        RELEASE_CANDIDATE,
        FINAL
    }

    public MapStructVersion(int major, int minor, int patch, ReleaseState releaseState,
                            int releaseStateVersion) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.releaseState = releaseState;
        this.releaseStateVersion = releaseStateVersion;
    }

    public static MapStructVersion fromVersionString( String version ) {
        if ( version == null ) {
            return DEFAULT_VERSION;
        }
        try {
            String[] s = version.split( "\\." );
            int majorVersion = Integer.parseInt( s[0] );
            int minorVersion = Integer.parseInt( s[1] );
            int patch = Integer.parseInt( s[2] );
            ReleaseState releaseState = ReleaseState.FINAL;
            int releaseStateVersion = 0;
            if ( s.length > 3 ) {
                String releaseStateString = s[3];
                if ( releaseStateString.startsWith( "Alpha" ) ) {
                    releaseState = ReleaseState.ALPHA;
                    releaseStateVersion = Integer.parseInt( releaseStateString.substring( 5 ) );
                }
                else if ( releaseStateString.startsWith( "Beta" ) ) {
                    releaseState = ReleaseState.BETA;
                    releaseStateVersion = Integer.parseInt( releaseStateString.substring( 4 ) );
                }
                else if ( releaseStateString.startsWith( "CR" ) ) {
                    releaseState = ReleaseState.RELEASE_CANDIDATE;
                    releaseStateVersion = Integer.parseInt( releaseStateString.substring( 2 ) );
                }
            }
            return new MapStructVersion(majorVersion, minorVersion, patch, releaseState, releaseStateVersion);
        }
        catch ( RuntimeException e ) {
            return DEFAULT_VERSION;
        }
    }

    public boolean isBuilderSupported() {
        return compareTo( BUILDER_SUPPORTED ) >= 0;
    }

    public boolean isConstructorSupported() {
        return compareTo( CONSTRUCTOR_SUPPORTED ) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        MapStructVersion that = (MapStructVersion) o;
        return major == that.major && minor == that.minor && patch == that.patch &&
                releaseStateVersion == that.releaseStateVersion && releaseState == that.releaseState;
    }

    @Override
    public int hashCode() {
        return Objects.hash( major, minor, patch, releaseState, releaseStateVersion );
    }
}
