package net.minecraft.src;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class EntityWorker extends EntityCreature {

	protected static final int actionGetEquipment = 0;
	protected static final int actionIdle = 1;
	protected static final int actionGetOutOfBuilding = 2;
	protected static final int actionDeliverGoods = 3;
	protected static final int actionGoBack = 5;
	protected int actionFreq = 0;
	protected boolean blockJumping = false;

	protected int ticksToDelivery = 100;
	protected int actualTicksToDelivery = 0;

	protected String signText1 = "";
	protected String signText2 = "";
	protected String signText3 = "";
	protected int previousSignX;
	protected int previousSignY;
	protected int previousSignZ;

	protected int initialXOffset = 0;
	protected int initialYOffset = 0;
	protected int initialZOffset = -6;

	protected int currentAction;
	protected int checkFreq = 1;
	protected int MaxCheckFreq = 8;
	protected int animFreq = 1;
	protected int maxAnimFreq = 24;
	protected int workingRange;
	protected PathEntity pathToEntity;
	protected double starringPointX;
	protected double starringPointY;
	protected double starringPointZ;
	protected Vec3D destPoint;
	protected RalphPathfinder pf;
	protected boolean starringPointSet;
	protected int stuckCount = 0;
	protected int strafingDir = 1;
	protected int strafingDirChange = 3;
	protected int freeRoamCount = 0;
	protected int roamingStuckLimit;
	boolean allowShortCuts=true;  // pathfinding will attempt short cuts

	protected ItemStack toolsList[];
	protected int currentlyEquipedTool;
	protected int currentToolEfficiency=12;
	protected List<ItemStack> inventory;

	public int homePosX;
	public int homePosY;
	public int homePosZ;
    boolean haveHomeChest=false;
    int findHome=0;

	public int iPosX;
	public int iPosY;
	public int iPosZ;

	World world;

	protected boolean isSwinging = false;
	protected float speed = 0;

	// For Humans+ Compatibility
	public boolean goodMob = true;
	public boolean peacefulMob = false;

	public void setHomePosition(double x, double y, double z) {
		homePosX = MathHelper.floor_double(x);
		homePosY = MathHelper.floor_double(y);
		homePosZ = MathHelper.floor_double(z);
		haveHomeChest=true;
	}
	public boolean isHomePosition(int x, int y, int z)
	{
		if (x != Math.floor(homePosX)) return false;
		if (y != Math.floor(homePosY)) return false;
		if (z != Math.floor(homePosZ)) return false;
		return true;
	}

	public ItemStack getHeldItem() {
		return defaultHoldItem;
	}

	protected ItemStack defaultHoldItem;

	public EntityWorker(World world) {
		super(world);
		workingRange = 20;
		starringPointSet = false;
		starringPointX = 0;
		starringPointY = 0;
		starringPointZ = 0;
		destPoint = null;
		toolsList = new ItemStack[2];
		toolsList[0] = null;
		toolsList[1] = null;
		currentlyEquipedTool = 0;
		roamingStuckLimit = 7;
		this.world=world;
		pf = new RalphPathfinder(world);
	}
	protected void onSwing()
	{

	}

	public void onUpdate() {
		if (homePosX == 0 && homePosY == 0 && homePosZ == 0) {
			Vec3D chestPos = scanForBlockNearEntity(mod_MineColony.hutBuilder.blockID,
					workingRange, workingRange, workingRange);
			if (chestPos != null) {
				setHomePosition(chestPos.xCoord, chestPos.yCoord,
						chestPos.zCoord);
				rotationPitch = defaultPitch;
			}
		}

		if (isSwinging)
			swingProgress = 1 - ((float) animFreq / maxAnimFreq);
		else
			swingProgress = 0;

		animFreq--;
		if(animFreq==0)
		{
			animFreq=maxAnimFreq;
			onSwing();
		}

		checkFreq--;
		if (checkFreq == 0) {
			// iPosX = (int)Math.round(posX);
			// iPosY = (int)Math.round(posY);
			// iPosZ = (int)Math.round(posZ);
			iPosX = MathHelper.floor_double(posX);
			iPosY = MathHelper.floor_double(posY);
			iPosZ = MathHelper.floor_double(posZ);

			cutWayThrough();

			if(!this.isDead)
			{
				EntityItem nearbyItem=gatherItemNearby(Item.sign.shiftedIndex);
				if(nearbyItem!=null) { onGetItem();nearbyItem.setEntityDead();}

				workerUpdate();
			}

			checkFreq = MaxCheckFreq;
		}

		if(destPoint!=null) // && !starringPointSet)
		{

			walkToTarget(destPoint);

		}


		if (starringPointSet) {
			facePoint(starringPointX, starringPointY, starringPointZ, 10);
		}

		super.onUpdate();
	}

	protected void workerUpdate() {
		// implement worker's task
	}

	protected void destroySign()
	{
		// destroy previous sign
		if(worldObj.getBlockId(previousSignX, previousSignY, previousSignZ)==Block.signPost.blockID)
			placeBlockAt(previousSignX, previousSignY, previousSignZ, 0);
	}

	protected void destroySignsInRange(int rx, int ry, int rz)
	{
		for (int i = iPosX - rx; i <= iPosX + rx; i++)
			for (int j = iPosY - ry; j <= iPosY + ry; j++)
				for (int k = iPosZ - rz; k <= iPosZ + rz; k++) {
					{
						if(worldObj.getBlockId(i, j, k)==Block.signPost.blockID)
						{
							TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(i,j,k);
							if(sign==null)
								continue;

							for(int cp = 0;cp<sign.signText.length;cp++)
							{
								String textNr = sign.signText[cp];

								if(textNr.startsWith("Out") ||
										textNr.startsWith("Sleeping") ||
										textNr.startsWith("No trees in range"))
								{
									placeBlockAt(i, j, k, 0);
									continue;
								}

							}

						}
					}
				}
	}


	String getSignText(int x, int y, int z, int lnNbr)
	{
		String answer="";
		if(worldObj.getBlockId(x, y, z)==Block.signPost.blockID)
		{
			TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(x,y,z);
			if(sign==null)
				return answer;

			if (lnNbr<sign.signText.length)
				answer=sign.signText[lnNbr];
		}
		return answer;
	}


	void destroyBuildSigns(int rx, int ry, int rz)
	{
		for (int i = iPosX - rx; i <= iPosX + rx; i++)
			for (int j = iPosY - ry; j <= iPosY + ry; j++)
				for (int k = iPosZ - rz; k <= iPosZ + rz; k++) {
					{
						if(worldObj.getBlockId(i, j, k)==Block.signPost.blockID)
						{
							TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(i,j,k);
							if(sign==null)
								continue;

							for(int cp = 0;cp<sign.signText.length;cp++)
							{
								String textNr = sign.signText[cp];
								if(textNr.toLowerCase().startsWith("build") )
								{
									placeBlockAt(i, j, k, 0);
									continue;
								}
							}

						}
					}
				}
	}


	protected TileEntitySign placeSign(int blockID, String t1,String t2,String t3) {
		// place sign only near blockId
		if(scanForBlockNearPoint(blockID, iPosX, iPosY, iPosZ, 10, 10, 10)==null)
			return null;

		Vec3D signVec = null;

		signVec = scanForSignPlace(iPosX, iPosY, iPosZ);

		if(signVec!=null)
		{
			int x = MathHelper.floor_double(signVec.xCoord);
			int y = MathHelper.floor_double(signVec.yCoord);
			int z = MathHelper.floor_double(signVec.zCoord);
			placeBlockAt(x,y,z, Block.signPost.blockID);
			TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(x,y,z);
			if(sign!=null)
			{
				sign.signText = new String[]{t1, t2, t3, ""};

				previousSignX = x;
				previousSignY = y;
				previousSignZ = z;
				return sign;
			}
		}

		return null;
	}


	private Vec3D scanForSignPlace(int x, int y, int z) {

		for (int i = x - 1; i <= x + 1; i++)
			for (int j = y - 1; j <= y + 1; j++)
				for (int k = z - 1; k <= z + 1; k++) {
					if(Block.signPost.canPlaceBlockAt(worldObj, i,j,k))
						return Vec3D.createVectorHelper(i, j, k);
				}

		return null;
	}


	protected void cutWayThrough() {
		// check if leaves are nearby and cut them into pieces
		Vec3D closestLeaves = scanForBlockNearEntity(Block.leaves.blockID, 1,
				2, 1);
		if (closestLeaves != null) {
			placeBlockAt(
					MathHelper.floor_double(closestLeaves.xCoord),
					MathHelper.floor_double(closestLeaves.yCoord),
					MathHelper.floor_double(closestLeaves.zCoord), 0);
		}
	}


	// scan for block type in the specified range
	protected Vec3D scanForBlockNearEntity(int blockId, int rx, int ry, int rz) {
		int x = iPosX;
		int y = iPosY;
		int z = iPosZ;

		return scanForBlockNearPoint(blockId, x, y, z, rx, ry, rz);
	}

	protected Vec3D scanForBlockNearPoint(int blockId, double x, double y, double z,
			int rx, int ry, int rz) {
		return scanForBlockNearPoint(blockId,
				MathHelper.floor_double(x),
				MathHelper.floor_double(y),
				MathHelper.floor_double(z),
				rx, ry, rz);
	}

	protected Vec3D scanForBlockNearPoint(int blockId, int x, int y, int z,
			int rx, int ry, int rz) {
		Vec3D entityVec = Vec3D.createVector(x, y, z);

		Vec3D closestVec = null;
		double minDistance = 999999999;

		for (int i = x - rx; i <= x + rx; i++)
			for (int j = y - ry; j <= y + ry; j++)
				for (int k = z - rz; k <= z + rz; k++) {
					if (worldObj.getBlockId(i, j, k) == blockId) {

						Vec3D tempVec = Vec3D.createVectorHelper(i, j, k);

						if (closestVec == null
								|| tempVec.distanceTo(entityVec) < minDistance) {
							closestVec = tempVec;
							minDistance = closestVec.distanceTo(entityVec);
						}
					}
				}

		if(minDistance<999999999)
			return closestVec;
		else
			return null;
	}

	protected Vec3D scanForFreeSpace(int baseBlockId, int x, int y, int z, int rx, int ry, int rz, int height) {
		for (int i = x - rx; i <= x + rx; i++)
			for (int j = y - ry; j <= y + ry; j++)
				for (int k = z - rz; k <= z + rz; k++) {
					if (worldObj.getBlockId(i, j, k) == baseBlockId)
					{
						boolean isFree = true;
						for(int h=height;h<=height;h++)
							if(worldObj.getBlockId(i, j+h, k) != 0 && worldObj.getBlockId(i, j+h, k)!=Block.snow.blockID)
							{
								isFree = false;
								break;
							}

						if(isFree)
							return Vec3D.createVectorHelper(i, j, k);
					}
				}

		return null;
	}


	public void facePoint(double px, double py, double pz, float f) {
		double dx = px - posX;
		double dz = pz - posZ;

		if (Math.abs(dx) > 10 || Math.abs(dz) > 10)
			return;

		float f1 = (float) ((Math.atan2(dz, dx) * 180D) / 3.1415927410125732D) - 90F;

		rotationYaw = updateRotation(rotationYaw, f1, f);

		double posEyes = iPosY + 1;
		float newRotationPitch = 0;

		if(py==posEyes)
			newRotationPitch = 0;
		else if(py-1==posEyes)
			newRotationPitch = -25;
		else if(py-1>posEyes)
			newRotationPitch = -45;
		else if(py+1==posEyes)
			newRotationPitch = 25;
		else if(py+1<posEyes)
			newRotationPitch = 45;

		if(newRotationPitch>rotationPitch)
			rotationPitch += 5;
		else if(newRotationPitch<rotationPitch)
			rotationPitch -= 5;

		if (rotationPitch > 45)
			rotationPitch = 45;

		if (rotationPitch < -45)
			rotationPitch = -45;
	}

	protected float updateRotation(float f, float f1, float f2) {
		float f3;
		for (f3 = f1 - f; f3 < -180F; f3 += 360F) {
		}
		for (; f3 >= 180F; f3 -= 360F) {
		}
		if (f3 > f2) {
			f3 = f2;
		}
		if (f3 < -f2) {
			f3 = -f2;
		}
		return f + f3;
	}


	protected void equipItemFromChest(TileEntityChest tileentitychest, int itemId, int i, int toolSlot) {

		ItemStack itemStack = null;
		itemStack = getItemFromChest(tileentitychest, itemId, i);

		if(itemStack!=null)
			equipItem(itemStack, toolSlot);
	}

	protected boolean equipItem(int toolSlot) {
		if(toolsList[toolSlot]!=null)
		{
			defaultHoldItem = toolsList[toolSlot].copy();
			return true;
		}
		else return false;
	}

	protected void equipItem(ItemStack item, int toolSlot) {
		if(item!=null)
		{
			defaultHoldItem = item.copy();
			toolsList[toolSlot] = item.copy();
		}
	}


	int countItemInInventory(ItemStack is)
	{
		int total=0;
		for (int i=0; i<inventory.size(); i++)
		{
			ItemStack itm= inventory.get(i);
			if (itm.isItemEqual(is))
				total+=itm.stackSize;
		}
		return total;
	}


	boolean addItemToInventory(ItemStack is,int cnt)
	{
		for (int i=0; i<inventory.size(); i++)
		{
			ItemStack itm= inventory.get(i);
			if (itm.itemID==is.itemID
					 && itm.stackSize+cnt<itm.getMaxStackSize())
			{
			itm.stackSize+=cnt;
			return true;
			}
		}
		inventory.add(is);

		return true;
	}


	int removeItemInInventory(ItemStack is, int cnt)
	{
		int cntRemoved=0;
		for (int i=0; i<inventory.size(); i++)
		{
			ItemStack itm= inventory.get(i);
			if (itm.itemID==is.itemID )
			{
				if (itm.stackSize>=cnt)
				{
					itm.stackSize-=cnt;
					cntRemoved+=cnt;
					if (itm.stackSize==0)
						inventory.remove(i);

					return cntRemoved;
				}

				cntRemoved+=itm.stackSize;
				inventory.remove(i);
				i=-1;
			}
		}
		return cntRemoved;
	}


	boolean doesChestContainItem(TileEntityChest chest, int itemID, int cnt)
	{
		if(chest==null)
			return false;

		int slotIndex = 0;
		ItemStack slot = null;
		while((slot=chest.getStackInSlot(slotIndex))== null || slot.itemID != itemID)
		{
			slotIndex++;
			if(slotIndex>=chest.getSizeInventory()-1)
			{
				return false;
			}
		}

		if(slot!=null && slot.stackSize>0) {
			return true;
		}
		return false;
	}


	boolean chestCanHold(TileEntityChest chest, Class itemClass, int cnt)
	{
		if(chest==null)
			return false;

		int slotIndex = 0;
		ItemStack slot = null;
		while((slot=chest.getStackInSlot(slotIndex))== null || slot.getItem().getClass() != itemClass)
		{
			slotIndex++;
			if(slotIndex>=chest.getSizeInventory()-1)
			{
				return false;
			}
		}

		if(slot!=null && slot.stackSize+cnt<slot.getMaxStackSize()) {
			return true;
		}
		return false;
	}



	protected ItemStack getItemFromChest(TileEntityChest chest, Class itemClass, int i) {
		if(chest==null)
			return null;

		int slotIndex = 0;
		ItemStack slot = null;
		while((slot=chest.getStackInSlot(slotIndex))== null || slot.getItem().getClass() != itemClass)
		{
			slotIndex++;
			if(slotIndex>=chest.getSizeInventory()-1)
			{
				slot = null;
				break;
			}
		}

		if(slot!=null && slot.stackSize>0) {
			int howManyItems = i;
			if(i>slot.stackSize)
				howManyItems = slot.stackSize;
			slot = chest.decrStackSize(slotIndex, howManyItems);
			return new ItemStack(slot.getItem().shiftedIndex, howManyItems,0);
		}
		return null;
	}



	protected ItemStack getItemFromChest(TileEntityChest chest, int itemId, int i) {
		if(chest==null)
			return null;

		int slotIndex = 0;
		ItemStack slot = null;
		while((slot=chest.getStackInSlot(slotIndex))== null || slot.itemID != itemId)
		{
			slotIndex++;
			if(slotIndex>=chest.getSizeInventory()-1)
			{
				slot = null;
				break;
			}
		}

		if(slot!=null && slot.stackSize>0) {
			int howManyItems = i;
			if(i>slot.stackSize)
				howManyItems = slot.stackSize;
			slot = chest.decrStackSize(slotIndex, howManyItems);
			if(slot.stackSize==0)
				slot = null;

			return new ItemStack(itemId, howManyItems,0);
		}
		return null;
	}

	protected boolean putItemIntoChest(TileEntityChest chest, int itemId, int i) {
		int slotIndex = 0;
		ItemStack slot = null;
       // System.out.println("putting " + i + " items into chest");
		while((slot=chest.getStackInSlot(slotIndex))!= null)
		{
			if(slot.itemID == itemId && slot.stackSize+i<=slot.getMaxStackSize())
				break;

			slotIndex++;
			if(slotIndex>=chest.getSizeInventory())
				return false;  // was break
		}


		if((slot!=null && slot.stackSize+i<=slot.getMaxStackSize())) {
			chest.setInventorySlotContents(slotIndex, new ItemStack(itemId, slot.stackSize+i,0));
			return true;
		}
		else if(slot==null)
		{
			chest.setInventorySlotContents(slotIndex, new ItemStack(itemId, i,0));
			return true;
		}
		return false;
	}

	protected EntityItem gatherItemNearby(int blockID) {
		List list = worldObj.getEntitiesWithinAABBExcludingEntity(this,
				boundingBox.expand(3.0D, 3.0D, 3.0D));
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getClass() == EntityItem.class) {
					EntityItem entity = (EntityItem) list.get(i);

					if (!entity.isDead && entity.item.itemID == blockID) {
						return entity;
					}
				}
			}
		}
		return null;
	}



	protected EntityItem gatherItemNearby(Class entityType) {
		List list = worldObj.getEntitiesWithinAABBExcludingEntity(this,
				boundingBox.expand(1.0D, 0.0D, 1.0D));
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getClass() == EntityItem.class) {
					EntityItem entity = (EntityItem) list.get(i);

					if (!entity.isDead && entity.item!=null && entity.item.stackSize>0 && entity.item.getItem().getClass() == entityType) {
						return entity;
					}
				}
			}
		}
		return null;
	}

	protected boolean toolWeariness(ItemStack inventoryTool, int wear) {
		if(toolsList[currentlyEquipedTool]!=null)
		{
			//toolsList[currentlyEquipedTool].damageItem(wear);
			toolsList[currentlyEquipedTool].func_25190_a(wear, ModLoader.getMinecraftInstance().thePlayer);
			if(toolsList[currentlyEquipedTool].stackSize==0)
			{
				isSwinging = false;
				rotationPitch = defaultPitch;
				toolsList[currentlyEquipedTool] = null;
				defaultHoldItem = null;
				starringPointSet = false;
				return true;
			}
		}
		return false;
	}

	protected boolean isNumber(String in)
	{
		try
		{
			Integer.parseInt(in);
		}
		catch(NumberFormatException ex)
		{
			return false;
		}
		return true;
	}


	boolean signAt(int x, int y, int z)
	{
		return (worldObj.getBlockId(x, y, z) == Block.signPost.blockID ||
				worldObj.getBlockId(x, y, z) == Block.signWall.blockID );
		//  || 				worldObj.getBlockTileEntity( x, y,  z) instanceof TileEntitySign);
	}



	// searches for a sign that reads Build Here
	Vec3D scanForNextBuildSign(int rx, int ry, int rz) {

		for (int i = iPosX - rx; i <= iPosX + rx; i++)
			for (int j = iPosY - ry; j <= iPosY + ry; j++)
				for (int k = iPosZ - rz; k <= iPosZ + rz; k++) {
					if (worldObj.getBlockId(i, j, k) == Block.signPost.blockID ||
							worldObj.getBlockId(i, j, k) == Block.signWall.blockID) {

						TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(i,j,k);
						if(sign==null)
							continue;


						int cp=0;
						String textNr = sign.signText[cp];
						//	System.out.println(textNr + cp + " " + textNr.length());
						if (textNr.toLowerCase().startsWith("build"))
							return Vec3D.createVectorHelper(i, j, k);

					}
				}

		return null;
	}

boolean pointIsDoor(PathPoint p)
{
	return (worldObj.getBlockId(p.xCoord, p.yCoord, p.zCoord)==Block.doorWood.blockID);
}
void openDoorForX(PathPoint p)
{

}
void openDoorForZ(PathPoint p)
{

}
void openDoorBetween(PathPoint pt0, PathPoint pt1)
{
	if (pt0.xCoord!=pt1.xCoord)
	{
		if (pointIsDoor(pt1)) openDoorForX(pt1);
		if (pointIsDoor(pt0)) openDoorForX(pt0);
	}
	else
	{
		if (pointIsDoor(pt1)) openDoorForZ(pt1);
		if (pointIsDoor(pt0)) openDoorForZ(pt0);
	}
}



boolean atEndOfPath()
{
	if (rpe==null) return true;
	return (rpeIdx==rpe.pathLength-1);
}
// move forward is variable used by workers
// speed is whats given to minecraft.entity
Vec3D pathDest;

public int rpeIdx=-1;
void pathFindAndMove(Vec3D vec3d)
{
	double mySpeed=1;
	if (speed==0)
		return;

	Vec3D moveToVec;

	iPosX = (int) Math.floor(posX);
	iPosY = (int) Math.floor(posY);
	iPosZ = (int) Math.floor(posZ);

	//System.out.println("my pos is " + posX + " " + posY + " " + posZ );
	// System.out.println("my ipos is " + iPosX + " " + iPosY + " " + iPosZ );
	//System.out.println("eventual dest is " + vec3d.toString());
	if (rpe!=null)
	{
		rpeIdx = rpe.indexOf( iPosX, pf.findGroundLevel(iPosX, iPosY, iPosZ), iPosZ);
	}

	if (rpeIdx<0)
		System.out.print("");
	if (rpeIdx<0 ||  (pathDest!=null &&  pathDest.distanceTo(vec3d)>.5))
	{

		//pf.debug=true;
		rpe = pf.createEntityPathTo(this, iPosX, iPosY, iPosZ, (int) vec3d.xCoord, (int) vec3d.yCoord, (int) vec3d.zCoord, 100);
		//System.out.println("creating path");
		pathDest = Vec3D.createVectorHelper(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);

		if (rpe!=null)
		{
			rpeIdx = rpe.indexOf(iPosX, pf.findGroundLevel(iPosX, iPosY, iPosZ), iPosZ);
	     //  System.out.println("rpeIdx = " + rpeIdx + " path size="  + rpe.points.length);
	    }
 		if (rpeIdx<0)
			rpeIdx=0;

 		if (rpe!=null)
 			{
 	//		System.out.println("newpath");
 	//		for (int i=rpeIdx;i<rpe.points.length; i++)
 		//		System.out.println(rpe.points[i].xCoord + " " + rpe.points[i].yCoord + " " + rpe.points[i].zCoord + " ");
 			}
	}


	if (rpe!=null && rpeIdx>=rpe.points.length-1)
	{
		speed=0;
		moveForward=0;
		// System.out.println(vec3d + " and " + pathDest);
	return;
	}

	moveToVec=Vec3D.createVector(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
	if (rpe!=null)
	{
		isJumping=false;
		if (worldObj.getBlockId(iPosX, iPosY, iPosZ)==Block.doorWood.blockID)
			Block.doorWood.blockActivated(worldObj, iPosX, iPosY, iPosZ, null);

		if (rpe.points.length>rpeIdx+1)
		{
			PathPoint pt1 = rpe.points[rpeIdx+1];
			PathPoint pt0 = rpe.points[rpeIdx];

			if (pointIsDoor(pt1) ||  pointIsDoor(pt0))
				openDoorBetween(pt0, pt1);

			moveToVec=Vec3D.createVector(rpe.points[rpeIdx+1].xCoord,rpe.points[rpeIdx+1].yCoord,rpe.points[rpeIdx+1].zCoord) ;
			mySpeed=.5f;

			if (pt1.yCoord>iPosY || worldObj.getBlockId(pt1.xCoord, pt1.yCoord, pt1.zCoord)==Block.doorWood.blockID)
				isJumping=true;

			if (allowShortCuts &&  rpe.points.length>rpeIdx+2)
			{
				PathPoint pt2 = rpe.points[rpeIdx+2];

				//System.out.println(rpe.points[rpeIdx+2]);

				moveToVec=Vec3D.createVector(rpe.points[rpeIdx+2].xCoord,rpe.points[rpeIdx+2].yCoord,rpe.points[rpeIdx+2].zCoord) ;
				//				System.out.println(" my pos is " + iPosX + " " + iPosY + " " + iPosZ + " walking to " + vec3d);
				if (pt2.yCoord>iPosY || worldObj.getBlockId(pt2.xCoord, pt2.yCoord, pt2.zCoord)==Block.doorWood.blockID)
					isJumping=true;

				mySpeed=1f;

				//System.out.println("my y = " + iPosY + "pt1 y =" + pt1.yCoord + " pt2 y =" + pt2.yCoord);
			}

		}
	}
	// 0 is east    towards gate
	// 90 is south  towards (lumberjacks)
	// 180 is west
	// 270 north
	if (moveToVec != null) {
		moveToVec.xCoord+=.5;  // was -
		moveToVec.zCoord+=.5;
		//System.out.println("moveforward = " + moveForward + " dest is " + vec3d.toString());

		double dx =dx = moveToVec.xCoord - posX;
		double dz = moveToVec.zCoord - posZ;
		double dy = moveToVec.yCoord - MathHelper.floor_double(boundingBox.minY);

		float f4 = (float) ((Math.atan2(dz, dx) * 180D) / 3.1415927410125732D)  - 90F;
		//System.out.println("dz=" + dz + " dx = " + dx  + " f4=" + f4);

		float f5 = f4 - rotationYaw;
		if (speed<mySpeed)  // don't go faster than what they ask for
			mySpeed=speed;
		for (; f5 < -180F; f5 += 360F)
		{
		}
		for (; f5 >= 180F; f5 -= 360F)
		{
		}
		for (; rotationYaw < -180F; rotationYaw += 360F)
		{
		}
		for (; rotationYaw >= 180F; rotationYaw -= 360F)
		{
		}
		//System.out.println("f4=" + f4 + " rotationYaw = " + rotationYaw + " f5=" + f5);
		if (f5 > 30F)
		{
			f5 = 30F;
		}
		if (f5 < -30F)
		{
			f5 = -30F;
		}

		rotationYaw += f5;

		//if (isJumping) System.out.println("isjumping=true");
		//System.out.println("moveforward = " + moveForward + " dest is " + vec3d.toString() + " direction is " + rotationYaw);
		speed=moveForward=(float) mySpeed;

		boolean flag1 = handleWaterMovement();
		boolean flag2 = handleLavaMovement();
		if (flag1 || flag2)
		{
			isJumping = true;
		}


	}
}


    protected void walkToTarget(Vec3D vec3d)
    {
			pathFindAndMove(vec3d);
    }


	protected void walkToTargetStraight(Vec3D vec3d) {
		if(vec3d == null)
			return;



		isJumping = false;

		// if is close to destination then slow down
		// or if there are many block around then also slow down

		if(destPoint!=null && destPoint.distanceTo(Vec3D.createVector(posX, posY, posZ))<2)
		{
			speed = (float) 0.5;
		}

		if(countBlocksAround(iPosX, iPosY, iPosZ, 1,1,1)>12)
			speed = (float) 0.5;

		if(stuckCount>8)
		{
			stuckCount = 0;
		}

		// check if entity is moving
		Vec3D prevPos = Vec3D.createVector(prevPosX, posY, prevPosZ);
		double distanceWalked = prevPos.squareDistanceTo(posX, posY, posZ);
		if (distanceWalked>=0 && distanceWalked < 0.0001)
			stuckCount++;
		// else
		// stuckCount = 0;


		if(!blockJumping && stuckCount<10 && worldObj.getBlockId(iPosX, iPosY+2, iPosZ)==0 && (scanForBlockNearPoint(Block.dirt.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.stone.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.sand.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.sandStone.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.grass.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.cobblestone.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(mod_MineColony.hutWarehouse.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(mod_MineColony.hutMiner.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(mod_MineColony.hutLumberjack.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(mod_MineColony.hutFarmer.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.tilledField.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.gravel.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null))
			isJumping = true;

		// strafe
		Vec3D normVec = vec3d.normalize();
		int nx = MathHelper.floor_double(normVec.xCoord);
		int nz = MathHelper.floor_double(normVec.zCoord);
		moveStrafing *= 0.85;
		if(stuckCount>3 && (scanForBlockNearPoint(Block.dirt.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.stone.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.sand.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.sandStone.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.grass.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.wood.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.planks.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.stairCompactPlanks.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.gravel.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.fence.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.cobblestone.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null))
		{
			if(strafingDirChange>rand.nextInt(20)+10)
			{
				strafingDir = -strafingDir;
				strafingDirChange=0;
			}
			strafingDirChange++;
			moveStrafing = strafingDir*3;
		}
		// if stuck then seek for free space around and go for it

		if(stuckCount>roamingStuckLimit && freeRoamCount<100)
		{
			speed = 1;
			blockJumping = false;
			freeRoamCount++;

			int rx = rand.nextInt(2);
			int rz = rand.nextInt(2);
			vec3d = scanForFreeSpace(0, iPosX, iPosY, iPosZ, rx, 0, rz, 2);
		}
		if(freeRoamCount>=100)
		{
			freeRoamCount = 0;
			stuckCount = 0;
		}

		if(!isInHouse())
		{
			// place dirt to stay on something
			if(stuckCount>9 && (worldObj.getBlockId(iPosX,iPosY-1,iPosZ) == 0 ||
					worldObj.getBlockId(iPosX,iPosY-1,iPosZ) == Block.waterMoving.blockID ||
					worldObj.getBlockId(iPosX,iPosY-1,iPosZ) == Block.waterStill.blockID ||
					worldObj.getBlockId(iPosX,iPosY-1,iPosZ) == Block.snow.blockID))
			{
				//stuckCount = 0;
				if(isNaturalBlock(iPosX,iPosY-1,iPosZ))
					placeBlockAt(iPosX,iPosY-1,iPosZ, Block.dirt.blockID);
			}

			// dig upwards if not a human placed block
			if(stuckCount>5 && isNaturalBlock(iPosX,iPosY+2,iPosZ) && worldObj.getBlockId(iPosX,iPosY+2,iPosZ) != 0)
			{
				//stuckCount = 0;
				placeBlockAt(iPosX,iPosY+2,iPosZ, 0);
			}
		}

		double d = width;

		if(vec3d != null && vec3d.squareDistanceTo(posX, posY, posZ) < d * d * 2)
		{
			return;
		}

		if (vec3d != null) {
			double dx = vec3d.xCoord - posX;
			double dz = vec3d.zCoord - posZ;
			double dy = vec3d.yCoord - MathHelper.floor_double(boundingBox.minY);
			float f4 = (float) ((Math.atan2(dz, dx) * 180D) / 3.1415927410125732D) - 90F;
			float f5 = f4 - rotationYaw;
			moveForward = speed;
			for (; f5 < -180F; f5 += 360F) {
			}
			for (; f5 >= 180F; f5 -= 360F) {
			}
			if (f5 > 30F) {
				f5 = 30F;
			}
			if (f5 < -30F) {
				f5 = -30F;
			}

			rotationYaw += f5;

			boolean flag1 = handleWaterMovement();
			boolean flag2 = handleLavaMovement();
			if (flag1 || flag2) {
				isJumping = true;
			}


		}
		vec3d = null;
	}

	protected boolean isNaturalBlock(int x, int y, int z) {

		int blockId = worldObj.getBlockId(x,y,z);
		if( blockId == 0 ||
				blockId == Block.dirt.blockID ||
				blockId == Block.grass.blockID ||
				blockId == Block.stone.blockID ||
				blockId == Block.gravel.blockID ||
				blockId == Block.sand.blockID ||
				blockId == Block.wood.blockID ||
				blockId == Block.oreCoal.blockID ||
				blockId == Block.oreDiamond.blockID ||
				blockId == Block.oreGold.blockID ||
				blockId == Block.oreIron.blockID ||
				blockId == Block.oreRedstone.blockID ||
				blockId == Block.oreRedstoneGlowing.blockID ||
				//blockId == Block.blockIce.blockID ||
				blockId == Block.blockSnow.blockID ||
				blockId == Block.pumpkin.blockID
		)
			return true;

		return false;
	}

	protected boolean isInHouse() {
		if(iPosX>=homePosX-8 && iPosX<=homePosX+8 &&
				iPosY>=homePosY-1 && iPosY<=homePosY+7 &&
				iPosZ>=homePosZ-8 && iPosZ<=homePosZ+8)
			return true;

		return false;
	}



	protected int countBlocksAround(int x, int y, int z, int rx, int ry, int rz) {
		int counter = 0;

		for (int i = x - rx; i <= x + rx; i++)
			for (int j = y - ry; j <= y + ry; j++)
				for (int k = z - rz; k <= z + rz; k++) {
					//if (worldObj.getBlockId(i, j, k) != 0 && worldObj.getBlockId(i, j, k) != Block.snow.blockID)
					if (worldObj.isBlockOpaqueCube(i, j, k))
						counter++;
				}

		return counter;

	}

	protected void updatePlayerActionState() {
		// needs to be overridden

	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);

		NBTTagList inventoryList = new NBTTagList();

		if(toolsList[0]!=null)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			toolsList[0].writeToNBT(tagCompound);
			inventoryList.setTag(tagCompound);
		}

		if(toolsList[1]!=null)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			toolsList[1].writeToNBT(tagCompound);
			inventoryList.setTag(tagCompound);
		}

		nbttagcompound.setTag("Inventory", inventoryList);
		nbttagcompound.setInteger("currentAction", currentAction);
		nbttagcompound.setInteger("homePosX", homePosX);
		nbttagcompound.setInteger("homePosY", homePosY);
		nbttagcompound.setInteger("homePosZ", homePosZ);

		nbttagcompound.setInteger("previousSignX", previousSignX);
		nbttagcompound.setInteger("previousSignY", previousSignY);
		nbttagcompound.setInteger("previousSignZ", previousSignZ);

		nbttagcompound.setInteger("currentlyEquipedTool", currentlyEquipedTool);
		nbttagcompound.setInteger("currentToolEfficiency", currentToolEfficiency);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("Inventory");

		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound tagCompound = (NBTTagCompound)nbttaglist.tagAt(i);

			ItemStack itemstack = new ItemStack(tagCompound);

			if(itemstack.getItem() == null)
			{
				continue;
			}

			toolsList[i] = itemstack;
			defaultHoldItem = toolsList[i].copy();
		}
		currentAction = nbttagcompound.getInteger("currentAction");

		homePosX = nbttagcompound.getInteger("homePosX");
		homePosY = nbttagcompound.getInteger("homePosY");
		homePosZ = nbttagcompound.getInteger("homePosZ");

		previousSignX = nbttagcompound.getInteger("previousSignX");
		previousSignY = nbttagcompound.getInteger("previousSignY");
		previousSignZ = nbttagcompound.getInteger("previousSignZ");

		currentlyEquipedTool = nbttagcompound.getInteger("currentlyEquipedTool");
		currentToolEfficiency = nbttagcompound.getInteger("currentToolEfficiency");
	}

	protected int getDropItemId() {
		return Item.axeStone.shiftedIndex;
	}

	protected boolean placeBlockAt(int x, int y, int z, int blockId)
	{
		if(x < 0xfe17b800 || z < 0xfe17b800 || x >= 0x1e84800 || z > 0x1e84800)
		{
			return false;
		}
		if(y < 0)
		{
			return false;
		}
		if(y >= 128)
		{
			return false;
		}

		worldObj.setBlockWithNotify(x, y, z, blockId);
		return true;
	}


	protected int findTopGround(int x, int z) {
		int ySolid = 127;
		int blockId = worldObj.getBlockId(x, ySolid, z);
		while ( (blockId==0 || blockId==Block.leaves.blockID ||
				blockId==Block.wood.blockID ||
				blockId==Block.cactus.blockID ||
				blockId==Block.crops.blockID ||
				blockId==Block.fence.blockID ||
				blockId==Block.fire.blockID ||
				blockId==Block.cobblestone.blockID ||
				blockId==Block.planks.blockID ||
				blockId==Block.brick.blockID) && ySolid>0)
		{
			ySolid--;
			blockId = worldObj.getBlockId(x, ySolid, z);
		}
		return ySolid+1;
	}


	protected int findTopGround2(int x, int z, int maxy) {
		int ySolid = maxy;
		int blockId = worldObj.getBlockId(x, ySolid, z);
		while(blockId==0 || blockId==Block.leaves.blockID ||
				blockId==Block.wood.blockID ||
				blockId==Block.cactus.blockID ||
				blockId==Block.crops.blockID ||
				blockId==Block.fence.blockID ||
				blockId==Block.fire.blockID ||
				blockId==Block.cobblestone.blockID ||
				blockId==Block.planks.blockID ||
				blockId==Block.brick.blockID)
		{
			ySolid--;
			blockId = worldObj.getBlockId(x, ySolid, z);
		}
		return ySolid+1;
	}


	protected void onGetItem()
	{
		//worldObj.playSoundAtEntity(this, "random.pop", 0.4F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
	}

	RalphPathEntity rpe;
}