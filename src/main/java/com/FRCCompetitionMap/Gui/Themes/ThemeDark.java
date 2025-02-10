package com.FRCCompetitionMap.Gui.Themes;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class ThemeDark
	extends FlatMacDarkLaf
{
	public static final String NAME = "app_dark";

	public static boolean setup() {
		return setup( new ThemeDark() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, ThemeDark.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
