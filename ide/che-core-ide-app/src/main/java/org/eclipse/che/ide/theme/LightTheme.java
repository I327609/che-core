/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.theme;

import org.eclipse.che.ide.api.theme.Theme;

import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class LightTheme implements Theme {

    public static final String LIGHT_THEME_ID = "LightTheme";

    @Override
    public String getId() {
        return LIGHT_THEME_ID;
    }

    @Override
    public String getDescription() {
        return "Light Theme";
    }

    @Override
    public String getHoverBackgroundColor() {
        return "#D4E8FF";
    }

    @Override
    public String getKeyboardSelectionBackgroundColor() {
        return "#C3DEFF";
    }

    @Override
    public String getSelectionBackground() {
        return "#C3DEFF";
    }

    @Override
    public String getInactiveTabBackground() {
        return "#D6D6D9";
    }

    @Override
    public String getInactiveTabBorderColor() {
        return "#D6D6D9";
    }

    @Override
    public String getActiveTabBackground() {
        return "#ffffff";
    }

    @Override
    public String getActiveTabBorderColor() {
        return "#8E8E8E";
    }

    @Override
    public String getActiveTabTextColor() {
        return "#555555";
    }

    @Override
    public String getActiveTabTextShadow() {
        return "none";
    }

    @Override
    public String getActiveTabIconColor() {
        return "#1A68AF";
    }

    @Override
    public String getTabTextColor() {
        return "#555555";
    }

    @Override
    public String getHoveredTabTextColor() {
        return "#333333";
    }

    @Override
    public String getTabsPanelBackground() {
        return "#D6D6D9";
    }

    @Override
    public String getTabBorderColor() {
        return "#8E8E8E";
    }

    @Override
    public String getTabUnderlineColor() {
        return "rgb(70,102,149)";
    }

    @Override
    public String getPartBackground() {
        return "#ffffff";
    }

    @Override
    public String getPartToolbar() {
        return "#D6D6D6";
    }

    @Override
    public String getPartToolbarActive() {
        return "rgba(195,222,255,1)";
    }

    @Override
    public String getPartToolbarShadow() {
        return "#bdbdbd";
    }

    @Override
    public String getPartToolbarSeparatorTopColor() {
        return "#FFFFFF";
    }

    @Override
    public String getPartToolbarSeparatorBottomColor() {
        return "#AAAAAA";
    }

    @Override
    public String getMainFontColor() {
        return "#222222";
    }

    @Override
    public String getRadioButtonBackgroundColor() {
        return "#BDBDBD";
    }

    @Override
    public String getDisabledMenuColor() {
        return "#AAAAAA";
    }

    @Override
    public String getDialogContentBackground() {
        return "#FFFFFF";
    }

    @Override
    public String getButtonTopColor() {
        return "#D6D6D6";
    }

    @Override
    public String getSocialButtonColor() {
        return "#ffffff";
    }

    @Override
    public String getButtonColor() {
        return "#D6D6D6";
    }

    @Override
    public String getInputBackground() {
        return "#FFFFFF";
    }

    @Override
    public String getEditorPanelBackgroundColor() {
        return "#D6D6D9";
    }

    @Override
    public String getEditorBackgroundColor() {
        return "white";
    }

    @Override
    public String getEditorCurrentLineColor() {
        return "#E8F2FF";
    }

    @Override
    public String getEditorDefaultFontColor() {
        return "black";
    }

    @Override
    public String getEditorSelectionColor() {
        return "#d4e2ff";
    }

    @Override
    public String getEditorInactiveSelectionColor() {
        return "#d4d4d4";
    }

    @Override
    public String getEditorCursorColor() {
        return "black";
    }

    @Override
    public String getEditorGutterColor() {
        return "#eee";
    }

    @Override
    public String getEditorKeyWord() {
        return "#708";
    }

    @Override
    public String getEditorAtom() {
        return "#219";
    }

    @Override
    public String getEditorNumber() {
        return "#164";
    }

    @Override
    public String getEditorDef() {
        return "#00f";
    }

    @Override
    public String getEditorVariable() {
        return "black";
    }

    @Override
    public String getEditorVariable2() {
        return "#05a";
    }

    @Override
    public String getEditorProperty() {
        return "black";
    }

    @Override
    public String getEditorOperator() {
        return "black";
    }

    @Override
    public String getEditorComment() {
        return "#a50";
    }

    @Override
    public String getEditorString() {
        return "#a11";
    }

    @Override
    public String getEditorMeta() {
        return "#049";
    }

    @Override
    public String getEditorError() {
        return "#f00";
    }

    @Override
    public String getEditorBuiltin() {
        return "#30a";
    }

    @Override
    public String getEditorTag() {
        return "#170";
    }

    @Override
    public String getEditorAttribute() {
        return "#00c";
    }

    @Override
    public String getEditorString2() {
        return "#f50";
    }

    @Override
    public String getCompletionPopupBackgroundColor() {
        return "#303030";
    }

    @Override
    public String getCompletionPopupBorderColor() {
        return "#484848";
    }

    @Override
    public String getWindowContentBackground() {
        return getDialogContentBackground();
    }

    @Override
    public String getWindowHeaderBackground() {
        return "#bdbdbd";
    }

    @Override
    public String getWindowSeparatorColor() {
        return "#818181";
    }

    @Override
    public String getWizardStepsColor() {
        return "#DBDBDB";
    }

    @Override
    public String getWizardStepsBorderColor() {
        return "#BDBDBD";
    }

    @Override
    public String getWelcomeFontColor() {
        return "#5E5E5E";
    }

    @Override
    public String getCaptionFontColor() {
        return "#888888";
    }

    @Override
    public String getConsolePanelColor() {
        return getTabsPanelBackground();
    }

    @Override
    public String getStatusPanelColor() {
        return getTabsPanelBackground();
    }

    @Override
    public String getCellOddRowColor() {
        return "#f3f7fb";
    }

    @Override
    public String getCellOddEvenColor() {
        return "#ffffff";
    }

    @Override
    public String getCellKeyboardSelectedRowColor() {
        return "#ffc";
    }

    @Override
    public String getCellHoveredRow() {
        return "#eee";
    }

    @Override
    public String getMainMenuBkgColor() {
        return "#f6f6f6";
    }

    @Override
    public String getMainMenuFontColor() {
        return "#222222";
    }

    @Override
    public String getNotableButtonTopColor() {
        return "#dbdbdb";
    }

    @Override
    public String getNotableButtonColor() {
        return "#2d6ba3";
    }

    @Override
    public String getTabBorderShadow() {
        return "rgba(198, 205, 209, 0.5)";
    }

    @Override
    public String getButtonTextShadow() {
        return "rgba(188, 195, 199, 0.5)";
    }

    @Override
    public String getTreeTextFileColor() {
        return "#7b7b7b";
    }

    @Override
    public String getTreeTextFolderColor() {
        return "#606060";
    }

    @Override
    public String getTreeTextShadow() {
        return "rgba(255, 255, 255, 0.5)";
    }

    @Override
    public String getTreeIconFileColor() {
        return "#606060";
    }

    @Override
    public String getBoxBorderColor() {
        return "#aaaaaa";
    }

    @Override
    public String getBoxTextColor() {
        return "#606060";
    }


    @Override
    public String getBoxInnerShadow() {
        return "0px 1px 2px 0px #777777 inset";
    }

    @Override
    public String getBoxOuterShadow() {
        return "0px 1px 0px 0px #7a7a7a";
    }

    @Override
    public String getBoxBackgroundColor() {
        return "#e2e2e2";
    }

    @Override
    public String getBoxBackgroundColorDisabled() {
        return getTabsPanelBackground();
    }

    @Override
    public String getBoxTextShadow() {
        return "0px 1px 0px #ffffff";
    }

    public String getButtonHoverTextColor() {
        return "#000000";
    }

    @Override
    public String getButtonHoverColor() {
        return "#ffffff";
    }

    @Override
    public String getToolbarActionGroupShadowColor() {
        return "#c5c5c5";
    }

    @Override
    public String getToolbarActionGroupBackgroundColor() {
        return "#ffffff";
    }

    @Override
    public String getToolbarActionGroupBorderColor() {
        return "#afafaf";
    }

    @Override
    public String getToolbarBackgroundImage() {
        return "linear-gradient(-180deg, #d3d3d4   0%, #bdbec0   100%)";
    }

    @Override
    public String getToolbarBackgroundColor() {
        return " #EAEAEA";
    }

    @Override
    public String getToolbarIconColor() {
        return "#7c7c7c";
    }

    @Override
    public String getToolbarHoverIconColor() {
        return "#565656";
    }

    @Override
    public String getTooltipBackgroundColor() {
        return "#FFFFFF";
    }

    @Override
    public String getPerspectiveSwitcherBackgroundColor() {
        return "#1a68af";
    }

    @Override
    public String getSelectCommandActionIconColor() {
        return "#1a68af";
    }

    @Override
    public String getSelectCommandActionIconBackgroundColor() {
        return "#e9e9e9";
    }

    @Override
    public String getSelectCommandActionColor() {
        return "#555555";
    }

    @Override
    public String getSelectCommandActionHoverColor() {
        return "#565656";
    }

    @Override
    public String getDropDownListSelectionBackground() {
        return "rgba(0, 0, 0, 0.12)";
    }

    @Override
    public String getProgressColor() {
        return "#000000";
    }

    @Override
    public String getSuccessEventColor() {
        return "#7dc878";
    }

    @Override
    public String getErrorEventColor() {
        return "#e25252";
    }

    @Override
    public String getDelimeterColor() {
        return "#2f2f2f";
    }

    @Override
    public String getLinkColor() {
        return "#acacac";
    }

    @Override
    public String getFactoryLinkColor() {
        return "#60abe0";
    }

    @Override
    public String getMinimizeIconColor() {
        return "#7b7b7b";
    }

    @Override
    public String getMinimizeIconHoverColor() {
        return "#333333";
    }

    @Override
    public String getOutputFontColor() {
        return "#e6e6e6";
    }

    @Override
    public String getOutputLinkColor() {
        return "#61b7ef";
    }

    @Override
    public String getEditorInfoBackgroundColor() {
        return "#ddd";
    }


    @Override
    public String getEditorInfoBorderColor() {
        return "#bdbdbd";
    }

    @Override
    public String getEditorInfoBorderShadowColor() {
        return "#f0f0f0";
    }

    @Override
    public String getEditorLineNumberColor() {
        return "#888888";
    }

    @Override
    public String getEditorSeparatorColor() {
        return "#888888";
    }

    @Override
    public String getBlueIconColor() {
        return "#1a68af";
    }

    @Override
    public String getSplitterSmallBorderColor() {
        return "#8E8E8E";
    }

    @Override
    public String getSplitterLargeBorderColor() {
        return "#E1E1E1";
    }

    @Override
    public String getBadgeBackgroundColor() {
        return "rgb(78, 171, 255)";
    }

    @Override
    public String getBadgeFontColor() {
        return "white";
    }

}
