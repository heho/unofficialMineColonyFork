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
		marketPositions = new ArrayList<Vec3D>();
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
			double posX = nbttagcompound.getDouble((new StringBuilder().append("marketPosX").append(i).toString()));
			double posY = nbttagcompound.getDouble((new StringBuilder().append("marketPosY").append(i).toString()));
			double posZ = nbttagcompound.getDouble((new StringBuilder().append("marketPosZ").append(i).toString()));
			marketPositions.add(Vec3D.createVector(posX, posY, posZ));
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
		nbttagcompound.setInteger("nMarkets", marketPositions.size());

		for(int i = 0; i < marketPositions.size(); i++)
		{
			nbttagcompound.setDouble((new StringBuilder().append("marketPosX").append(i).toString()), marketPositions.get(i).xCoord);
			nbttagcompound.setDouble((new StringBuilder().append("marketPosY").append(i).toString()), marketPositions.get(i).yCoord);
			nbttagcompound.setDouble((new StringBuilder().append("marketPosZ").append(i).toString()), marketPositions.get(i).zCoord);
		}
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
	public ArrayList<Vec3D> marketPositions;
}
