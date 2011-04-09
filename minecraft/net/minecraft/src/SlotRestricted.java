package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public class SlotRestricted extends Slot
{

    public SlotRestricted(IInventory iinventory, int i, int j, int k,
		boolean canBeFilled, Item validItem)
    {
		super(iinventory, i, j, k);
		inventory = iinventory;
        slotIndex = i;
		this.canBeFilled = canBeFilled;
		this.validItem = validItem;
    }

    public boolean isItemValid(ItemStack itemstack)
    {
		if(this.validItem == null)
		{
			return true;
		}
		else
		{
			if(itemstack.getItem().getItemName() == this.validItem.getItemName()
				&& this.canBeFilled == true)
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
	private final Item validItem;
    private final int slotIndex;
    private final IInventory inventory;
    public int field_20007_a;
    public int xDisplayPosition;
    public int yDisplayPosition;
}
