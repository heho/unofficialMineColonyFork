package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html

import java.util.ArrayList;

// Decompiler options: packimports(3) braces deadcode

public class TileEntityTownHall extends TileEntityInformator
{

    public TileEntityTownHall()
    {
		cityName = "<!default>";
		numberOfSettlers = 0;
		numberOfBurgesses = 0;
		numberOfEsquires = 0;
		numberOfNobles = 0;
		numberOfClerics = 0;
		gains = 0;
		expanses = 0;
		taxHeightforSettlers = 0;
		taxHeightforBurgesses = 0;
		taxHeightforEsquires = 0;
		taxHeightforNobles = 0;
		taxHeightforClerics = 0;
		marketXPositions = new ArrayList<Double>();
		marketYPositions = new ArrayList<Double>();
		marketZPositions = new ArrayList<Double>();
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
		cityName = nbttagcompound.getString("cityName");
		numberOfSettlers = nbttagcompound.getInteger("nSettlers");
		numberOfBurgesses = nbttagcompound.getInteger("nBurgesses");
		numberOfEsquires = nbttagcompound.getInteger("nEsquires");
		numberOfNobles = nbttagcompound.getInteger("nNobles");
		numberOfClerics = nbttagcompound.getInteger("nClerics");
		gains = nbttagcompound.getInteger("gains");
		expanses = nbttagcompound.getInteger("expanses");
		taxHeightforSettlers = nbttagcompound.getInteger("tSettlers");
		taxHeightforBurgesses = nbttagcompound.getInteger("tBurgesses");
		taxHeightforEsquires = nbttagcompound.getInteger("tEsquires");
		taxHeightforNobles = nbttagcompound.getInteger("tNobles");
		taxHeightforClerics = nbttagcompound.getInteger("tCleris");
		int numberOfMarkets = nbttagcompound.getInteger("nMarkets");

		for(int i = 0; i < numberOfMarkets; i++)
		{
			marketXPositions.add(nbttagcompound.getDouble((new StringBuilder().append("marketPosX").append(i).toString())));
			marketYPositions.add(nbttagcompound.getDouble((new StringBuilder().append("marketPosY").append(i).toString())));
			marketZPositions.add(nbttagcompound.getDouble((new StringBuilder().append("marketPosZ").append(i).toString())));
		}
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);

		nbttagcompound.setString("cityName", cityName);
		nbttagcompound.setInteger("nSettlers", numberOfSettlers);
		nbttagcompound.setInteger("nBurgesses", numberOfBurgesses );
		nbttagcompound.setInteger("nEsquires", numberOfEsquires);
		nbttagcompound.setInteger("nNobles", numberOfNobles);
		nbttagcompound.setInteger("nClerics", numberOfClerics);
		nbttagcompound.setInteger("gains", gains);
		nbttagcompound.setInteger("expanses", expanses);
		nbttagcompound.setInteger("tSettlers", taxHeightforSettlers);
		nbttagcompound.setInteger("tBurgesses", taxHeightforBurgesses);
		nbttagcompound.setInteger("tEsquires", taxHeightforEsquires);
		nbttagcompound.setInteger("tNobles", taxHeightforNobles);
		nbttagcompound.setInteger("tClerics", taxHeightforClerics);
		nbttagcompound.setInteger("nMarkets", marketXPositions.size());

		for(int i = 0; i < marketXPositions.size(); i++)
		{
			nbttagcompound.setDouble((new StringBuilder().append("marketPosX").append(i).toString()), marketXPositions.get(i));
			nbttagcompound.setDouble((new StringBuilder().append("marketPosY").append(i).toString()), marketYPositions.get(i));
			nbttagcompound.setDouble((new StringBuilder().append("marketPosZ").append(i).toString()), marketZPositions.get(i));
		}
    }

    public void updateEntity()
    {
    }

	public void deleteMarket(int i, int j, int k)
	{
		System.out.println("x");
		for(int h = 0; h < marketXPositions.size(); h++)
		{
			System.out.println(marketXPositions.get(h));
			System.out.println(marketYPositions.get(h));
			System.out.println(marketZPositions.get(h));
			System.out.println(i);
			System.out.println(j);
			System.out.println(k);
			
			if(marketXPositions.get(h) == i && marketYPositions.get(h) == j && marketZPositions.get(h) == k)
			{
				marketXPositions.remove(h);
				marketYPositions.remove(h);
				marketZPositions.remove(h);
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

	public String cityName;
	public int numberOfSettlers;
	public int numberOfBurgesses;
	public int numberOfEsquires;
	public int numberOfNobles;
	public int numberOfClerics;
	public int gains;
	public int expanses;
	public int taxHeightforSettlers;
	public int taxHeightforBurgesses;
	public int taxHeightforEsquires;
	public int taxHeightforNobles;
	public int taxHeightforClerics;
	public ArrayList<Double> marketXPositions;
	public ArrayList<Double> marketYPositions;
	public ArrayList<Double> marketZPositions;
}
