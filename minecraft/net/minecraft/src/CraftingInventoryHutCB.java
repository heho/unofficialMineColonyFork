package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public class CraftingInventoryHutCB extends CraftingInventoryCB
{

    public CraftingInventoryHutCB(IInventory playerInventory, IInventory inventory)
    {
        field_20125_a = inventory;
        int i = inventory.getSizeInventory() / 9;
        int j = (i - 4) * 18;
        for(int k = 0; k < 4; k++)
        {
            for(int j1 = 0; j1 < 9; j1++)
            {
                addSlot(new Slot(inventory, j1 + k * 9, 8 + j1 * 18, 18 + k * 18));
            }

        }

        for(int l = 0; l < 3; l++)
        {
            for(int k1 = 0; k1 < 9; k1++)
            {
                addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 103 + l * 18 + j));
            }

        }

        for(int i1 = 0; i1 < 9; i1++)
        {
            addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + j));
        }

    }

    public boolean isUsableByPlayer(EntityPlayer entityplayer)
    {
        return field_20125_a.canInteractWith(entityplayer);
    }

    private IInventory field_20125_a;
}
