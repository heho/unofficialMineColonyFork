package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html

import java.lang.StringBuilder;

// Decompiler options: packimports(3) braces deadcode

public class TileEntityMarket extends TileEntityInformator
{

    public TileEntityMarket()
    {
		townHallPosition = null;
		stalls = new EnumMarketStalls[4];
		for(int i = 0; i < stalls.length; i++)
		{
			stalls[i] = EnumMarketStalls.EMPTY;
		}
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
		townHallX = nbttagcompound.getDouble("townhallPosX");
		townHallY = nbttagcompound.getDouble("townhallPosY");
		townHallZ = nbttagcompound.getDouble("townhallPosZ");



		for(int i = 0; i < stalls.length; i++)
		{
			EnumMarketStalls stallIds[] = EnumMarketStalls.values();
			for(int j = 0; j < stallIds.length; j++)
			{
				if(stallIds[j].id == nbttagcompound.getInteger((new StringBuilder("stall")).append(i).toString()))
				{
					stalls[i] = stallIds[j];
					break;
				}
			}
		}
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
		nbttagcompound.setDouble("townhallPosX", townHallX);
		nbttagcompound.setDouble("townhallPosY", townHallY);
		nbttagcompound.setDouble("townhallPosZ", townHallZ);

		for(int i = 0; i < stalls.length; i++)
		{
			nbttagcompound.setInteger((new StringBuilder("stall")).append(i).toString(), stalls[i].id);
		}
    }

    public void updateEntity()
    {

    }

	public void setTownHall(int i, int j, int k, World world)
	{
		Vec3D nextInfluenced = scanForBlockNearPoint(world, mod_MineColony.hutMarket.blockID, i,j,k, 100, 30, 100);
		if(nextInfluenced != null)
		{
			TileEntityMarket market = (TileEntityMarket) (world.getBlockTileEntity((int)nextInfluenced.xCoord, (int)nextInfluenced.yCoord, (int)nextInfluenced.zCoord));
			this.townHallX = market.townHallX;
			this.townHallY = market.townHallY;
			this.townHallZ = market.townHallZ;
			return;
		}
		nextInfluenced = scanForBlockNearPoint(world, mod_MineColony.hutCitizen.blockID, i,j,k, 60, 30, 60);
		if(nextInfluenced == null)
		{
			TileEntityCitizen citizen = (TileEntityCitizen) (world.getBlockTileEntity((int)nextInfluenced.xCoord, (int)nextInfluenced.yCoord, (int)nextInfluenced.zCoord));
			this.townHallX = citizen.townHallX;
			this.townHallY = citizen.townHallY;
			this.townHallZ = citizen.townHallZ;
			return;
		}
		if(nextInfluenced == null)
		{
			nextInfluenced = scanForBlockNearPoint(world, mod_MineColony.hutTownHall.blockID, i,j,k, 100, 30, 100);
			this.townHallX = nextInfluenced.xCoord;
			this.townHallY = nextInfluenced.yCoord;
			this.townHallZ = nextInfluenced.zCoord;
		}
	}

	protected Vec3D scanForBlockNearPoint(World world, int blockId, int x, int y, int z,
		int rx, int ry, int rz) {

	Vec3D entityVec = Vec3D.createVector(x, y, z);

	Vec3D closestVec = null;
	double minDistance = 999999999;

	for (int i = x - rx; i <= x + rx; i++)
		for (int j = y - ry; j <= y + ry; j++)
			for (int k = z - rz; k <= z + rz; k++) {
				if (world.getBlockId(i, j, k) == blockId) {
					Vec3D tempVec = Vec3D.createVector(i, j, k);

					if ((closestVec == null
							|| tempVec.distanceTo(entityVec) < minDistance) && tempVec.distanceTo(entityVec) != 0) {
						closestVec = tempVec;
						minDistance = closestVec.distanceTo(entityVec);
					}
				}
			}

	return closestVec;
	}

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        if(worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
        {
            return false;
        }
        return entityplayer.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64D;
    }

	protected Vec3D townHallPosition;
	protected double townHallX;
	protected double townHallY;
	protected double townHallZ;
	protected EnumMarketStalls[] stalls;
}
