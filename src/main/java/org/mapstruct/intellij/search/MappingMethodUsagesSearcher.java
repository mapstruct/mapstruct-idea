/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.search.MethodTextOccurrenceProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchRequestCollector;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.util.MapstructUtil;

/**
 * Methods usages searcher for {@code source} and {@code target} values in {@code @Mapping} annotation.
 *
 * This is an adapted class from {@link com.intellij.psi.impl.search.MethodUsagesSearcher}.
 *
 * @author Filip Hrisafov
 * @see com.intellij.psi.impl.search.MethodUsagesSearcher
 */
public class MappingMethodUsagesSearcher
    extends QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters> {
    // TODO How can we improve this in order to do only the things that we need. I don't
    // know about needStrictSignatureSearch
    @Override
    public void processQuery(@NotNull final MethodReferencesSearch.SearchParameters p,
        @NotNull Processor<? super PsiReference> consumer) {
        final PsiMethod method = p.getMethod();
        // Instead of looking for the method name we need to look for the property name.
        // All the other things are taken over from the IntelliJ MethodUsagesSearcher
        final String[] propertyName = new String[1];
        final boolean[] needStrictSignatureSearch = new boolean[1];
        final boolean strictSignatureSearch = p.isStrictSignatureSearch();

        final PsiClass aClass = DumbService.getInstance( p.getProject() ).runReadActionInSmartMode( () -> {
            PsiClass aClass1 = method.getContainingClass();
            if ( aClass1 == null ) {
                return null;
            }
            propertyName[0] = MapstructUtil.getPropertyName( method );
            needStrictSignatureSearch[0] = strictSignatureSearch && ( aClass1 instanceof PsiAnonymousClass
                || aClass1.hasModifierProperty( PsiModifier.FINAL )
                || method.hasModifierProperty( PsiModifier.STATIC )
                || method.hasModifierProperty( PsiModifier.FINAL )
                || method.hasModifierProperty( PsiModifier.PRIVATE ) );
            return aClass1;
        } );
        if ( aClass == null ) {
            return;
        }

        final SearchRequestCollector collector = p.getOptimizer();

        final SearchScope searchScope = DumbService.getInstance( p.getProject() )
            .runReadActionInSmartMode( p::getEffectiveSearchScope );
        if ( searchScope == GlobalSearchScope.EMPTY_SCOPE ) {
            return;
        }

        if ( needStrictSignatureSearch[0] ) {
            ReferencesSearch.searchOptimized( method, searchScope, false, collector, consumer );
            return;
        }

        if ( StringUtil.isEmpty( propertyName[0] ) ) {
            return;
        }

        DumbService.getInstance( p.getProject() ).runReadActionInSmartMode( () -> {
            final PsiMethod[] methods =
                strictSignatureSearch ? new PsiMethod[] { method } : aClass.findMethodsByName( propertyName[0], false );

            short searchContext =
                UsageSearchContext.IN_CODE | UsageSearchContext.IN_COMMENTS | UsageSearchContext.IN_FOREIGN_LANGUAGES;
            for ( PsiMethod m : methods ) {
                collector.searchWord(
                    propertyName[0],
                    searchScope.intersectWith( m.getUseScope() ),
                    searchContext,
                    true,
                    m,
                    getTextOccurrenceProcessor( new PsiMethod[] { m }, aClass, strictSignatureSearch )
                );
            }

            return null;
        } );
    }

    protected MethodTextOccurrenceProcessor getTextOccurrenceProcessor(PsiMethod[] methods, PsiClass aClass,
        boolean strictSignatureSearch) {
        return new MethodTextOccurrenceProcessor( aClass, strictSignatureSearch, methods );
    }
}
