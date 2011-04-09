package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockHutBank extends BlockChanger {
	protected int hutWidth;
	protected int hutHeight;
	protected int clearingRange;
	protected int halfWidth;
	protected int workingRange;
	protected int textureID;
	
	public BlockHutBank(int blockID, int _textureID) {
		super(blockID);
		setTickOnLoad(true);
		textureID = _textureID;
		hutWidth = 6;
		hutHeight = 4;
		clearingRange = 4;
		halfWidth = hutWidth/2;
		workingRange = 60;
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
	

	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutBank.blockID, i,j,k, workingRange, 20, workingRange);
		if (chestPos != null)
			return false;

		return super.canPlaceBlockAt(world, i, j, k);
	}
	
	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
	{	
		// build house when clicked with stick
		ItemStack is = entityplayer.getCurrentEquippedItem();
		if(is !=null && is.getItem()!=null && (is.getItem().shiftedIndex == mod_MineColony.scepterGold.shiftedIndex))
		{
			// Build Bank

			// clean area around house
			for (int x = i - clearingRange; x <= i + clearingRange; x++)
				for (int z = k - clearingRange; z <= k + clearingRange; z++)
					for (int y = j; y < j + hutHeight; y++) {
						if(x!=i || y!=j || z!=k)
							world.setBlockWithNotify(x, y, z, 0);
					}

			// create empty box with stone
			for (int x = i - halfWidth; x <= i + halfWidth; x++)
				for (int z = k - halfWidth; z <= k + halfWidth; z++)
					for (int y = j - 1; y < j + hutHeight; y++) {

						if (x == i - halfWidth || x == i + halfWidth
								|| z == k - halfWidth || z == k + halfWidth) {
							// stone on sides
							world.setBlockWithNotify(x, y, z, Block.stone.blockID);
						} else if(x!=i || y!=j || z!=k){
							// air inside
							world.setBlockWithNotify(x, y, z, 0);
						}
					}

			// floor
			for (int x = i - halfWidth-1; x <= i + halfWidth+1; x++)
				for (int z = k - halfWidth-1; z <= k + halfWidth+1; z++) {
					if(x == i - halfWidth-1 || x == i + halfWidth+1 || z == k - halfWidth-1 || z == k + halfWidth+1)
					{
						world.setBlockWithNotify(x, j - 1, z, Block.cobblestone.blockID);
					}
					else
					{
						world.setBlockWithNotify(x, j - 1, z, Block.planks.blockID);
					}
				}
			
			// roof
			/*for (int x = i - halfWidth-1; x <= i + halfWidth+1; x++)
				for (int z = k - halfWidth-1; z <= k + halfWidth+1; z++) {
					world.setBlockWithNotify(x, j + hutHeight-1, z, Block.planks.blockID);
				}*/

			for (int dy = -1 ; dy <= 3; dy++)
				for (int x = i - halfWidth -1 +dy; x <= i + halfWidth-dy+1; x++)
					for (int z = k - halfWidth-1; z <= k + halfWidth+1; z++) {
						if(dy>=0 && x!=i-halfWidth-1+dy && x!=i+halfWidth-dy+1)
							world.setBlockWithNotify(x, j +hutHeight + dy, z, Block.cobblestone.blockID);
						else
						{
							if(x==i)
								world.setBlockWithNotify(x, j +hutHeight + dy, z, Block.cobblestone.blockID);
							else
							{
								world.setBlockWithNotify(x, j +hutHeight + dy, z, Block.stairCompactCobblestone.blockID);
								if(x>i)
									world.setBlockMetadataWithNotify(x, j +hutHeight + dy, z, 1);
							}
						}
					}

			// Door
			world.setBlockWithNotify(i, j, k - halfWidth, 0);
			world.setBlockWithNotify(i, j + 1, k - halfWidth,0);

			int i1 = 1;

			/*world.setBlockWithNotify(i, j, k - halfWidth,Block.doorWood.blockID);
			world.setBlockMetadataWithNotify(i, j, k, i1);
			world.setBlockWithNotify(i, j+1, k - halfWidth,Block.doorWood.blockID);
			world.setBlockMetadataWithNotify(i, j+1, k, i1);*/

			world.setBlockWithNotify(i+1, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i-1, j + 2, k - halfWidth,Block.planks.blockID);
			//world.setBlockWithNotify(i-2, j + 2, k - halfWidth,Block.planks.blockID);
			//world.setBlockWithNotify(i+2, j + 2, k - halfWidth,Block.planks.blockID);
			
			
			
		}
		else
			return super.blockActivated(world, i, j, k, entityplayer);
		
		return true;
		
	}
	
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);		
		//world.setWorldTime(0);
		world.setBlockWithNotify(i, j, k, mod_MineColony.hutBank.blockID);
		TileEntityChanger tileentitychanger = (TileEntityChanger) world
				.getBlockTileEntity(i, j, k);
		

	}
	
	
	public int getBlockTextureFromSide(int side)
    {
       if(side==1)
       {
          // Workshop top
          return textureID;
       }
        return blockIndexInTexture;
    }
	
	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
		if(l == 1)
        {
            return textureID;
        }
		else
			return super.getBlockTexture(iblockaccess, i, j, k, l);
    }
    
}
