package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.ArrayList;

public class SlotRestricted extends Slot
{
    public SlotRestricted(IInventory iinventory, int i, int j, int k,
		boolean canBeFilled)
    {
		super(iinventory, i, j, k);
		inventory = iinventory;
        slotIndex = i;
		this.validItems = new ArrayList<Item>();
		this.canBeFilled = canBeFilled;
    }

    public SlotRestricted(IInventory iinventory, int i, int j, int k,
		boolean canBeFilled, Item validItem)
    {
		super(iinventory, i, j, k);
		inventory = iinventory;
        slotIndex = i;
		this.validItems = new ArrayList<Item>();
		this.canBeFilled = canBeFilled;
		if(validItem != null)
		{
			this.validItems.add(validItem);
		}
    }

	public SlotRestricted(IInventory iinventory, int i, int j, int k,
		boolean canBeFilled, ArrayList<Item> validItems)
    {
		super(iinventory, i, j, k);
		inventory = iinventory;
        slotIndex = i;
		this.validItems = new ArrayList<Item>();
		this.canBeFilled = canBeFilled;
		if(validItems != null)
		{
			this.validItems.addAll(validItems);
		}
    }

    public boolean isItemValid(ItemStack itemstack)
    {
		if(this.validItems.size()== 0)
		{
			return true;
		}
		else
		{
			boolean isValid = false;
			for(int i = 0; i < validItems.size(); i++)
			{
				if(itemstack.getItem().getItemName() == validItems.get(i).getItemName())
				{
					isValid = true;
				}
			}

			if(isValid && this.canBeFilled == true)
			{
				return true;
			}
			return false;
		}
    }

    public void putStack(ItemStack itemstack)
    {
		if(this.canBeFilled)
		{
			inventory.setInventorySlotContents(slotIndex, itemstack);
			onSlotChanged();
		}
    }

    public int getSlotStackLimit()
    {
        return inventory.getInventoryStackLimit();
    }

	private final boolean canBeFilled;
	private final ArrayList<Item> validItems;
    private final int slotIndex;
    private final IInventory inventory;
    public int field_20007_a;
    public int xDisplayPosition;
    public int yDisplayPosition;
}
