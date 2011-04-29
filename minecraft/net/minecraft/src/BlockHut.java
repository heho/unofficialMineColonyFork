package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockHut extends BlockChest {
	protected int hutWidth;
	protected int hutHeight;
	protected int clearingRange;
	protected int halfWidth;
	protected int workingRange;
	protected int textureID;
	
	public BlockHut(int blockID)
	{
		super(blockID);
		setTickOnLoad(true);
		
		// Need to create the item that will be dropped
		//Item.itemsList[blockID] = new ItemBlock(blockID - 256);
	}
	
	public int idDropped(int i, Random random) {
		return blockID;
	}
	
	protected int findTopGround(World world, int x, int z) {
		int ySolid = world.findTopSolidBlock(x, z);
		int blockId = world.getBlockId(x, ySolid, z);
		while(blockId==Block.leaves.blockID ||
				blockId==Block.wood.blockID ||
				blockId==Block.cactus.blockID ||
				blockId==Block.crops.blockID ||
				blockId==Block.fence.blockID ||
				blockId==Block.fire.blockID ||
				blockId==0)
		{
			ySolid--;
			blockId = world.getBlockId(x, ySolid, z);
		}
		return ySolid;
	}
	
	// scan for block type in the specified range
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

						if (closestVec == null
								|| tempVec.distanceTo(entityVec) < minDistance) {
							closestVec = tempVec;
							minDistance = closestVec.distanceTo(entityVec);
						}
					}
				}

		return closestVec;
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        Object obj = (TileEntityChest)world.getBlockTileEntity(i, j, k);
        if(world.multiplayerWorld)
        {
            return true;
        } else
        {
            TileEntityChest tileentityhut = (TileEntityChest)world.getBlockTileEntity(i, j, k);
//			entityplayer.displayGUIChest(((IInventory) (obj)));
			GuiHut guiHut = new GuiHut(entityplayer.inventory, tileentityhut);
            ModLoader.OpenGUI(entityplayer, guiHut);
            return true;
        }
    }

	protected EntityWorker createEntity(World world)
	{
		return null;
	}

	public void spawnWorker(World world, int i, int j, int k)
	{
		// spawn miner
		//el = new EntityLumberjack(world, workingRange);
		EntityWorker worker = createEntity(world);

		// scan for first free block near chest
		Vec3D spawnPoint = scanForBlockNearPoint(world, 0, i, j, k, 1, 0, 1);

		if(spawnPoint==null)
			spawnPoint = scanForBlockNearPoint(world, Block.snow.blockID, i, j, k, 1, 0, 1);

		if(spawnPoint!=null)
		{
			worker.setPosition(spawnPoint.xCoord, spawnPoint.yCoord, spawnPoint.zCoord);
			worker.setHomePosition(i, j, k);
			world.entityJoinedWorld(worker);
		}

	}
	
    protected TileEntity getBlockEntity()
    {
        return new TileEntityHut();
    }
}