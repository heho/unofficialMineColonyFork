package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class TileEntityCitizen extends TileEntityHut {
	private List<EntityCitizen> inhabitants;
	
	
	public TileEntityCitizen()
	{
		inhabitants = new ArrayList<EntityCitizen>();
	}
	
	

	public void spawnInhabitant()
	{
		int newId = inhabitants.size();
		// spawn delivery man
		inhabitants.add((EntityCitizen) EntityList.createEntityInWorld("Citizen", worldObj));

		// scan for first free block near chest
		Vec3D spawnPoint = scanForBlockNearPoint(worldObj, 0, xCoord, yCoord, zCoord, 1, 0, 1);
		if(spawnPoint==null)
			spawnPoint = scanForBlockNearPoint(worldObj, Block.snow.blockID,  xCoord, yCoord, zCoord, 1, 0, 1);

		if(spawnPoint!=null)
		{
			inhabitants.get(newId).setPosition(spawnPoint.xCoord, spawnPoint.yCoord, spawnPoint.zCoord);
			inhabitants.get(newId).setHomePosition( xCoord, yCoord, zCoord);
			worldObj.entityJoinedWorld(inhabitants.get(newId));
		}
		
		inhabitants.get(newId).setHomePosition( xCoord, yCoord, zCoord);
	}

	public void workerCheckin(EntityCitizen citizen)
	{
		/*if (dm !=d && dm!=null)
		{
			dm.isDead=true;
		}
		dm=d;*/
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

	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
	}

	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
	}

	protected double townHallX;
	protected double townHallY;
	protected double townHallZ;
}
