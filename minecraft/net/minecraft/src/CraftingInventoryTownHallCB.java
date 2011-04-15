package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.List;

public class CraftingInventoryTownHallCB extends CraftingInventoryInformatorCB
{

    public CraftingInventoryTownHallCB(IInventory iinventory, TileEntityTownHall tileentitytownhall)
    {
		super(iinventory, tileentitytownhall);
        townhall = tileentitytownhall;
    }

    public void func_20114_a()
    {
        super.updateCraftingResults();
    }


    public boolean isUsableByPlayer(EntityPlayer entityplayer)
    {
        return townhall.canInteractWith(entityplayer);
    }

	public void onCraftGuiClosed(EntityPlayer entityplayer)
    {
    }

    private TileEntityTownHall townhall;
}
