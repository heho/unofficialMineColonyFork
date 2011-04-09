package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockHutBuilder extends BlockHut {

	private EntityBuilder em;

	public BlockHutBuilder(int blockID, int _textureID) {
		super(blockID);
		textureID = _textureID;
		hutWidth = 5;
		hutHeight = 3;
		clearingRange = 4;
		halfWidth = hutWidth/2;
		workingRange = 40;
		em=null;
		// Sets the recipe be two planks horizontal to each other
		// CraftingManager.getInstance().addRecipe(new ItemStack(blockID, 1,0),
		// new Object[] { "##", Character.valueOf('#'), Block.dirt,});
	}


	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutBuilder.blockID, i,j,k, workingRange, 20, workingRange);
		if (chestPos != null)
			return false;

		return super.canPlaceBlockAt(world, i, j, k);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
	{
		// build house when clicked with stick
		ItemStack is = entityplayer.getCurrentEquippedItem();

		 if(is !=null && (is.getItem().shiftedIndex == mod_MineColony.scepterSteel.shiftedIndex))
		{
			// fence
			Vec3D chestPos = Vec3D.createVector(i, j, k);
			for(int dx = i-workingRange; dx<=i+workingRange;dx++)
				for(int dz=k-workingRange; dz<=k+workingRange;dz++)
				{
					int dy = findTopGround(world, dx, dz);
					int groundBlockId = world.getBlockId(dx, dy, dz);
					if(groundBlockId == Block.dirt.blockID ||
							groundBlockId == Block.grass.blockID ||
							groundBlockId == Block.gravel.blockID ||
							groundBlockId == Block.sand.blockID ||
							groundBlockId == Block.stone.blockID)
					{
						if(dx == i-workingRange || dx == i+workingRange ||
								dz == k-workingRange || dz == k+workingRange)
							world.setBlockWithNotify(dx,dy+1,dz, Block.fence.blockID);
					}
				}
		}
		else
			return super.blockActivated(world, i, j, k, entityplayer);

		return true;

	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		//world.setWorldTime(0);

		// Chest for stuff with 5 stone axes
		world.setBlockWithNotify(i, j, k, mod_MineColony.hutBuilder.blockID);
		TileEntityChest tileentitychest = (TileEntityChest) world
		.getBlockTileEntity(i, j, k);



		//tileentitychest.setInventorySlotContents(8, new ItemStack(mod_MineColony.scepterGold, 1));

		spawnWorker(world, i, j, k);
	}

	public void updateTick(World world, int i, int j, int k, Random random)
	{
		super.updateTick(world, i, j, k, random);

		if(getBuilderAround(world, i,j,k)==null)
		{
			if(em!=null)
				em.isDead = true;
			spawnWorker(world, i, j, k);
		}
	}

	public void spawnWorker(World world, int i, int j, int k)
	{
		em = (EntityBuilder)EntityList.createEntityInWorld("Builder", world);

		// scan for first free block near chest
		Vec3D spawnPoint = scanForBlockNearPoint(world, 0, i, j, k, 1, 0, 1);
		if(spawnPoint==null)
			spawnPoint = scanForBlockNearPoint(world, Block.snow.blockID, i, j, k, 1, 0, 1);

		if(spawnPoint!=null)
		{
			em.setPosition(spawnPoint.xCoord, spawnPoint.yCoord, spawnPoint.zCoord);
			em.setHomePosition(i, j, k);
			world.entityJoinedWorld(em);
		}

	}

	public void onBlockRemoval(World world, int i, int j, int k)
	{
		em = getBuilderAround(world, i,j,k);
		if(em!=null)
		{
			em.isDead=true;
		}
	}

	public EntityBuilder getBuilderAround(World world, int i, int j, int k)
	{
		List list = world.getEntitiesWithinAABB(EntityBuilder.class, this.getCollisionBoundingBoxFromPool(world, i, j, k).expand(workingRange, 20, workingRange));
		if (list != null) {
			for (int ii = 0; ii < list.size(); ii++) {
				if (list.get(ii) instanceof EntityBuilder) {
					return (EntityBuilder)list.get(ii);
				}
			}
		}

		return null;
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
