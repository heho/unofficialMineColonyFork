package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

public class TileEntityChanger extends TileEntity
    implements IInventory
{

    public TileEntityChanger()
    {
        changerItemStacks = new ItemStack[13];
        changerCurrentItemsValue = 0;
		changerCurrentOutputsValue = 0;
		changerLastOutputsValue = 0;
		lastStackHeight = new int[13];
		stackValue = new int [13];
		stackItem = new Item [13];

		for(int i = 1; i <= 12; i++)
		{
			lastStackHeight[i] = 0;
		}

		stackValue[1] = 1;
		stackValue[2] = 10;
		stackValue[3] = 50;
		stackValue[4] = 100;
		stackValue[5] = 250;
		stackValue[6] = 1000;
		stackValue[7] = 1;
		stackValue[8] = 10;
		stackValue[9] = 50;
		stackValue[10] = 100;
		stackValue[11] = 250;
		stackValue[12] = 1000;

		stackItem[1] = mod_MineColony.moneyBronze;
		stackItem[2] = mod_MineColony.moneySilver;
		stackItem[3] = mod_MineColony.moneyGold;
		stackItem[4] = Item.ingotIron;
		stackItem[5] = Item.ingotGold;
		stackItem[6] = Item.diamond;
		stackItem[7] = mod_MineColony.moneyBronze;
		stackItem[8] = mod_MineColony.moneySilver;
		stackItem[9] = mod_MineColony.moneyGold;
		stackItem[10] = Item.ingotIron;
		stackItem[11] = Item.ingotGold;
		stackItem[12] = Item.diamond;

    }

    public int getSizeInventory()
    {
        return changerItemStacks.length;
    }

    public ItemStack getStackInSlot(int i)
    {
        return changerItemStacks[i];
    }

    public ItemStack decrStackSize(int i, int j)
    {
        if(changerItemStacks[i] != null)
        {
            if(changerItemStacks[i].stackSize <= j)
            {
                ItemStack itemstack = changerItemStacks[i];
                changerItemStacks[i] = null;
                return itemstack;
            }
            ItemStack itemstack1 = changerItemStacks[i].splitStack(j);
            if(changerItemStacks[i].stackSize == 0)
            {
                changerItemStacks[i] = null;
            }
            return itemstack1;
        } else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
		if(itemstack == null)
		{
			lastStackHeight[i] = 0;
		}
		else
		{
			lastStackHeight[i] = itemstack.stackSize;
		}
		
        changerItemStacks[i] = itemstack;
        if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    public String getInvName()
    {
        return "Changer";
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
        changerItemStacks = new ItemStack[getSizeInventory()];
        for(int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            byte byte0 = nbttagcompound1.getByte("Slot");
            if(byte0 >= 0 && byte0 < 7)
            {
                changerItemStacks[byte0] = new ItemStack(nbttagcompound1);
            }
        }

		//onInventoryChanged();
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        for(int i = 0; i < 7; i++)
        {
            if(changerItemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                changerItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.setTag(nbttagcompound1);
            }
        }

        nbttagcompound.setTag("Items", nbttaglist);
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

	public int getStackValue(int i)
	{
		if(changerItemStacks[i] == null)
		{
			return 0;
		}

		return changerItemStacks[i].stackSize * stackValue[i];
	}

	public void setStackIfPossible(int i, int valueOfItem, int currentCredit, Item stackItem)
	{
		int value = (currentCredit / valueOfItem);

		if(value != 0)
		{
			ItemStack stack = new ItemStack(stackItem,
					1);
			setInventorySlotContents(i, stack);
		}
		else
		{
			setInventorySlotContents(i, null);
		}
	}

    public void updateEntity()
    {
    }

	public boolean outputChanged()
	{
		for(int i = 7; i <= 12; i++)
		{
			if(changerItemStacks[i] != null)
			{
				if(changerItemStacks[i].stackSize != lastStackHeight[i])
				{
					return true;
				}
			}
			else
			{
				if(lastStackHeight[i] != 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	public void onInventoryChanged()
    {

        if(outputChanged())
		{
			//setInventorySlotContents(7, new ItemStack(Item.stick,1));
			
			changerCurrentOutputsValue = 0;
			for(int i = 7; i <= 12; i++)
			{
				changerCurrentOutputsValue += getStackValue(i);
			}

			changerLastOutputsValue = 0;
			for(int i = 7; i <= 12; i++)
			{
				changerLastOutputsValue += lastStackHeight[i] * stackValue[i];
			}

			for(int i = 7; i <= 12; i++)
			{
				lastStackHeight[i] = 0;
				setInventorySlotContents(i, null);
			}

			int OutputsDifference = changerLastOutputsValue
					- changerCurrentOutputsValue;
			
			System.out.println(OutputsDifference);

			//get as much money without going under 0 beginning with the largest
			for(int i = 6; i >= 1; i--)
			{
				
				while(stackValue[i] <= OutputsDifference && changerItemStacks[i] != null)
				{
					if(changerItemStacks[i].stackSize == 1)
					{
						changerItemStacks[i] = null;
					}
					else
					{
						changerItemStacks[i].splitStack(1);
					}
					OutputsDifference -= stackValue[i];
				}
			}

			System.out.println(OutputsDifference);

			//go the smallest amount under zero
			for(int i = 1; i <= 6; i++)
			{

				System.out.println(stackValue[i]);

				if(stackValue[i] >= OutputsDifference && changerItemStacks[i] != null)
				{
					if(changerItemStacks[i].stackSize == 1)
					{
						changerItemStacks[i] = null;
					}
					else
					{
						changerItemStacks[i].splitStack(1);
					}
					OutputsDifference -= stackValue[i];
				}
			}

			OutputsDifference = 0 - OutputsDifference;

			System.out.println(OutputsDifference);
			//give recharge
			for(int i = 6; i >= 1; i--)
			{

				while(stackValue[i] <= OutputsDifference)
				{
					if(changerItemStacks[i] == null)
					{
						setInventorySlotContents(i, new ItemStack(stackItem[i], 1));
					}
					else if(changerItemStacks[i].stackSize <= 63)
					{
						changerItemStacks[i].stackSize++;
					}
					else
					{
						break;
					}

					OutputsDifference -= stackValue[i];
				}
			}

			System.out.println(OutputsDifference);

			onInventoryChanged();
		}
		else
		{
			if(!worldObj.multiplayerWorld)
			{
				changerCurrentItemsValue = 0;
				for(int i = 1; i <= 6; i++)
				{
					changerCurrentItemsValue += getStackValue(i);
				}


				setStackIfPossible(7, 1, changerCurrentItemsValue, mod_MineColony.moneyBronze);
				setStackIfPossible(8, 10, changerCurrentItemsValue, mod_MineColony.moneySilver);
				setStackIfPossible(9, 50, changerCurrentItemsValue, mod_MineColony.moneyGold);
				setStackIfPossible(10, 100, changerCurrentItemsValue, Item.ingotIron);
				setStackIfPossible(11, 250, changerCurrentItemsValue, Item.ingotGold);
				setStackIfPossible(12, 1000, changerCurrentItemsValue, Item.diamond);
			}
		}
    }

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        if(worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
        {
            return false;
        }
        return entityplayer.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64D;
    }

    private ItemStack changerItemStacks[];
    public int changerCurrentItemsValue;
	public int changerCurrentOutputsValue;
	public int changerLastOutputsValue;
	public int[] lastStackHeight;
	public int[] stackValue;
	public Item[] stackItem;
}
