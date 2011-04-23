package net.minecraft.src;

import java.util.List;

public class TileEntityWarehouse extends TileEntityHut {
	private EntityDeliveryMan dm;
	
	
	public TileEntityWarehouse()
	{
		
	}
	
	

	public void spawnWorker()
	{
		// spawn delivery man
		dm = (EntityDeliveryMan) EntityList.createEntityInWorld("DeliveryMan", worldObj);

		// scan for first free block near chest
		Vec3D spawnPoint = scanForBlockNearPoint(worldObj, 0, xCoord, yCoord, zCoord, 1, 0, 1);
		if(spawnPoint==null)
			spawnPoint = scanForBlockNearPoint(worldObj, Block.snow.blockID,  xCoord, yCoord, zCoord, 1, 0, 1);

		if(spawnPoint!=null)
		{
			dm.setPosition(spawnPoint.xCoord, spawnPoint.yCoord, spawnPoint.zCoord);
			dm.setHomePosition( xCoord, yCoord, zCoord);
			worldObj.entityJoinedWorld(dm);
		}
		dm.findRouteName();
		dm.setHomePosition( xCoord, yCoord, zCoord);
	}

	public void workerCheckin(EntityDeliveryMan d)
	{
		if (dm!=d && dm!=null)
		{
			dm.isDead=true;
		}
		dm=d;
	}
	

	public EntityDeliveryMan getDeliverymanAround2( )
	{
		List l = worldObj.getLoadedEntityList();
		for (int c=0;c<l.size(); c++)
			if (l.get(c) instanceof EntityDeliveryMan)
			{
			EntityDeliveryMan d=  (EntityDeliveryMan) l.get(c);
			if (d.isHomePosition(xCoord, yCoord, zCoord))
				return d;
			}
		return null;
	}
	
	public void removeWorker()
	{
		//EntityDeliveryMan dm = getWorkerAround(world, i,j,k);
		dm = getDeliverymanAround2();
		if(dm!=null)
		{
			dm.isDead=true;
		}
	}
	
	   public void updateEntity()
	    {

			//if(getWorkerAround(world, i,j,k)==null)
			//{
		   if (dm==null)
		   {
				EntityDeliveryMan dm2=getDeliverymanAround2();
				if ((dm2==null || !dm2.isHomePosition(xCoord, yCoord, zCoord) ) && (dm==null ||  dm.isDead || dm.stuckCount>12))
				{
					System.out.println("");
					if (dm!=null) dm.isDead=true;
					dm=null;
					spawnWorker();
				}
		   }
				//if(dm!=null)
				//dm.isDead = true;
				//spawnWorker(world, i, j, k);
			//}
	    }
	
	    protected TileEntity getBlockEntity()
	    {
	        return new TileEntityWarehouse();
	    } 
	   
	   public void readFromNBT(NBTTagCompound nbttagcompound)
	    {
	        super.readFromNBT(nbttagcompound);
	    }

	    public void writeToNBT(NBTTagCompound nbttagcompound)
	    {
	        super.writeToNBT(nbttagcompound);
	    }
	    
	    static 
	    {
	    	
	    }
	   
}
