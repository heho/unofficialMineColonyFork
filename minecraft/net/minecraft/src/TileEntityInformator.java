package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

public class TileEntityInformator extends TileEntity
    implements IInventory
{

    public TileEntityInformator()
    {

    }

    public int getSizeInventory()
    {
        return 0;
    }

    public ItemStack getStackInSlot(int i)
    {
        return null;
    }

    public ItemStack decrStackSize(int i, int j)
    {
        return null;
    }

    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
    }

    public String getInvName()
    {
        return "Informator";
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);

    }

    public int getInventoryStackLimit()
    {
        return 0;
    }

    public void updateEntity()
    {
    }

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        if(worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
        {
            return false;
        }
        return entityplayer.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64D;
    }
}
