/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.MetaAnnotationUtil;
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
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.ReportingPolicy;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.codeInsight.AnnotationUtil.findDeclaredAttribute;
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
        if ( selectedEditor instanceof TextEditor ) {
            Editor editor = ( (TextEditor) selectedEditor ).getEditor();

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

    public static Stream<PsiAnnotation> findAllDefinedMappingAnnotations(@NotNull PsiMethod method,
        MapStructVersion mapStructVersion) {
        //TODO cache
        Stream<PsiAnnotation> mappingsAnnotations = Stream.empty();
        PsiAnnotation mappings = findAnnotation( method, true, MapstructUtil.MAPPINGS_ANNOTATION_FQN );
        if ( mappings != null ) {
            //TODO maybe there is a better way to do this, but currently I don't have that much knowledge
            PsiNameValuePair mappingsValue = findDeclaredAttribute( mappings, null );
            if ( mappingsValue != null && mappingsValue.getValue() instanceof PsiArrayInitializerMemberValue ) {
                mappingsAnnotations = Stream.of( ( (PsiArrayInitializerMemberValue) mappingsValue.getValue() )
                    .getInitializers() )
                    .filter( MapstructAnnotationUtils::isMappingPsiAnnotation )
                    .map( memberValue -> (PsiAnnotation) memberValue );
            }
            else if ( mappingsValue != null && mappingsValue.getValue() instanceof PsiAnnotation ) {
                mappingsAnnotations = Stream.of( (PsiAnnotation) mappingsValue.getValue() );
            }
        }

        Stream<PsiAnnotation> mappingAnnotations = findMappingAnnotations( method, mapStructVersion );

        return Stream.concat( mappingAnnotations, mappingsAnnotations );
    }

    private static Stream<PsiAnnotation> findMappingAnnotations(@NotNull PsiMethod method,
        MapStructVersion mapStructVersion) {
        if ( mapStructVersion.isConstructorSupported() ) {
            // Meta annotations support was added when constructor support was added
            return MetaAnnotationUtil.findMetaAnnotations( method, Collections.singleton( MAPPING_ANNOTATION_FQN ) );
        }
        return Stream.of( method.getModifierList().getAnnotations() )
            .filter( MapstructAnnotationUtils::isMappingAnnotation );
    }

    public static Stream<PsiAnnotation> findAllDefinedValueMappingAnnotations(@NotNull PsiMethod method) {
        Stream<PsiAnnotation> valueMappingsAnnotations = Stream.empty();
        PsiAnnotation valueMappings = findAnnotation( method, true, MapstructUtil.VALUE_MAPPINGS_ANNOTATION_FQN );
        if ( valueMappings != null ) {
            PsiNameValuePair mappingsValue = findDeclaredAttribute( valueMappings, null );
            if ( mappingsValue != null && mappingsValue.getValue() instanceof PsiArrayInitializerMemberValue ) {
                valueMappingsAnnotations = Stream.of( ( (PsiArrayInitializerMemberValue) mappingsValue.getValue() )
                        .getInitializers() )
                    .filter( MapstructAnnotationUtils::isValueMappingPsiAnnotation )
                    .map( memberValue -> (PsiAnnotation) memberValue );
            }
            else if ( mappingsValue != null && mappingsValue.getValue() instanceof PsiAnnotation ) {
                valueMappingsAnnotations = Stream.of( (PsiAnnotation) mappingsValue.getValue() );
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
        return Objects.equals( psiAnnotation.getQualifiedName(), VALUE_MAPPING_ANNOTATION_FQN );
    }

    /**
     * @param psiAnnotation that needs to be checked
     *
     * @return {@code true} if the {@code psiAnnotation} is the {@link org.mapstruct.Mapping} annotation, {@code
     * false} otherwise
     */
    private static boolean isMappingAnnotation(PsiAnnotation psiAnnotation) {
        return Objects.equals( psiAnnotation.getQualifiedName(), MAPPING_ANNOTATION_FQN );
    }

    /**
     * Find the mapper config reference class or interface defined in the {@code mapperAnnotation}
     *
     * @param mapperAnnotation the mapper annotation in which the mapper config is defined
     *
     * @return the class / interface that is defined in the mapper config,
     * or {@code null} if there isn't anything defined
     */
    public static PsiModifierListOwner findMapperConfigReference(PsiAnnotation mapperAnnotation) {
        PsiNameValuePair configAttribute = findDeclaredAttribute( mapperAnnotation, "config" );
        if ( configAttribute == null ) {
            return null;
        }

        PsiAnnotationMemberValue configValue = configAttribute.getValue();
        if ( !( configValue instanceof PsiClassObjectAccessExpression ) ) {
            return null;
        }

        PsiJavaCodeReferenceElement referenceElement = ( (PsiClassObjectAccessExpression) configValue ).getOperand()
            .getInnermostComponentReferenceElement();
        if ( referenceElement == null ) {
            return null;
        }

        PsiElement resolvedElement = referenceElement.resolve();
        if ( !( resolvedElement instanceof PsiModifierListOwner ) ) {
            return null;
        }

        return (PsiModifierListOwner) resolvedElement;
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
    private static Stream<PsiClass> findReferencedMappers(PsiAnnotation mapperAnnotation) {
        PsiNameValuePair usesAttribute = findDeclaredAttribute( mapperAnnotation, "uses" );
        if ( usesAttribute == null ) {
            return Stream.empty();
        }

        PsiAnnotationMemberValue usesValue = usesAttribute.getValue();

        List<PsiClassObjectAccessExpression> usesExpressions = new ArrayList<>();
        if ( usesValue instanceof PsiArrayInitializerMemberValue ) {
            usesExpressions = Stream.of( ( (PsiArrayInitializerMemberValue) usesValue )
                    .getInitializers() )
                .filter( PsiClassObjectAccessExpression.class::isInstance )
                .map( PsiClassObjectAccessExpression.class::cast )
                .collect( Collectors.toList() );
        }
        else if ( usesValue instanceof PsiClassObjectAccessExpression ) {
            usesExpressions = List.of( (PsiClassObjectAccessExpression) usesValue );
        }

        return usesExpressions.stream()
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
    public static ReportingPolicy getReportingPolicyFromMethode( @NotNull PsiMethod method,
                                                                 @Nullable @NonNls String attributeName,
                                                                 @NotNull ReportingPolicy fallback ) {
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return fallback;
        }
        return getReportingPolicyFromClass( containingClass, attributeName, fallback );
    }

    @NotNull
    public static ReportingPolicy getReportingPolicyFromClass( @NotNull PsiClass containingClass,
                                                               @NonNls @Nullable String attributeName,
                                                               @NotNull ReportingPolicy fallback ) {
        PsiAnnotation mapperAnnotation = containingClass.getAnnotation( MapstructUtil.MAPPER_ANNOTATION_FQN );
        if (mapperAnnotation == null) {
            return fallback;
        }

        PsiAnnotationMemberValue classAnnotationOverwrite = mapperAnnotation.findDeclaredAttributeValue(
                attributeName );
        if (classAnnotationOverwrite != null) {
            return getReportingPolicyFromAnnotation( classAnnotationOverwrite, fallback );
        }
        return getReportingPolicyFromMapperConfig( mapperAnnotation, fallback );
    }

    @NotNull
    private static ReportingPolicy getReportingPolicyFromMapperConfig( @NotNull PsiAnnotation mapperAnnotation,
                                                                       @NotNull ReportingPolicy fallback) {
        PsiModifierListOwner mapperConfigReference = findMapperConfigReference(  mapperAnnotation );
        if ( mapperConfigReference == null ) {
            return fallback;
        }
        PsiAnnotation mapperConfigAnnotation = mapperConfigReference.getAnnotation(
                MapstructUtil.MAPPER_CONFIG_ANNOTATION_FQN );

        if (mapperConfigAnnotation == null) {
            return fallback;
        }
        PsiAnnotationMemberValue configValue =
                mapperConfigAnnotation.findDeclaredAttributeValue( "unmappedTargetPolicy" );
        if (configValue == null) {
            return fallback;
        }
        return getReportingPolicyFromAnnotation( configValue, fallback );
    }


    /**
     * Converts the configValue to ReportingPolicy enum. If no matching ReportingPolicy found, returns fallback.
     * @param configValue The annotation value to convert to ReportingPolicy enum
     * @param fallback the fallback value if no matching ReportingPolicy found
     * @return the mapped ReportingPolicy enum
     */
    @NotNull
    public static ReportingPolicy getReportingPolicyFromAnnotation( @NotNull PsiAnnotationMemberValue configValue,
                                                                    @NotNull ReportingPolicy fallback) {
        switch (configValue.getText()) {
            case "IGNORE":
            case "ReportingPolicy.IGNORE":
                return ReportingPolicy.IGNORE;
            case "ERROR":
            case "ReportingPolicy.ERROR":
                return ReportingPolicy.ERROR;
            case "WARN":
            case "ReportingPolicy.WARN":
                return ReportingPolicy.WARN;
            default:
                return fallback;
        }
    }

}
