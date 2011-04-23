package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public enum EnumMarketStalls
{
	EMPTY("EMPTY", 0),
    FISH("FISH", 1),
	BAKEDGOODS("BAKEDGOODS", 2),
	TOOLS("TOOLS", 3),
	WEAPONS("WEAPONS", 4);

	private EnumMarketStalls(String s, int i)
    {
        name = s;
        id = i;
    }

	public final String name;
	public final int id;
}
