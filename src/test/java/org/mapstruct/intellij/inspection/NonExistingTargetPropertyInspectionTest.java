package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;

public class NonExistingTargetPropertyInspectionTest extends BaseInspectionTest {

  @Override
  protected @NotNull Class<? extends LocalInspectionTool> getInspection() {
    return NonExistingTargetPropertiesInspection.class;
  }

  public void testNonExistingProperty() {
    doTest();


  }
}
