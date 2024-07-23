/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.module.LanguageLevelUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.Strings;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationParamListImpl;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.ReportingPolicy;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.codeInsight.intention.AddAnnotationPsiFix.addPhysicalAnnotationTo;
import static com.intellij.codeInsight.intention.AddAnnotationPsiFix.removePhysicalAnnotations;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPING_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.VALUE_MAPPING_ANNOTATION_FQN;

/**
 * Utils for working with mapstruct annotation.
 *
 * @author Filip Hrisafov
 */
public class MapstructAnnotationUtils {

    private static final String UNMAPPED_TARGET_POLICY = "unmappedTargetPolicy";

    private MapstructAnnotationUtils() {
    }

    /**
     * This method adds the {@code mappingAnnotation} to the given {@code mappingMethod}. It takes into
     * consideration, the current mappings, language level and whether the {@link org.mapstruct.Mapping} repeatable
     * annotation can be used.
     *
     * @param project the project
     * @param mappingMethod the method to which the annotation needs to be added
     * @param mappingAnnotation the {@link org.mapstruct.Mapping} annotation
     */
    public static void addMappingAnnotation(@NotNull Project project,
                                            @NotNull PsiMethod mappingMethod,
                                            @NotNull PsiAnnotation mappingAnnotation,
                                            boolean moveCaretToEmptySourceAttribute) {
        Pair<PsiAnnotation, Optional<PsiAnnotation>> mappingsPair = findOrCreateMappingsAnnotation(
            project,
            mappingMethod
        );
        final PsiFile containingFile = mappingMethod.getContainingFile();

        PsiAnnotation containerAnnotation = mappingsPair.getFirst();
        PsiAnnotation newAnnotation = createNewAnnotation(
            project,
            mappingMethod,
            containerAnnotation,
            mappingAnnotation
        );
        if ( newAnnotation != null ) {
            if ( containerAnnotation != null && containerAnnotation.isPhysical() ) {
                runWriteCommandAction(
                    project,
                    () -> {
                        PsiElement replaced = containerAnnotation.replace( newAnnotation );

                        if ( moveCaretToEmptySourceAttribute ) {
                            moveCaretToEmptySourceAttribute( project, replaced );
                        }
                    },
                    containingFile
                );
            }
            else {
                String fqn = containerAnnotation != null ? MapstructUtil.MAPPINGS_ANNOTATION_FQN :
                    MapstructUtil.MAPPING_ANNOTATION_FQN;
                PsiNameValuePair[] attributes = newAnnotation.getParameterList().getAttributes();
                Optional<String> annotationToRemove = mappingsPair.getSecond()
                    .map( PsiAnnotation::getQualifiedName );

                runWriteCommandAction(
                    project, () -> {
                        // If there was a mapping annotation previously we need to remove it (it is already included
                        // in the new attributes
                        annotationToRemove
                            .ifPresent( qualifiedName -> removePhysicalAnnotations(
                                mappingMethod,
                                qualifiedName
                            ) );

                        PsiAnnotation inserted = addPhysicalAnnotationTo(
                            fqn,
                            attributes,
                            mappingMethod.getModifierList()
                        );
                        JavaCodeStyleManager.getInstance( project ).shortenClassReferences( inserted );

                        if ( moveCaretToEmptySourceAttribute ) {
                            moveCaretToEmptySourceAttribute( project, inserted );
                        }

                    }, containingFile );

                UndoUtil.markPsiFileForUndo( containingFile );
            }
        }
    }

    private static void moveCaretToEmptySourceAttribute(@NotNull Project project, PsiElement element) {

        FileEditor selectedEditor = FileEditorManager.getInstance( project ).getSelectedEditor();
        if ( selectedEditor instanceof TextEditor textEditor ) {
            Editor editor = textEditor.getEditor();

            TextRange textRange = element.getTextRange();
            String textOfElement = String.valueOf( editor.getDocument()
                .getCharsSequence()
                .subSequence( textRange.getStartOffset(), textRange.getEndOffset() ) );
            int indexOfEmptySourceAttribute = Strings.indexOf( textOfElement, "\"\"" ) + 1;

            editor.getCaretModel().moveToOffset( textRange.getStartOffset() + indexOfEmptySourceAttribute );
            editor.getScrollingModel().scrollToCaret( ScrollType.MAKE_VISIBLE );
        }

    }

    /**
     * This methods looks for the {@link org.mapstruct.Mappings} annotation on the given {@code mappingMethod},
     * if the annotation was not found and the {@link org.mapstruct.Mapping} repeatable annotation cannot be used
     * (Language level is lower than JDK 1.8 and/or mapstruct jdk8 is not present) it creates a dummy
     * {@link org.mapstruct.Mappings} annotation.
     * <p>
     * In case the method was already annotated with {@link org.mapstruct.Mapping}, it returns a dummy
     * {@link org.mapstruct.Mappings} annotation that contains the already attached {@link org.mapstruct.Mapping} and
     * returns the annotation that needs to be removed from the method as a second value of the {@link Pair}
     *
     * @param project the project
     * @param mappingMethod the method that needs to be checked
     *
     * @return see the description
     */
    private static Pair<PsiAnnotation, Optional<PsiAnnotation>> findOrCreateMappingsAnnotation(
        @NotNull Project project,
        @NotNull PsiMethod mappingMethod) {
        PsiAnnotation mappingsAnnotation = AnnotationUtil.findAnnotation(
            mappingMethod,
            MapstructUtil.MAPPINGS_ANNOTATION_FQN
        );
        if ( mappingsAnnotation != null ) {
            return Pair.create( mappingsAnnotation, Optional.empty() );
        }

        final PsiFile containingFile = mappingMethod.getContainingFile();
        if ( !canUseRepeatableMapping( mappingMethod ) ) {
            PsiAnnotation oldMappingAnnotation = AnnotationUtil.findAnnotation(
                mappingMethod,
                MapstructUtil.MAPPING_ANNOTATION_FQN
            );
            String otherMappings = oldMappingAnnotation == null ? "" : "\n" + oldMappingAnnotation.getText();

            mappingsAnnotation = JavaPsiFacade.getElementFactory( project )
                .createAnnotationFromText(
                    "@" + MapstructUtil.MAPPINGS_ANNOTATION_FQN + "({" + otherMappings + "\n})",
                    containingFile
                );
            return Pair.create( mappingsAnnotation, Optional.ofNullable( oldMappingAnnotation ) );
        }
        return Pair.create( null, Optional.empty() );
    }

    /**
     * Run default {@link WriteCommandAction}.
     *
     * @param project the project in which to run the action
     * @param runnable the runnable to be executed
     * @param containingFile the file in which to run
     */
    private static void runWriteCommandAction(Project project, Runnable runnable, PsiFile containingFile) {
        WriteCommandAction.runWriteCommandAction( project, null, null, runnable, containingFile );
    }

    /**
     * Create a new annotation that can be added to a method. This method takes into consideration the different
     * possibilities of having a array based repeatable annotation declaration.
     *
     * @param project the project
     * @param container the container for the annotation
     * @param containerAnnotation the container annotation for {@code mappingAnnotation}
     * @param mappingAnnotation the single mapping annotation that needs to be taken into consideration when creating
     *
     * @return the annotation that should be added to the mapping method
     */
    private static PsiAnnotation createNewAnnotation(@NotNull Project project,
        PsiElement container,
        PsiAnnotation containerAnnotation,
        @NotNull PsiAnnotation mappingAnnotation) {
        if ( containerAnnotation == null ) {
            return mappingAnnotation;
        }
        if ( !containerAnnotation.getText().contains( "{" ) ) {
            //The container annotation contains a single value not declared as array
            final PsiNameValuePair[] attributes = containerAnnotation.getParameterList().getAttributes();
            if ( attributes.length == 1 ) {
                final String currentMappings = attributes[0].getText();
                return JavaPsiFacade.getInstance( project ).getElementFactory().createAnnotationFromText(
                    "@" + MapstructUtil.MAPPINGS_ANNOTATION_FQN + "({\n" + currentMappings + ",\n " +
                        mappingAnnotation.getText() + "\n})", container );

            }
        }
        else {
            final int curlyBraceIndex = containerAnnotation.getText().lastIndexOf( '}' );
            if ( curlyBraceIndex > 0 ) {
                final String textBeforeCurlyBrace = containerAnnotation.getText().substring( 0, curlyBraceIndex );
                int braceIndex = textBeforeCurlyBrace.lastIndexOf( ')' );
                final String textToPreserve =
                    braceIndex < 0 ? textBeforeCurlyBrace : textBeforeCurlyBrace.substring( 0, braceIndex ) + "),\n";
                return JavaPsiFacade.getInstance( project ).getElementFactory().createAnnotationFromText(
                    textToPreserve + " " + mappingAnnotation.getText() + "\n})", container );
            }
            else {
                throw new IncorrectOperationException( containerAnnotation.getText() );
            }
        }
        return null;
    }

    /**
     * Checks if the {@link java.lang.annotation.Repeatable} {@link org.mapstruct.Mapping} annotation can be used.
     * The annotation can be used when the following is satisfied:
     * <ul>
     * <li>The {@link LanguageLevel} of the module is at least {@link LanguageLevel#JDK_1_8}</li>
     * <li>Mapstruct jdk 8 is present in the module</li>
     * </ul>
     *
     * @param psiElement element from the module
     *
     * @return {@code true} if the {@link java.lang.annotation.Repeatable} {@link org.mapstruct.Mapping} annotation
     * can be used, {@code false} otherwise
     */
    private static boolean canUseRepeatableMapping(PsiElement psiElement) {
        Module module = ModuleUtilCore.findModuleForPsiElement( psiElement );
        return module != null
            && LanguageLevelUtil.getEffectiveLanguageLevel( module ).isAtLeast( LanguageLevel.JDK_1_8 )
            && MapstructUtil.isMapStructJdk8Present( module );
    }

    public static Stream<PsiAnnotation> findAllDefinedMappingAnnotations(@NotNull PsiModifierListOwner owner,
                                                                         MapStructVersion mapStructVersion) {

        // Meta annotations support was added when constructor support was added
        boolean includeMetaAnnotations = mapStructVersion.isConstructorSupported();

        return findAllDefinedMappingAnnotations( owner, includeMetaAnnotations );
    }

    @NotNull
    private static Stream<PsiAnnotation> findAllDefinedMappingAnnotations(@NotNull PsiModifierListOwner owner,
                                                                          boolean includeMetaAnnotations) {
        //TODO cache
        PsiAnnotation mappings = findAnnotation( owner, true, MapstructUtil.MAPPINGS_ANNOTATION_FQN );
        Stream<PsiAnnotation> mappingsAnnotations = extractMappingAnnotationsFromMappings( mappings );
        Stream<PsiAnnotation> mappingAnnotations = findMappingAnnotations( owner, includeMetaAnnotations );

        return Stream.concat( mappingAnnotations, mappingsAnnotations );
    }

    @NotNull
    public static Stream<PsiAnnotation> extractMappingAnnotationsFromMappings(@Nullable PsiAnnotation mappings) {
        if (mappings == null) {
            return Stream.empty();
        }
        //TODO maybe there is a better way to do this, but currently I don't have that much knowledge
        PsiAnnotationMemberValue mappingsValue = mappings.findDeclaredAttributeValue( null );
        if ( mappingsValue instanceof PsiArrayInitializerMemberValue mappingsArrayInitializerMemberValue) {
            return Stream.of( mappingsArrayInitializerMemberValue
                            .getInitializers() )
                    .filter( MapstructAnnotationUtils::isMappingPsiAnnotation )
                    .map( PsiAnnotation.class::cast );
        }
        else if ( mappingsValue instanceof PsiAnnotation mappingsAnnotation ) {
            return Stream.of( mappingsAnnotation );
        }
        return Stream.empty();
    }

    private static Stream<PsiAnnotation> findMappingAnnotations(@NotNull PsiModifierListOwner method,
                                                                boolean includeMetaAnnotations) {

        if ( includeMetaAnnotations ) {
            // do not use MetaAnnotationUtil#findMetaAnnotations since it only finds the first @Mapping annotation
            return findDirectAndMetaAnnotations( method, new HashSet<>() ).stream();
        }

        return Stream.of( method.getModifierList() )
            .filter( Objects::nonNull )
            .flatMap( psiModifierList -> Arrays.stream( psiModifierList.getAnnotations() ) )
            .filter( MapstructAnnotationUtils::isMappingAnnotation );
    }

    @NotNull
    private static Set<PsiAnnotation> findDirectAndMetaAnnotations(@NotNull PsiModifierListOwner owner,
                                                                   Set<? super PsiClass> visited) {

        Set<PsiAnnotation> result = new HashSet<>();

        // to avoid infinite loops, do not include meta annotations at this point
        findAllDefinedMappingAnnotations( owner, false ).forEach( result::add );

        List<PsiClass> annotationClasses = getResolvedClassesInAnnotationsList( owner );

        for ( PsiClass annotationClass : annotationClasses ) {
            if ( visited.add( annotationClass ) ) {
                result.addAll( findDirectAndMetaAnnotations( annotationClass, visited ) );
            }
        }

        return result;
    }

    /**
     * copy of private method <code>MetaAnnotationUtil#getResolvedClassesInAnnotationsList(PsiModifierListOwner)</code>
     */
    private static List<PsiClass> getResolvedClassesInAnnotationsList(PsiModifierListOwner owner) {
        PsiModifierList modifierList = owner.getModifierList();
        if ( modifierList != null ) {
            return ContainerUtil.mapNotNull(
                modifierList.getApplicableAnnotations(),
                PsiAnnotation::resolveAnnotationType
            );
        }
        return Collections.emptyList();
    }

    public static Stream<PsiAnnotation> findAllDefinedValueMappingAnnotations(@NotNull PsiMethod method) {
        Stream<PsiAnnotation> valueMappingsAnnotations = Stream.empty();
        PsiAnnotation valueMappings = findAnnotation( method, true, MapstructUtil.VALUE_MAPPINGS_ANNOTATION_FQN );
        if ( valueMappings != null ) {
            PsiAnnotationMemberValue mappingsValue = valueMappings.findDeclaredAttributeValue( null );
            if ( mappingsValue instanceof PsiArrayInitializerMemberValue mappingsArrayInitializerMemberValue ) {
                valueMappingsAnnotations = Stream.of( mappingsArrayInitializerMemberValue.getInitializers() )
                    .filter( MapstructAnnotationUtils::isValueMappingPsiAnnotation )
                    .map( PsiAnnotation.class::cast );
            }
            else if ( mappingsValue instanceof PsiAnnotation mappingsAnnotation ) {
                valueMappingsAnnotations = Stream.of( mappingsAnnotation );
            }
        }

        Stream<PsiAnnotation> valueMappingAnnotations = findValueMappingAnnotations( method );

        return Stream.concat( valueMappingAnnotations, valueMappingsAnnotations );
    }

    private static Stream<PsiAnnotation> findValueMappingAnnotations(@NotNull PsiMethod method) {
        return Stream.of( method.getModifierList().getAnnotations() )
            .filter( MapstructAnnotationUtils::isValueMappingAnnotation );
    }

    /**
     * @param memberValue that needs to be checked
     *
     * @return {@code true} if the {@code memberValue} is the {@link org.mapstruct.Mapping} {@link PsiAnnotation},
     * {@code false} otherwise
     */
    private static boolean isMappingPsiAnnotation(PsiAnnotationMemberValue memberValue) {
        return memberValue instanceof PsiAnnotation
            && isMappingAnnotation( (PsiAnnotation) memberValue );
    }

    /**
     * @param memberValue that needs to be checked
     *
     * @return {@code true} if the {@code memberValue} is the {@link org.mapstruct.ValueMapping} {@link PsiAnnotation},
     * {@code false} otherwise
     */
    private static boolean isValueMappingPsiAnnotation(PsiAnnotationMemberValue memberValue) {
        return memberValue instanceof PsiAnnotation
            && isValueMappingAnnotation( (PsiAnnotation) memberValue );
    }

    /**
     * @param psiAnnotation that needs to be checked
     *
     * @return {@code true} if the {@code psiAnnotation} is the {@link org.mapstruct.ValueMapping} annotation,
     * {@code false} otherwise
     */
    private static boolean isValueMappingAnnotation(PsiAnnotation psiAnnotation) {
        return VALUE_MAPPING_ANNOTATION_FQN.equals( psiAnnotation.getQualifiedName() );
    }

    /**
     * @param psiAnnotation that needs to be checked
     *
     * @return {@code true} if the {@code psiAnnotation} is the {@link org.mapstruct.Mapping} annotation, {@code
     * false} otherwise
     */
    private static boolean isMappingAnnotation(PsiAnnotation psiAnnotation) {
        return MAPPING_ANNOTATION_FQN.equals( psiAnnotation.getQualifiedName() );
    }

    /**
     * Find the mapper config reference class or interface defined in the {@code mapperAnnotation}
     *
     * @param mapperAnnotation the mapper annotation in which the mapper config is defined
     *
     * @return the class / interface that is defined in the mapper config,
     * or {@code null} if there isn't anything defined
     */
    @Nullable
    public static PsiModifierListOwner findMapperConfigReference(@NotNull PsiAnnotation mapperAnnotation) {
        PsiAnnotationMemberValue configValue = mapperAnnotation.findDeclaredAttributeValue( "config" );
        if ( !( configValue instanceof PsiClassObjectAccessExpression configClassObjectAccessExpression ) ) {
            return null;
        }

        PsiJavaCodeReferenceElement referenceElement = configClassObjectAccessExpression.getOperand()
            .getInnermostComponentReferenceElement();
        if ( referenceElement == null ) {
            return null;
        }

        PsiElement resolvedElement = referenceElement.resolve();
        if ( !( resolvedElement instanceof PsiModifierListOwner psiModifierListOwner ) ) {
            return null;
        }

        return psiModifierListOwner;
    }

    public static Optional<PsiClass> findMapperConfigClass(@NotNull PsiAnnotation mapperAnnotation) {

        PsiModifierListOwner mapperConfigReference = findMapperConfigReference( mapperAnnotation );

        if ( !( mapperConfigReference instanceof PsiClass mapperPsiClass ) ) {
            return Optional.empty();
        }

        return Optional.of( mapperPsiClass );
    }

    /**
     * Find the other mapper types used by the class or interface defined in the {@code mapperAnnotation}
     *
     * @param mapperAnnotation the mapper annotation in which the mapper config is defined
     * @return the classes / interfaces that are defined with the {@code uses} attribute of the current
     * {@code mapperAnnotation} or referenced @MappingConfig, or and empty stream if there isn't anything defined
     */
    public static Stream<PsiClass> findReferencedMapperClasses(PsiAnnotation mapperAnnotation) {

        Stream<PsiClass> localUsesReferences = findReferencedMappers( mapperAnnotation );

        Stream<PsiClass> mapperConfigUsesReferences = findReferencedMappersOfMapperConfig( mapperAnnotation );

        return Stream.concat( localUsesReferences, mapperConfigUsesReferences );
    }

    @NotNull
    private static Stream<PsiClass> findReferencedMappers(@NotNull PsiAnnotation mapperAnnotation) {
        PsiAnnotationMemberValue usesValue = mapperAnnotation.findDeclaredAttributeValue( "uses" );

        Stream<PsiClassObjectAccessExpression> usesExpressions = Stream.empty();
        if ( usesValue instanceof PsiArrayInitializerMemberValue psiArrayInitializerMemberValue ) {
            usesExpressions = Stream.of( psiArrayInitializerMemberValue.getInitializers() )
                .filter( PsiClassObjectAccessExpression.class::isInstance )
                .map( PsiClassObjectAccessExpression.class::cast );
        }
        else if ( usesValue instanceof PsiClassObjectAccessExpression usedPsiClassObjectAccessExpression ) {
            usesExpressions = Stream.of( usedPsiClassObjectAccessExpression );
        }

        return usesExpressions
            .map( usesExpression -> usesExpression.getOperand().getInnermostComponentReferenceElement() )
            .filter( Objects::nonNull )
            .map( PsiReference::resolve )
            .filter( PsiClass.class::isInstance )
            .map( PsiClass.class::cast );
    }

    private static Stream<PsiClass> findReferencedMappersOfMapperConfig(PsiAnnotation mapperAnnotation) {

        PsiModifierListOwner mapperConfigReference = findMapperConfigReference( mapperAnnotation );

        if ( mapperConfigReference == null ) {
            return Stream.empty();
        }

        PsiAnnotation mapperConfigAnnotation = findAnnotation(
            mapperConfigReference,
            true,
            MapstructUtil.MAPPER_CONFIG_ANNOTATION_FQN
        );

        if ( mapperConfigAnnotation == null ) {
            return Stream.empty();
        }

        return findReferencedMappers( mapperConfigAnnotation );
    }

    @NotNull
    public static ReportingPolicy getUnmappedTargetPolicy(@NotNull PsiMethod method) {
        PsiAnnotation beanMapping = method.getAnnotation( MapstructUtil.BEAN_MAPPING_FQN );
        if ( beanMapping != null ) {
            PsiAnnotationMemberValue beanAnnotationOverwrite =
                beanMapping.findDeclaredAttributeValue( UNMAPPED_TARGET_POLICY );
            if ( beanAnnotationOverwrite != null ) {
                return getUnmappedTargetPolicyPolicyFromAnnotation( beanAnnotationOverwrite );
            }
        }
        PsiClass containingClass = method.getContainingClass();
        if ( containingClass == null ) {
            return ReportingPolicy.WARN;
        }
        return getUnmappedTargetPolicyFromClass( containingClass );
    }

    @NotNull
    private static ReportingPolicy getUnmappedTargetPolicyFromClass(@NotNull PsiClass containingClass) {
        PsiAnnotation mapperAnnotation = containingClass.getAnnotation( MapstructUtil.MAPPER_ANNOTATION_FQN );
        if ( mapperAnnotation == null ) {
            return ReportingPolicy.WARN;
        }

        PsiAnnotationMemberValue classAnnotationOverwrite = mapperAnnotation.findDeclaredAttributeValue(
            UNMAPPED_TARGET_POLICY );
        if ( classAnnotationOverwrite != null ) {
            return getUnmappedTargetPolicyPolicyFromAnnotation( classAnnotationOverwrite );
        }
        return getUnmappedTargetPolicyFromMapperConfig( mapperAnnotation );
    }

    @NotNull
    private static ReportingPolicy getUnmappedTargetPolicyFromMapperConfig(@NotNull PsiAnnotation mapperAnnotation) {
        PsiAnnotationMemberValue configValue = findConfigValueFromMapperConfig( mapperAnnotation,
                UNMAPPED_TARGET_POLICY );
        if (configValue == null) {
            return ReportingPolicy.WARN;
        }
        return getUnmappedTargetPolicyPolicyFromAnnotation( configValue );
    }

    /**
     * finds a property from a referenced mapper config class
     * @param mapperAnnotation the @Mapper annotation from the current class
     * @param name the name of the property tp find
     * @return null if no mapper config class is used or no property with name is found.
     */
    @Nullable
    public static PsiAnnotationMemberValue findConfigValueFromMapperConfig(@NotNull PsiAnnotation mapperAnnotation,
                                                                           @NotNull String name) {
        PsiModifierListOwner mapperConfigReference = findMapperConfigReference( mapperAnnotation );
        if ( mapperConfigReference == null ) {
            return null;
        }
        PsiAnnotation mapperConfigAnnotation = mapperConfigReference.getAnnotation(
            MapstructUtil.MAPPER_CONFIG_ANNOTATION_FQN );

        if ( mapperConfigAnnotation == null ) {
            return null;
        }
        return mapperConfigAnnotation.findDeclaredAttributeValue( name );
    }


    /**
     * Converts the configValue to ReportingPolicy enum. If no matching ReportingPolicy found,
     * returns ReportingPolicy.WARN.
     *
     * @param configValue The annotation value to convert to ReportingPolicy enum
     * @return the mapped ReportingPolicy enum
     */
    @NotNull
    private static ReportingPolicy getUnmappedTargetPolicyPolicyFromAnnotation(
        @NotNull PsiAnnotationMemberValue configValue) {
        return switch (configValue.getText()) {
            case "IGNORE", "ReportingPolicy.IGNORE" -> ReportingPolicy.IGNORE;
            case "ERROR", "ReportingPolicy.ERROR" -> ReportingPolicy.ERROR;
            default -> ReportingPolicy.WARN;
        };
    }

    @Nullable
    public static PsiMethod getAnnotatedMethod(@NotNull PsiAnnotation psiAnnotation) {
        PsiElement psiAnnotationParent = psiAnnotation.getParent();
        if (psiAnnotationParent == null) {
            return null;
        }
        PsiElement psiAnnotationParentParent = psiAnnotationParent.getParent();
        if (psiAnnotationParentParent instanceof PsiMethod annotatedPsiMethod) {
            // directly annotated with @Mapping
            return annotatedPsiMethod;
        }

        PsiElement psiAnnotationParentParentParent = psiAnnotationParentParent.getParent();
        if (psiAnnotationParentParentParent instanceof PsiAnnotation) {
            // inside @Mappings without array
            PsiElement mappingsAnnotationParent = psiAnnotationParentParentParent.getParent();
            if (mappingsAnnotationParent == null) {
                return null;
            }
            PsiElement mappingsAnnotationParentParent = mappingsAnnotationParent.getParent();
            if (mappingsAnnotationParentParent instanceof PsiMethod annotatedPsiMethod) {
                return annotatedPsiMethod;
            }
            return null;
        }
        else if (psiAnnotationParentParentParent instanceof PsiAnnotationParamListImpl) {
            // inside @Mappings wit array
            PsiElement mappingsArray = psiAnnotationParentParentParent.getParent();
            if (mappingsArray == null) {
                return null;
            }
            PsiElement mappingsAnnotationParent = mappingsArray.getParent();
            if (mappingsAnnotationParent == null) {
                return null;
            }
            PsiElement mappingsAnnotationParentParent = mappingsAnnotationParent.getParent();
            if (mappingsAnnotationParentParent instanceof PsiMethod annotatedPsiMethod) {
                return annotatedPsiMethod;
            }
            return null;

        }
        return null;
    }

}
