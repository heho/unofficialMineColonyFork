package net.minecraft.src;


// at 0 look at all chests for resources to take, and places to put back stuff
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.Renderer;

import net.minecraft.client.Minecraft;

public class EntityDeliveryMan extends EntityWorker {
	private static final int actionFindNextCheckpoint = 30;
	private static final int actionGoToNextCheckpoint = 31;
	private static final int actionDeliverResources = 32;

	private static final String badRouteName="badroutename";

	private int currentCheckPoint;
	private boolean goBack;
	private String routeName=badRouteName;
    boolean firstTime=true;



	public EntityDeliveryMan(World world) {
		super(world);
		texture = "/mob/deliveryman.png";
		//texture = "/mob/char.png";
		setSize(0.7F, .9F);
		defaultHoldItem = null;
		currentAction = actionFindNextCheckpoint;
		currentCheckPoint = 0;
		destPoint = null;
		inventory = new ArrayList<ItemStack>();
		workingRange = 40;
		roamingStuckLimit = 12;
		goBack = false;
	}



	public boolean findRouteName()
	{
		int rxz=5;
		// look for closest sign
		// if first line is not a number, then use that as our route name
		for (int d = 0;d<rxz; d++)
		{
			//  scan along max x
			for (int y=-d;y<=d; y++)
				for (int z=-d;z<=d; z++)
				{
					if (signAt(homePosX + d, homePosY + y, homePosZ+ z))
					{
						TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(homePosX + d, homePosY + y, homePosZ+ z);
						if(sign==null)
							continue;

						try {
						String s=  sign.signText[0];
						Integer.parseInt(s);
						}
						catch (Exception err)
						{
							routeName = sign.signText[0];
							return true;
						}
					}
				}
			//  scan along min x
			for (int y=-d;y<=d; y++)
				for (int z=-d;z<=d; z++)
				{
					if (signAt(homePosX - d, homePosY + y, homePosZ+ z))
					{
						TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(homePosX - d, homePosY + y, homePosZ+ z);
						if(sign==null)
							continue;

						try {
						String s=  sign.signText[0];
						Integer.parseInt(s);
						}
						catch (Exception err)
						{
							routeName = sign.signText[0];
							return true;
						}
					}
				}
			//  scan along max z
			for (int y=-d;y<=d; y++)
				for (int x=-d;x<=d; x++)
				{
					if (signAt(homePosX + x, homePosY + y, homePosZ+ d))
					{
						TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(homePosX + x, homePosY + y, homePosZ+ d);
						if(sign==null)
							continue;

						try {
						String s=  sign.signText[0];
						Integer.parseInt(s);
						}
						catch (Exception err)
						{
							routeName = sign.signText[0];
							return true;
						}
					}
				}
			//  scan along min z
			for (int y=-d;y<=d; y++)
				for (int x=-d;x<=d; x++)
				{
					if (signAt(homePosX + x, homePosY + y, homePosZ- d))
					{
						TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(homePosX + x, homePosY + y, homePosZ- d);
						if(sign==null)
							continue;

						try {
						String s=  sign.signText[0];
						Integer.parseInt(s);
						}
						catch (Exception err)
						{
							routeName = sign.signText[0];
							return true;
						}
					}
				}

			//  scan along max y
			for (int x=-d;x<=d; x++)
				for (int z=-d;z<=d; z++)
				{
					if (signAt(homePosX + x, homePosY + d, homePosZ+ z))
					{
						TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(homePosX + x, homePosY + d, homePosZ+ z);
						if(sign==null)
							continue;

						try {
						String s=  sign.signText[0];
						Integer.parseInt(s);
						}
						catch (Exception err)
						{
							routeName = sign.signText[0];
							return true;
						}
					}
				}
			//  scan along min y
			for (int x=-d;x<=d; x++)
				for (int z=-d;z<=d; z++)
				{
					if (signAt(homePosX + x, homePosY - d, homePosZ+ z))
					{
						TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(homePosX + x, homePosY - d, homePosZ+ z);
						if(sign==null)
							continue;

						try {
						String s=  sign.signText[0];
						Integer.parseInt(s);
						}
						catch (Exception err)
						{
							routeName = sign.signText[0];
							return true;
						}
					}
				}

		}
		return false;
	}
	public void onUpdate() {
		super.onUpdate();
		fallDistance = 0.0F;
		if(motionY < -0.3999999999999999D)
		{
			motionY = -0.3999999999999999D;
		}
	}

	protected void workerUpdate() {
		if (firstTime)
		{
			firstTime=false;
			findRouteName();
			if (routeName.equals(badRouteName))
				routeName="";
			System.out.println("routename found is " + routeName);
		}

		if (!haveHomeChest)
		{
			if (world.getBlockTileEntity(homePosX, homePosY, homePosZ) instanceof TileEntityWarehouse)
			{
			TileEntityWarehouse tew= (TileEntityWarehouse) world.getBlockTileEntity(homePosX, homePosY, homePosZ);

			if (tew!=null)
			{
				haveHomeChest=true;
				tew.workerCheckin(this);
			}
			else
			{
			findHome++;
			if (findHome>5) isDead=true;
			}
			}
			else
			{
				findHome++;
				if (findHome>5) isDead=true;
			}

		}

		blockJumping = false;

		destroySign();
		if(signText1 != "" || signText2 != ""|| signText3 != "")
		{
			placeSign(mod_MineColony.hutWarehouse.blockID, signText1, signText2, signText3);
			speed=moveForward = 0;
			moveStrafing = 0;
			isJumping = false;
		}
		else
			destroySignsInRange(3,3,3);

		signText1 = "";
		signText2 = "";
		signText3 = "";

		// make a path?
		if (Math.random()>.8 && worldObj.getBlockId(iPosX, iPosY-1, iPosZ)==Block.grass.blockID)
		{
			placeBlockAt(iPosX, iPosY-1, iPosZ, Block.dirt.blockID);
		}

/*
		if (destPoint!=null)
		{
		System.out.println("moving towards " + destPoint.toString());
		if (rpe!=null)
			{
			System.out.println("at pos " + rpeIdx +  " of " + rpe.pathLength + " in path");
			for (int ii=0; ii<rpe.pathLength; ii++)
				System.out.println(rpe.points[ii].toString());
			if (rpeIdx>=rpe.pathLength)
				System.out.println("");
			}
		}
		*/

		speed = (float)1.0;
		if(destPoint==null)
			currentAction = actionFindNextCheckpoint;
		switch (currentAction) {
		case actionFindNextCheckpoint:
			if(currentCheckPoint<0)
			{
				//currentCheckPoint = 0;
				goBack = false;
			}

			destPoint = null;
			isSwinging = false;

			if(!worldObj.isDaytime() && currentCheckPoint==0 && goBack == false)
			{
				signText3 = "Sleeping";
				break;
			}

			// search for next checkpoint sign near
			Vec3D nextCP = scanForNextCheckpoint(workingRange, 30, workingRange, currentCheckPoint, routeName,  goBack);
			if (nextCP==null)
			{
				goBack=!goBack;
				nextCP = scanForNextCheckpoint(workingRange, 30, workingRange, currentCheckPoint, routeName,  goBack);
			}

			//if (nextCP!=null)
				//System.out.println("CP: " + currentCheckPoint + ")" + nextCP.toString());
			if(nextCP!=null)
			{

				//pathToEntity = findPathToXYZ(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);
				//if(pathToEntity!=null && pathToEntity.pathLength>2)
				//{

				destPoint = Vec3D.createVectorHelper(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);

				currentAction = actionGoToNextCheckpoint;
				stuckCount=0;

				//}
				//else
				//walkToTargetStraight(nextCP);
			}
			else
			{

				isDead=true;

				// nextCP = scanForNextCheckpoint(workingRange, 30, workingRange, 0);
				// if(nextCP!=null)
				// {
				//
				// //pathToEntity = findPathToXYZ(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);
				// //if(pathToEntity!=null && pathToEntity.pathLength>1)
				// //{
				// currentCheckPoint = 0;
				// destPoint = Vec3D.createVectorHelper(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);
				// if(destPoint!=null)
				// currentAction = actionGoToNextCheckpoint;
				// //}
				// //else
				// //walkToTargetStraight(nextCP);
				// }
			}

			break;
		case actionGoToNextCheckpoint:
			// allow 2 Y points grace
            Vec3D entVec = Vec3D.createVector(posX, posY, posZ);

			if (Math.abs(posY-destPoint.yCoord)<=6)
				entVec=Vec3D.createVector(posX, destPoint.yCoord, posZ);
			if (entVec==null)
				break;

			if((destPoint!=null && destPoint.distanceTo(entVec)<=2) )
				speed = (float) 0.5;
			else
				speed = 1;

			if (stuckCount>roamingStuckLimit)
				{
				isDead=true;
				System.out.println("killing deliveryman stuck>6");
				}


			if ((destPoint!=null && destPoint.distanceTo(entVec)<=4) || atEndOfPath())
			{
 				currentAction = actionDeliverResources;
			}
			break;
		case actionDeliverResources:
			stuckCount = 0;
			speed=moveForward = 0;
			moveStrafing = 0;
			freeRoamCount = 100;
			blockJumping = true;

			// seek for a different kind of chests

			Vec3D chestPos = null;

			chestPos = scanForBlockNearEntity(mod_MineColony.hutLumberjack.blockID, 4, 3, 4);
			if(chestPos==null)
				chestPos = scanForBlockNearEntity(mod_MineColony.hutMiner.blockID, 4, 3, 4);
			if(chestPos==null)
				chestPos = scanForBlockNearEntity(mod_MineColony.hutWarehouse.blockID, 4, 3, 4);
			if(chestPos==null)
				chestPos = scanForBlockNearEntity(mod_MineColony.hutFarmer.blockID, 4, 3, 4);

			if (chestPos != null) {
				TileEntityChest tileentitychest = (TileEntityChest) worldObj
				.getBlockTileEntity(
						MathHelper.floor_double(chestPos.xCoord),
						MathHelper.floor_double(chestPos.yCoord),
						MathHelper.floor_double(chestPos.zCoord));
				if (tileentitychest != null)
				{

					int ret = MakeDeliveryActions(tileentitychest);

					if(defaultHoldItem == null || ret ==2 )
					{
						defaultHoldItem = null;
						currentAction = actionFindNextCheckpoint;
					}
					else
						isSwinging = true;
				}
			}
			else
			{

				currentAction = actionFindNextCheckpoint;
			}
			break;

		}
	}

	// if return 1 then delivere more
	// if return 2 then stop delivery
	private int MakeDeliveryActions(TileEntityChest chest) {
		boolean haveSpade=false, chestNeedsSpade=false;
		boolean haveHoe=false, chestNeedsHoe=false;
		boolean haveAxe=false, chestNeedsAxe=false;
		boolean havePick=false, chestNeedsPick=false;


		defaultHoldItem = null;
		int chestType = worldObj.getBlockId(chest.xCoord, chest.yCoord, chest.zCoord);


		if (countItemInInventory(new ItemStack(Item.shovelWood,1))>0 ||
				countItemInInventory(new ItemStack(Item.shovelStone,1))>0 ||
				countItemInInventory(new ItemStack(Item.shovelSteel,1))>0 ||
				countItemInInventory(new ItemStack(Item.shovelDiamond))>0
			)
			{
			System.out.println("have spade in inventory");
			haveSpade=true;
			}
		if (countItemInInventory(new ItemStack(Item.hoeWood,1))>0 ||
				countItemInInventory(new ItemStack(Item.hoeStone,1))>0 ||
				countItemInInventory(new ItemStack(Item.hoeSteel,1))>0 ||
				countItemInInventory(new ItemStack(Item.hoeDiamond))>0
			)
			{
			System.out.println("have hoe in inventory");
			haveHoe=true;
			}
		if (countItemInInventory(new ItemStack(Item.axeWood,1))>0 ||
				countItemInInventory(new ItemStack(Item.axeStone,1))>0 ||
				countItemInInventory(new ItemStack(Item.axeSteel,1))>0 ||
				countItemInInventory(new ItemStack(Item.axeDiamond))>0
			)
			{
			System.out.println("have axe in inventory");
			haveAxe=true;
			}
		if (countItemInInventory(new ItemStack(Item.pickaxeWood,1))>0 ||
				countItemInInventory(new ItemStack(Item.pickaxeStone,1))>0 ||
				countItemInInventory(new ItemStack(Item.pickaxeSteel,1))>0 ||
				countItemInInventory(new ItemStack(Item.pickaxeDiamond))>0
			)
			{
			System.out.println("have pickaxe in inventory");
			havePick=true;
			}

		if(chestType == mod_MineColony.hutLumberjack.blockID)
		{
			// get goods
			if(getGoods(chest, Block.wood.blockID)) return 1;

			if (!doesChestContainItem( chest, ItemAxe.axeWood.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemAxe.axeStone.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemAxe.axeGold.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemAxe.axeSteel.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemAxe.axeDiamond.shiftedIndex, 1)
				)
			{
				System.out.println("chest needs axe");
				chestNeedsAxe=true;
			}

			// deliver tools
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				if(slot!=null && (slot.getItem().getClass() == ItemAxe.class  &&  chestNeedsAxe) ||
						slot.itemID == Block.sapling.blockID)
				{
					while(slot.stackSize>0)
					{
						int quantity = 1;
						if(slot.itemID == Block.sapling.blockID)
						{
							if(slot.stackSize>5)
								quantity = 5;
							else
								quantity = slot.stackSize;
						}
						if(putItemIntoChest(chest, slot.getItem().shiftedIndex, quantity))
						{
							slot.stackSize -=quantity;
							defaultHoldItem = new ItemStack(slot.getItem().shiftedIndex,quantity,1);
							if(slot.stackSize<=0)
							{
								inventory.remove(i);
								i--;
							}
							return 2;
						}
					}
				}
			}
		}
		else if(chestType == mod_MineColony.hutMiner.blockID)
		{
			// get goods
			if(getGoods(chest, Block.dirt.blockID)) return 1;
			if(getGoods(chest, Block.cobblestone.blockID)) return 1;
			if(getGoods(chest, Item.coal.shiftedIndex)) return 1;
			if(getGoods(chest, Block.oreIron.blockID)) return 1;
			if(getGoods(chest, Block.sand.blockID)) return 1;
			if(getGoods(chest, Block.gravel.blockID)) return 1;
			if(getGoods(chest, Item.flint.shiftedIndex)) return 1;
			if(getGoods(chest, Block.oreGold.blockID)) return 1;
			if(getGoods(chest, Item.diamond.shiftedIndex)) return 1;
			if(getGoods(chest, Item.redstone.shiftedIndex)) return 1;


			if (!doesChestContainItem( chest, ItemSpade.shovelWood.shiftedIndex, 1) &&
					!doesChestContainItem( chest, ItemSpade.shovelSteel.shiftedIndex, 1) &&
					!doesChestContainItem( chest, ItemSpade.shovelStone.shiftedIndex, 1) &&
					!doesChestContainItem( chest, ItemSpade.shovelGold.shiftedIndex, 1) &&
					!doesChestContainItem( chest, ItemSpade.shovelDiamond.shiftedIndex, 1)
					)
			{
				System.out.println("chest needs spade");
				chestNeedsSpade=true;
			}
			if (!doesChestContainItem( chest, ItemPickaxe.pickaxeWood.shiftedIndex, 1) &&
					!doesChestContainItem( chest, ItemPickaxe.pickaxeGold.shiftedIndex, 1) &&
					!doesChestContainItem( chest, ItemPickaxe.pickaxeSteel.shiftedIndex, 1) &&
					!doesChestContainItem( chest, ItemPickaxe.pickaxeStone.shiftedIndex, 1) &&
					!doesChestContainItem( chest, ItemPickaxe.pickaxeDiamond.shiftedIndex, 1)
					)
			{
				System.out.println("chest needs pick");
				chestNeedsPick=true;
			}


			// deliver tools
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				if(slot!=null && ((slot.getItem().getClass() == ItemPickaxe.class &&  chestNeedsPick) ||
						(slot.getItem().getClass() == ItemSpade.class  &&   chestNeedsSpade) ||
						slot.itemID == Block.torchWood.blockID))
				{
					while(slot.stackSize>0)
					{
						int quantity = 1;
						if(slot.itemID == Block.torchWood.blockID)
						{
							if(slot.stackSize>5)
								quantity = 5;
							else
								quantity = slot.stackSize;
						}
						if(putItemIntoChest(chest, slot.getItem().shiftedIndex, quantity))
						{
							slot.stackSize -=quantity;
							defaultHoldItem = new ItemStack(slot.getItem().shiftedIndex,quantity,0);
							if(slot.stackSize<=0)
							{
								inventory.remove(i);
								i--;
							}
							return 2;
						}
					}

				}
			}
		}
		else if(chestType == mod_MineColony.hutFarmer.blockID)
		{
			// get goods
			if(getGoods(chest, Item.wheat.shiftedIndex)) return 1;



			if (!doesChestContainItem( chest, ItemSpade.shovelWood.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemSpade.shovelGold.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemSpade.shovelSteel.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemSpade.shovelStone.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemSpade.shovelDiamond.shiftedIndex, 1)
					)
			{
				System.out.println("chest needs spade");
				chestNeedsSpade=true;
			}
			if (!doesChestContainItem( chest, ItemHoe.hoeWood.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemHoe.hoeStone.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemHoe.hoeSteel.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemHoe.hoeStone.shiftedIndex, 1)  &&
					!doesChestContainItem( chest, ItemHoe.hoeDiamond.shiftedIndex, 1)
					)
			{
				System.out.println("chest needs hoe");
				chestNeedsHoe=true;
			}


			// deliver tools
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				if(slot!=null && (
						(slot.getItem().getClass() == ItemHoe.class && chestNeedsHoe) ||
						(slot.getItem().getClass() == ItemSpade.class && chestNeedsSpade) ||
						slot.getItem().shiftedIndex == Item.seeds.shiftedIndex))
				{
					while(slot.stackSize>0)
					{
						int quantity = 1;
						if(slot.getItem().shiftedIndex == Item.seeds.shiftedIndex)
						{
							if(slot.stackSize>5)
								quantity = 5;
							else
								quantity = slot.stackSize;
						}
						if(putItemIntoChest(chest, slot.getItem().shiftedIndex, quantity))
						{
							slot.stackSize -=quantity;
							defaultHoldItem = new ItemStack(slot.getItem().shiftedIndex,quantity,0);
							if(slot.stackSize<=0)
							{
								inventory.remove(i);
								i--;
							}
							return 2;
						}
					}
				}
			}
		}
		else if(chestType == mod_MineColony.hutWarehouse.blockID)
		{
			// get other goods
			if(getGoods(chest, Block.sapling.blockID)) return 1;
			if(getGoods(chest, Block.torchWood.blockID)) return 1;
			if(getGoods(chest, Item.seeds.shiftedIndex)) return 1;

			// deliver goods
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				if(slot!=null && (slot.getItem().shiftedIndex == Block.wood.blockID ||
						slot.getItem().shiftedIndex == Block.dirt.blockID ||
						slot.getItem().shiftedIndex == Block.cobblestone.blockID ||
						slot.getItem().shiftedIndex == Item.coal.shiftedIndex ||
						slot.getItem().shiftedIndex == Block.oreIron.blockID ||
						slot.getItem().shiftedIndex == Block.sand.blockID ||
						slot.getItem().shiftedIndex == Block.gravel.blockID ||
						slot.getItem().shiftedIndex == Block.oreGold.blockID ||
						slot.getItem().shiftedIndex == Item.diamond.shiftedIndex ||
						slot.getItem().shiftedIndex == Item.redstone.shiftedIndex ||
						slot.getItem().shiftedIndex == Item.wheat.shiftedIndex))
				{
					int tmpcnt=0;  // fixes infinite loop where it for ever tries to put the item into the chest
					while(slot.stackSize>0 && tmpcnt<200)
				 	{
						if(putItemIntoChest(chest, slot.getItem().shiftedIndex, 1))
						{
							slot.stackSize -=1;
						}
						tmpcnt++;
					}
					if (tmpcnt>=200)
					{
						dropItem(slot.itemID, slot.stackSize);
					}
					inventory.remove(i);
					i--;
					defaultHoldItem = new ItemStack(slot.getItem().shiftedIndex,1,0);
					return 1;
				}
			}

			// get tools
			ItemStack items = null;

			if(!haveAxe && (items = getItemFromChest(chest, ItemAxe.class, 1))!=null)
			{
				if(items!=null)
				{
					inventory.add(items);
					defaultHoldItem = new ItemStack(items.getItem().shiftedIndex, 1,0);
					return 1;
				}
			}
			items = null;


			if(!havePick && (items = getItemFromChest(chest, ItemPickaxe.class, 1))!=null)
			{
				if(items!=null)
				{
					inventory.add(items);
					defaultHoldItem = new ItemStack(items.getItem().shiftedIndex, 1,0);
					return 1;
				}
			}
			items = null;

			if(!haveSpade && (items = getItemFromChest(chest, ItemSpade.class, 1))!=null)
			{
				if(items!=null)
				{
					inventory.add(items);
					defaultHoldItem = new ItemStack(items.getItem().shiftedIndex, 1,0);
					return 1;
				}
			}
			items = null;

			if(!haveHoe &&  (items = getItemFromChest(chest, ItemHoe.class, 1))!=null)
			{
				if(items!=null)
				{
					inventory.add(items);
					defaultHoldItem = new ItemStack(items.getItem().shiftedIndex, 1,0);
					return 1;
				}
			}
			items = null;

		}
		return 2;

	}



	private boolean getGoods(TileEntityChest chest, int blockID) {
		ItemStack items = null;
		while((items = getItemFromChest(chest, blockID, 99999))!=null)
		{
			if(items!=null)
			{
				inventory.add(items);
				defaultHoldItem = new ItemStack(blockID, 1,0);
				return true;
			}
		}
		return false;
		//items = null;
	}



	protected Vec3D scanForNextCheckpoint(int rx, int ry, int rz, int chp, String routeName, boolean goBack) {

		// if goback, we are looking for a number smaller than chp otherwise its a number bigger!
		Vec3D bestVec=null; int bestCP;
		if (goBack)
			bestCP=-100;
		else
			bestCP=99999;


		routeName = routeName.trim();
		for (int i = iPosX - rx; i <= iPosX + rx; i++)
			for (int j = iPosY - ry; j <= iPosY + ry; j++)
				for (int k = iPosZ - rz; k <= iPosZ + rz; k++) {
					if (signAt(i, j, k)) {

						TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(i,j,k);
						if(sign==null)
							continue;

						boolean matches =false; int signNbr;

						// 	for each string
						for (int sl = 0;sl<sign.signText.length;sl++)
						{
							String signTextline = sign.signText[sl];
							if (routeName.length()==0  ||  signTextline.toLowerCase().startsWith(routeName.toLowerCase()))
							{	// if it starts with my routename or routename is blank
								matches=true;

								signTextline = signTextline.substring(routeName.length()).trim();
								//   chop off the matching part.
								//   if there is a number following
								if (isNumber(signTextline))
								{
									signNbr=Integer.parseInt(signTextline);
									if ((goBack && signNbr>bestCP  && signNbr<chp)  ||   (!goBack &&  signNbr<bestCP  && signNbr>chp))
									{
										bestCP=signNbr;

										bestVec=Vec3D.createVectorHelper(i, j, k);
									}
								}
							}
							else
							{
								if (matches  &&  isNumber(signTextline))
								{
									signNbr=Integer.parseInt(signTextline);

									if ((goBack && signNbr>bestCP  && signNbr<chp)  ||   (!goBack &&  signNbr<bestCP  && signNbr>chp))
									{
										bestCP=signNbr;

										bestVec=Vec3D.createVectorHelper(i, j, k);
									}
								}
							}
						}
					}
				}
        if (bestVec!=null)
        {
        	currentCheckPoint=bestCP;
        }
		return bestVec;

		//     is it a 'better' number?
		//       use it
		//  else
		// if we have a match and the line can be parsed to a number
		/*
						int checkPoint = -1;

						for(int cp = 0;cp<sign.signText.length;cp++)
						{
							String textNr = sign.signText[cp];
							if(isNumber(textNr))
							{
								checkPoint = Integer.parseInt(textNr);
								if(checkPoint == chp)
									return Vec3D.createVectorHelper(i, j, k);
							}
						}

					}
				}

		return null;
		 */
	}



	protected int getDropItemId() {
		return Block.crate.blockID;
	}

	public void onDeath(Entity entity) {
		if(scoreValue > 0 && entity != null)
		{
			entity.addToPlayerScore(this, scoreValue);
		}
	//	field_9327_S = true;
		if(!worldObj.multiplayerWorld)
		{
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				dropItem(slot.itemID, slot.stackSize);
			}
		}
		worldObj.func_9425_a(this, (byte)3);
	}


	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("currentCheckPoint", currentCheckPoint);
		if (routeName.equals(""))
			nbttagcompound.setString("routeName", badRouteName);
		else
			nbttagcompound.setString("routeName", routeName);

		nbttagcompound.setBoolean("goBack", goBack);

		NBTTagList inventoryList = new NBTTagList();

		for(int i=0;i<inventory.size();i++)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			inventory.get(i).writeToNBT(tagCompound);
			inventoryList.setTag(tagCompound);
		}

		nbttagcompound.setTag("InventoryDelivery", inventoryList);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);


		currentCheckPoint = nbttagcompound.getInteger("currentCheckPoint");
		routeName = nbttagcompound.getString("routeName");
		if (routeName.equals(badRouteName))  routeName="";
		goBack = nbttagcompound.getBoolean("goBack");

		NBTTagList nbttaglist = nbttagcompound.getTagList("InventoryDelivery");
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound tagCompound = (NBTTagCompound)nbttaglist.tagAt(i);

			ItemStack itemstack = new ItemStack(tagCompound);

			if(itemstack.getItem() == null)
			{
				continue;
			}

			inventory.add(itemstack);
		}
	}

}
