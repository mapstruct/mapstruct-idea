/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection.inheritance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import org.mapstruct.intellij.util.MapstructUtil;
import org.mapstruct.intellij.util.TypeUtils;

import static org.mapstruct.intellij.util.TargetUtils.getRelevantType;

/**
 * Mapping Method with additional state. Minimal psi-adapted version of
 * <code>org.mapstruct.ap.internal.model.source.SourceMethod</code>
 */
public class SourceMethod {

    PsiMethod method;
    boolean fullyInitialized;

    public SourceMethod(PsiMethod method) {
        this.method = method;
        this.fullyInitialized = false;
    }

    /**
     * psi-modified version of <code>org.mapstruct.ap.internal.model.source.PsiMethod#canInheritFrom</code>
     */
    public boolean canInheritFrom(PsiMethod candidate, PsiType targetType) {

        PsiType candidateTargetType = getRelevantType( candidate );

        return targetType != null
            && candidate.getBody() == null
            && candidateTargetType != null
            && candidateTargetType.isAssignableFrom( targetType )
            && allParametersAreAssignable( method.getParameterList(), candidate.getParameterList() );
    }

    /**
     * psi-modified version of <code>org.mapstruct.ap.internal.model.source.PsiMethod#inverses</code>
     */
    public boolean inverses(PsiMethod candidate, PsiType targetType) {

        PsiType mappingSourceType = TypeUtils.firstParameterPsiType( method );
        PsiType mappingTargetType = getRelevantType( method );

        PsiType candidateTargetType = getRelevantType( candidate );
        PsiParameter candidateSourceParameter = Arrays.stream( candidate.getParameterList().getParameters() )
            .findFirst()
            .orElse( null );

        return targetType != null
            && candidate.getBody() == null
            && candidate.getParameterList().getParametersCount() == 1
            && candidateTargetType != null
            && mappingSourceType != null
            && candidateTargetType.isAssignableFrom( mappingSourceType )
            && candidateSourceParameter != null
            && mappingTargetType != null
            && candidateSourceParameter.getType().isAssignableFrom( mappingTargetType );
    }

    private boolean allParametersAreAssignable(PsiParameterList inheritParameters,
                                               PsiParameterList candidateParameters) {

        if ( inheritParameters == null || candidateParameters == null || inheritParameters.isEmpty() ||
            candidateParameters.isEmpty() ) {
            return false;
        }

        List<PsiParameter> fromParams = Arrays.stream( inheritParameters.getParameters() )
            .filter( MapstructUtil::isValidSourceParameter )
            .collect( Collectors.toList() );

        List<PsiParameter> toParams = Arrays.stream( candidateParameters.getParameters() )
            .filter( MapstructUtil::isValidSourceParameter )
            .collect( Collectors.toList() );

        return allParametersAreAssignable( fromParams, toParams );
    }

    /**
     * psi-modified version of
     * <code>org.mapstruct.ap.internal.model.source.SourceMethod#allParametersAreAssignable</code>
     */
    private boolean allParametersAreAssignable(List<PsiParameter> fromParams, List<PsiParameter> toParams) {
        if ( fromParams.size() == toParams.size() ) {
            Set<PsiParameter> unaccountedToParams = new HashSet<>( toParams );

            for ( PsiParameter fromParam : fromParams ) {
                // each fromParam needs at least one match, and all toParam need to be accounted for at the end
                boolean hasMatch = false;
                for ( PsiParameter toParam : toParams ) {
                    if ( toParam.getType().isAssignableFrom( fromParam.getType() ) ) {
                        unaccountedToParams.remove( toParam );
                        hasMatch = true;
                    }
                }

                if ( !hasMatch ) {
                    return false;
                }
            }

            return unaccountedToParams.isEmpty();
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        SourceMethod that = (SourceMethod) o;
        return Objects.equals( method, that.method );
    }

    @Override
    public int hashCode() {
        return Objects.hash( method );
    }
}
