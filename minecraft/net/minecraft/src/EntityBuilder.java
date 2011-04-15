

// slabs  cobblestone/wood/stone/sandstone    _c _w  _C  _S
//    
// redo floorplans
// do builder.txt
// put first line as version for file 1
// figure out way to handle repeats 


package net.minecraft.src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import net.minecraft.client.Minecraft;

public class EntityBuilder extends EntityWorker {
	private static final int actionFindSomethingToDo = 30;
	private static final int actionGotoNextConstructionSite = 31;
	private static final int actionBuild = 32;
	private static final int actionStartBuild = 36;
	private static final int actionGotoHome = 33;
	private static final int actionGotoChest = 34;
	private static final int actionGetMaterials = 35;
	private static final int actionBackToBuilding = 37;	
	
	private static final String dontCare="  ";
	private static final String emptyBlock="..";

	private List<ItemStack> inventory;
	
	private ArrayList<String> floorPlan;
	private int fpX, fpZ, fpY, fpIdx;
	private int fpSignX, fpSignY, fpSignZ;
	private String fpSymbol;
	private int fpRotation;
	private boolean haveBuildSign=false, fpSignGood=false;
	private int blockType;
	private int idleTime=0;
	
	private String mc_path;
	private double signX, signY, signZ;  // building is relative to the signs position
	private boolean haveSign=false;
	private boolean firstTimeRunning=true;
	
	Vec3D myChestPos;
	
	String inventoryResourceName[] ={"wood", "planks", "cobble","stone", "sand", "sandstone", "glass", "bricks", "wool"};
	int amtInInventory[]={ 0,0,0,0,0,0,0,0,0};  //8
	int amtNeeded[]={ 0,0,0,0,0,0,0,0,0};
	int amtWanted[]={ 0,0,0,0,0,0,0,0,0};
	int myBlockID[]={Block.wood.blockID, Block.planks.blockID,Block.cobblestone.blockID, Block.stone.blockID, Block.sand.blockID, Block.sandStone.blockID, Block.glass.blockID, Block.brick.blockID, Block.cloth.blockID},
	    baseBlockID[]={Block.wood.blockID, Block.wood.blockID,Block.cobblestone.blockID, Block.stone.blockID, Block.sand.blockID, Block.sand.blockID, Block.glass.blockID, Block.brick.blockID, Block.cloth.blockID};
	
 //  how much we have on hand, how much needed for current block, how much for current structure
	
	int resourceIDNeeded;  // holds the resource needed for the block, planks for doors, cobblestone for compactstonestairs...
	
	private double destX, destY, destZ;
	private World world;

	public EntityBuilder(World world) {
		super(world);
		this.world=world;

		// get texture
		texture = "/mob/builder1.png";
		int rndTexture = (int) (1+Math.random()*4);
		if (rndTexture==2) texture = "/mob/builder2.png";
		if (rndTexture==3) texture = "/mob/builder3.png";
		if (rndTexture==4) texture = "/mob/builder4.png";
		
		setSize(.9F, 1.3F);
		
		//setSize((float) (Math.random()*.4+.8), (float) (Math.random()*.4+.8));
		defaultHoldItem = null;
		currentAction = actionFindSomethingToDo;
		destPoint = null;
		inventory = new ArrayList<ItemStack>();
		workingRange = 40;
		roamingStuckLimit = 12;
	
	}
	
	
	public void onUpdate() {
		super.onUpdate();
		fallDistance = 0.0F;
	}
	
	
	protected void onSwing()
	{
		if(currentAction==actionBuild )
		{
			StepSound stepsound = Block.wood.stepSound;
			
			if (fpSymbol!=null)
			{
			
			if (fpSymbol.equals("cc")) stepsound = Block.cobblestone.soundStoneFootstep;
			}
			
			worldObj.playSoundAtEntity(this, stepsound.func_1145_d(), stepsound.func_1147_b() * 0.15F, stepsound.func_1144_c());

			// digging animation
			//ModLoader.getMinecraftInstance().effectRenderer.func_1186_a((int)starringPointX,(int)starringPointY,(int)starringPointZ);
			ModLoader.getMinecraftInstance().effectRenderer.addBlockHitEffects((int)starringPointX,(int)starringPointY,(int)starringPointZ,0);
		}
	}

	protected void workerUpdate() {
		if (firstTimeRunning)
		{
			destroySignsInRange(30,10,30);
			myChestPos = scanForBlockNearPoint(mod_MineColony.hutBuilder.blockID, lastTickPosX, lastTickPosY, lastTickPosZ, 30, 30, 30);

			firstTimeRunning=false;
		}
		blockJumping = false;

		switch (currentAction) {
		case  actionFindSomethingToDo:
			
			if (idleTime>0)
			{
				idleTime--;
				//displayMessage("Daydreaming", "", "");
				break;
			}
			else
				idleTime=-1;

			isJumping=false;
			defaultHoldItem=null;
			if (haveBuildSign && !fpSignExists((int) Math.floor(signX),(int)  Math.floor( signY), (int)  Math.floor(signZ)))
			{
				forgetFloorplanAndBuildInfo();
				haveBuildSign=false;
			}

			if(!worldObj.isDaytime() )
			{
				displayMessage("","", "Sleeping");
				break;
			}
			
			if (haveBuildSign  &&  fpSignGood  &&  needMaterials()>0)
			{
				int matsNeeded=needMaterials();
			//	System.out.println("matsneeded=" + matsNeeded + " and blocktype (which we search for is " + blockType);
				destPoint=smartFindChestWithResource(resourceIDNeeded,30,10);

				if (destPoint==null)
				{
					undisplayMessage();
					signText1=needMaterialsString();  // need function to generate proper text
					displayMessage(signText1, "", "");
					idleTime=20;
					break;
				}
				else
				{
					if (signText1.startsWith("Out of")) signText1="";
					undisplayMessage();
			
					currentAction=actionGotoChest;
					break;
				}
			}

			moveForward = 0;
			moveStrafing = 0;
			isJumping = false;

			destPoint = null;
			isSwinging = false;

			if (!fpSignExists((int) Math.floor(signX),(int)  Math.floor( signY), (int)  Math.floor(signZ)))
				haveBuildSign=false;
			if (haveBuildSign  &&  !fpSignGood)
			{
				displayMessage("Unknown Floorplan", mc_path, "");
				break;
			}

			if (haveBuildSign)
				break;
			
			
			// put items in hand into our chest
			
			
			
			// search for next checkpoint sign near
			Vec3D nextCP = scanForNextBuildSign(30,30,30);
			if(nextCP!=null)
			{
				destPoint = Vec3D.createVectorHelper(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);
				undisplayMessage();
				currentAction = actionGotoNextConstructionSite;
				undisplayMessage();
			}
			else
			{
				// didn't find build sign.
				// unload my materials
				placeAllMatsInChest();
			}
			break;
		case actionGotoNextConstructionSite:
			if (destPoint==null)
			{
				currentAction=actionGotoHome;
				break;
			}
			Vec3D entVec = Vec3D.createVector(posX, posY, posZ);

			if(destPoint!=null && destPoint.distanceTo(entVec)<=2)
			{
				speed = (float) 0;
				signX = destPoint.xCoord;
				signY = destPoint.yCoord;
				signZ = destPoint.zCoord;
				haveBuildSign=true;
				currentAction = actionStartBuild ;
			}
			else
				speed=1;
			break;
		case actionBackToBuilding:
			if (destPoint==null)
			{
				currentAction=actionGotoHome;
				break;
			}
			entVec = Vec3D.createVector(posX, destPoint.yCoord, posZ);

			if(destPoint!=null && destPoint.distanceTo(entVec)<=2)
			{
				speed = (float) 0.0;
				signX = destPoint.xCoord;
				signY = destPoint.yCoord;
				signZ = destPoint.zCoord;
				haveBuildSign=true;
				currentAction = actionBuild ;
			}
			else
				speed=1;
			break;			
		case actionStartBuild :
			speed = 0;
			stuckCount = 0;
			moveForward = 0;
			moveStrafing = 0;
			freeRoamCount = 100;
			blockJumping = true;

			//  get floorplan

			if (floorPlan==null || floorPlan.size()==0)
			{
				floorPlan= readFloorPlanFromFile(getSignText((int) signX, (int) signY, (int) signZ, 0), getSignText((int) signX, (int) signY, (int) signZ, 1), getSignText((int) signX, (int) signY, (int) signZ, 2));
				fpRotation = getSignRotation((int) signX, (int) signY, (int) signZ);
				// System.out.println("signx=" + (int) signX + " signy= " + (int)  signY + " signz=" + (int) signZ);

				fpX=0;
				fpY=0;
				fpZ=0;
				fpIdx=0;
				fpSymbol=emptyBlock;
				//getFPSignOffset();
				if (floorPlan==null  ||  floorPlan.size()==0 ||  floorPlan.get(0)==null)
				{
					floorPlan=null;
					fpSymbol="";
					destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);
					currentAction= actionGotoHome;
					fpSignGood=false;
					signText1 = "Unknown floorplan";
					break;
				}
				else
				{
					fpSignGood=true;
					if (floorPlan.get(0).length()>=2)
						fpSymbol = floorPlan.get(0).substring(0,2);
					while (fpSymbol.equals(emptyBlock) && fpIdx<floorPlan.size())
						incrFPBlock();
				}
				if (fpIdx>=floorPlan.size())
				{
					//  we are done!
					floorPlan=null;
					fpSymbol="";
					haveBuildSign=false;
				}
			}
			currentAction=actionBuild;

			break;
		case actionBuild :
			isSwinging=true;
			isJumping=false;		
			speed = (float) 0.0;
			moveForward=0;
			moveStrafing=0;
			if (!fpSignExists((int) signX, (int) signY, (int) signZ))
			{  // removed somehow;
				haveBuildSign=false;
				isSwinging=false;
				destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);
				currentAction= actionGotoHome;
				fpIdx=-1;
				floorPlan=null;
				break;
			}

			if(!worldObj.isDaytime() )
			{
				haveBuildSign=false;
				isSwinging=false;
				isJumping=false;
				destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);
				currentAction= actionGotoHome;
				break;
			}
			
			if (floorPlan==null)
			{
				destroyBuildSigns(30, 30, 30);
				haveBuildSign=false;
				destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);
				for (int i=0;i<=8;i++)
					amtNeeded[i]=0;
				isSwinging=false;
				currentAction= actionGotoHome;
			}
         //   System.out.println("fpsign values " + fpSignX + " " + fpSignY + " " + fpSignZ);
			blockType  = getBlockValues(fpSymbol);
			if (fpRotation==2)
			{
				destX =  signX-fpSignX+fpX/2;
				destY =  signY-fpSignY+fpY;
				destZ =  signZ-fpSignZ+fpZ;	    	
			}
			else if (fpRotation==1)   // build to north
			{
				destX =  signX-fpSignZ+fpZ;  // was signX + fpSignZ + fpZ
				destY =  signY-fpSignY+fpY;
				destZ =  signZ+fpSignX-fpX/2;   //  was signZ + fpSignX - fpX/2    	
			}
			else if (fpRotation==0)  //build to east
			{
				destX =  signX+fpSignX-fpX/2;
				destY =  signY-fpSignY+fpY;
				destZ =  signZ+fpSignZ-fpZ;			    	
			}
			else if (fpRotation==3) // build to south
			{
				destX =  signX+fpSignZ-fpZ;    // was signX-fpSignZ-fpZ
				destY =  signY-fpSignY+fpY;
				destZ =  signZ-fpSignX+fpX/2;  // was signZ-fpSignX+fpX/2ss					    	
			}
			//destPoint = Vec3D.createVectorHelper(destX, destY, destZ);
			//speed = (float) .50;
			int blockIDtoPlace = getBlockValues(fpSymbol);
			computeAmtResouceNeeded(blockIDtoPlace);
			if (blockType>=0)
			{
				if (haveResourceInInventory(resourceIDNeeded))
					placeBlock();
				else
				{
					computeAmtResouceWanted();
					destPoint=smartFindChestWithResource(resourceIDNeeded,30,10);
					isSwinging=false;
					defaultHoldItem=null;
					if (destPoint!=null)
						currentAction=actionGotoChest;					
					else
						currentAction=actionGotoHome;
					break;
				}
			}
			getNextBlock();

			break;
		case actionGotoChest:
			isSwinging = false;
			if (destPoint==null)
			{
				currentAction=actionGotoHome;
				break;
			}

			if(!worldObj.isDaytime() )
			{
				signText3 = "Sleeping";
				break;
			}

			entVec = Vec3D.createVector(posX, destPoint.yCoord, posZ);
			if(destPoint!=null && destPoint.distanceTo(entVec)<=2)
			{
				moveForward=0;
				moveStrafing=0;
				speed = (float) 0;
				blockJumping=false;
				isJumping=false;
				currentAction = actionGetMaterials;
			}
			else
				speed = 1;			
			break;
		case actionGotoHome:
			isSwinging = false;
			defaultHoldItem=null;


			destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);

			entVec = Vec3D.createVector(posX, destPoint.yCoord, posZ);
			if(destPoint!=null && destPoint.distanceTo(entVec)<=2)
			{
				speed = (float) 0;
				blockJumping=false;
				isJumping=false;
				currentAction = actionFindSomethingToDo;
			}
			else
				speed = 1;
			break;
		case actionGetMaterials:
			if (destPoint==null)
			{
				currentAction=actionGotoHome;
				break;
			}
			
		//getAllNeededMaterialsFromChest((int) destPoint.xCoord, (int) destPoint.yCoord, (int) destPoint.zCoord);
			if (getFirstNeededMaterialsFromChest((int) destPoint.xCoord, (int) destPoint.yCoord, (int) destPoint.zCoord))
				break;
			
			destPoint = Vec3D.createVectorHelper(signX, signY, signZ);
			currentAction=actionGotoNextConstructionSite;
			
			break;
		}	
	}

	
	void forgetFloorplanAndBuildInfo()
	{
		fpIdx=-1;
		for (int i=0;i<=8;i++)
			amtNeeded[i]=amtWanted[i]=0;
		isSwinging=false;
		floorPlan=null;
		//signX= signY= signZ=0;
	}
	
	
	void computeAmtResouceNeeded(int blockID) {
		int i;

		for (i = 0; i <= 8; i++)
			amtNeeded[i] = 0;

		resourceIDNeeded = -1;

		for (i = 0; i <= 8; i++)
			if (myBlockID[i] == blockID) {
				amtNeeded[i] = 1;
				resourceIDNeeded = blockID;
				
				if (blockID==Block.planks.blockID)
					amtNeeded[0]=1;
				if (blockID==Block.sandStone.blockID)
					amtNeeded[4]=1;	
				return;
			}

		if (blockID == Block.fence.blockID) {
			amtNeeded[1] = 1;
			amtNeeded[0] = 1;
			resourceIDNeeded = Block.planks.blockID;
		}

		if (blockID==Block.stairCompactCobblestone.blockID) {
			amtNeeded[2] = 1;
			resourceIDNeeded = Block.cobblestone.blockID;
		}

		if (blockID==Block.doorWood.blockID) {
			amtNeeded[1] = 6;
			amtNeeded[0] = 2;
			resourceIDNeeded = Block.planks.blockID;
		}

		if (blockID==Block.stairCompactPlanks.blockID) {
			amtNeeded[1] = 1;
			amtNeeded[0] = 1;
			resourceIDNeeded = Block.planks.blockID;
		}

	}
	
	
	void computeAmtResouceWanted()
	{
		amtWanted[0]=getCntSymbolNeeded("uu");
		amtWanted[2] = getCntSymbolNeeded("cc")+  getCntSymbolNeeded("^");
		amtWanted[1]=getCntSymbolNeeded("ww") + 6 * getCntSymbolNeeded("|") + getCntSymbolNeeded("v") + getCntSymbolNeeded("fp");
		amtWanted[0] += 1+(amtWanted[1]/4);
		amtWanted[3]= getCntSymbolNeeded("CC");  // stone
		amtWanted[4]= getCntSymbolNeeded("sa");  // sand
		amtWanted[5]= getCntSymbolNeeded("SS");  // sandstone
		amtWanted[4] += amtWanted[5]*4;
		amtWanted[6]= getCntSymbolNeeded("gg");  // glass
		amtWanted[7]=getCntSymbolNeeded("bb");  // bricks
		amtWanted[8]=getCntSymbolNeeded("oo");  // wool
	}	
	
	
	// handles inventory
	void replaceWorldBlock(int oldBlockType, int newBlockType)
	{
		if (oldBlockType==newBlockType)
			return;
		if (oldBlockType!=Block.glass.blockID) addtoOurInventory(oldBlockType, 1);
		useUpInventory(newBlockType);
	}
	
	
	boolean haveResourceInInventory(int blockType)
	{
		if (blockType<=0) return true;
		
		int i;
		
		for (i=0;i<=8;i++)
			if (myBlockID[i]==blockType && amtInInventory[i]>0) 
			  return true;
			  
		if (blockType == Block.planks.blockID ||  blockType == Block.wood.blockID  ||  blockType == Block.stairCompactPlanks.blockID)
		{
	//		System.out.println("checking planks in inventory (level = " + amtInInventory[1]);
	//		System.out.println("wood in inventory (level = " + amtInInventory[0]);


			if (amtNeeded[0]*4+amtNeeded[1]<=amtInInventory[0]*4+amtInInventory[1])
				return true;
			return false;
		}	
		if (blockType == Block.cobblestone.blockID   	)
		{
		if (amtNeeded[2]<=amtInInventory[2])
			return true;
		return false;
		}
		if (blockType == Block.sandStone.blockID   	)
		{
		if (amtNeeded[5]*4<=amtInInventory[4])
			return true;
		
		return false;
		}
		return false;
	}

	
	void useUpInventory(int blockType)
	{
		int i;
		for (i=0;i<=8; i++)
			if (myBlockID[i]==blockType  &&  amtInInventory[i]>0)
			{
				amtInInventory[i]--;
				return;
			}		

		int baseBlockType=getBaseBlockTypeFromBlockType(blockType);

		if (blockType == Block.planks.blockID ||  blockType == Block.wood.blockID  
				||  blockType == Block.stairCompactPlanks.blockID ||  blockType == Block.fence.blockID
				|| blockType == Block.doorWood.blockID)
		{

			if (blockType == Block.doorWood.blockID)
			{
		//		System.out.println("using up 6 planks for wood door");
				if (amtInInventory[1]<6)
				{
					amtInInventory[1]+=4;
					amtInInventory[0]--;
				}
				if (amtInInventory[1]<6)
				{
					amtInInventory[1]+=4;
					amtInInventory[0]--;
				}
				amtInInventory[1]-=6;
			} 
			if (blockType == Block.fence.blockID)
			{
	//			System.out.println("using up 1 planks for fence");
				if (amtInInventory[1]<1)
				{
					amtInInventory[1]+=4;
					amtInInventory[0]--;
				}
				amtInInventory[1]-=1;
			} 			
			if (blockType == Block.stairCompactPlanks.blockID)
			{
	//			System.out.println("using up 1 planks for stairs");

				if (amtInInventory[1]<1)
				{
					amtInInventory[1]+=4;
					amtInInventory[0]--;
				}
				amtInInventory[1]-=1;
			} 

			if (blockType == Block.planks.blockID)
			{
		//		System.out.println("using up 1 plank");

				if (amtInInventory[1]>0)
					amtInInventory[1]--;
				else
				{
					amtInInventory[1]+=3;
					amtInInventory[0]--;
				}
			}
			if (blockType == Block.wood.blockID)
				amtInInventory[0]--;
		}
		if (blockType == Block.cobblestone.blockID  ||  blockType == Block.stairCompactCobblestone.blockID)
			amtInInventory[2]--;

	//	for (i=0;i<=8;i++)
	//		System.out.println(inventoryResourceName[i] + " in inventory = " + amtInInventory[i]);
	}

	
	void addtoOurInventory(int blockType, int qty)
	{
	//System.out.println("adding to inventory " + blockType + " qty=" + qty);
    int i;
    for (i=0;i<=8; i++)
    	if (myBlockID[i]==blockType)
    	{
    		amtInInventory[i]+=qty;
    		return;
    	}
    for (i=0;i<=8; i++)
    	if (getBaseBlockTypeFromBlockType(myBlockID[i])==blockType)
    	{
    		amtInInventory[getBaseBlockTypeFromBlockType(myBlockID[i])]+=qty;
    		return;
    	}   
	}
	
	
	int needMaterials()
	{
		for (int i=0;i<=8; i++)
			if (amtNeeded[i]>amtInInventory[i])
				return myBlockID[i];
		
		return -1;
	}
	
	String needMaterialsString()
	{
		for (int i=8;i>=0; i--)
			if (amtNeeded[i]>0)  
				return "Out of " + inventoryResourceName[i] + " " + amtWanted[i];

		return "";
	}
	
	
	boolean chestContains(TileEntityChest ch, int blockType)
	{
		if (ch==null) return false;

		int slotIndex = 0;
		ItemStack slot = null;
		while((slot=ch.getStackInSlot(slotIndex))== null || slot.itemID != blockType)
		{
			slotIndex++;
			if(slotIndex>=ch.getSizeInventory()-1)
			{
				return false;
			}
		}
		return true;
	}
	
	
	//  blocktype is the place id, the id that will actually be placed
	Vec3D smartFindChestWithResource(int blockType, int rxz,  int ry)
	{
	Vec3D v = findChestWithResource(blockType, rxz, ry);
	
	if (v==null)
	{
		int bb =  getBaseBlockTypeFromBlockType(blockType);
		if (bb!=blockType)
		{
			v=findChestWithResource(bb,30,10);
		}
	}
	
	return v;
	}
	
	
	Vec3D findChestWithResource(int blockType, int rxz,  int ry)
	{
		int blockIDat;
		for (int d = 0;d<rxz; d++)
		{
			//  scan along max x
			for (int y=-d;y<=d; y++)
				for (int z=-d;z<=d; z++)
				{
				  blockIDat=world.getBlockId((int) lastTickPosX + d, (int) lastTickPosY+y,(int)  lastTickPosZ+z);
				  if (blockIDat==mod_MineColony.hutBuilder.blockID || blockIDat==mod_MineColony.hutLumberjack.blockID || blockIDat==mod_MineColony.hutMiner.blockID || blockIDat==mod_MineColony.hutWarehouse.blockID
						|| blockIDat == Block.crate.blockID)
						{
							//  convert to chest
					    TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity((int) lastTickPosX + d, (int) lastTickPosY+y,(int)  lastTickPosZ+z);
					    if (chestContains(ch, blockType))
					    {
					    	Vec3D v = Vec3D.createVectorHelper((int) lastTickPosX + d, (int) lastTickPosY+y,(int)  lastTickPosZ+z);
					    	return v;
					    }
					    
						}
				}
			//  scan along min x	
			for (int y=-d;y<=d; y++)
				for (int z=-d;z<=d; z++)
				{
				  blockIDat=world.getBlockId((int) lastTickPosX - d, (int) lastTickPosY+y,(int)  lastTickPosZ+z);
				  if (blockIDat==mod_MineColony.hutBuilder.blockID || blockIDat==mod_MineColony.hutLumberjack.blockID || blockIDat==mod_MineColony.hutMiner.blockID || blockIDat==mod_MineColony.hutWarehouse.blockID
						|| blockIDat == Block.crate.blockID)
						{
							//  convert to chest
					    TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity((int) lastTickPosX - d, (int) lastTickPosY+y,(int)  lastTickPosZ+z);
					    if (chestContains(ch, blockType))
					    {
					    	Vec3D v = Vec3D.createVectorHelper((int) lastTickPosX - d, (int) lastTickPosY+y,(int)  lastTickPosZ+z);
					    	return v;
					    }
					    
						}
				}			
			//  scan along max z
			for (int y=-d;y<=d; y++)
				for (int x=-d;x<=d; x++)
				{
				  blockIDat=world.getBlockId((int) lastTickPosX + x, (int) lastTickPosY+y,(int)  lastTickPosZ+d);
				  if (blockIDat==mod_MineColony.hutBuilder.blockID || blockIDat==mod_MineColony.hutLumberjack.blockID || blockIDat==mod_MineColony.hutMiner.blockID || blockIDat==mod_MineColony.hutWarehouse.blockID
						|| blockIDat == Block.crate.blockID)
						{
							//  convert to chest
					    TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity((int) lastTickPosX + x, (int) lastTickPosY+y,(int)  lastTickPosZ+d);
					    if (chestContains(ch, blockType))
					    {
					    	Vec3D v = Vec3D.createVectorHelper((int) lastTickPosX + x, (int) lastTickPosY+y,(int)  lastTickPosZ+d);
					    	return v;
					    }
					    
						}
				}			
			//  scan along min z	
			for (int y=-d;y<=d; y++)
				for (int x=-d;x<=d; x++)
				{
				  blockIDat=world.getBlockId((int) lastTickPosX + x, (int) lastTickPosY+y,(int)  lastTickPosZ-d);
				  if (blockIDat==mod_MineColony.hutBuilder.blockID || blockIDat==mod_MineColony.hutLumberjack.blockID || blockIDat==mod_MineColony.hutMiner.blockID || blockIDat==mod_MineColony.hutWarehouse.blockID
						|| blockIDat == Block.crate.blockID)
						{
							//  convert to chest
					    TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity((int) lastTickPosX + x, (int) lastTickPosY+y,(int)  lastTickPosZ-d);
					    if (chestContains(ch, blockType))
					    {
					    	Vec3D v = Vec3D.createVectorHelper((int) lastTickPosX + x, (int) lastTickPosY+y,(int)  lastTickPosZ-d);
					    	return v;
					    }
					    
						}
				}
			
			//  scan along max y
			for (int x=-d;x<=d; x++)
				for (int z=-d;z<=d; z++)
				{
				  blockIDat=world.getBlockId((int) lastTickPosX + x, (int) lastTickPosY+d,(int)  lastTickPosZ+z);
				  if (blockIDat==mod_MineColony.hutBuilder.blockID || blockIDat==mod_MineColony.hutLumberjack.blockID || blockIDat==mod_MineColony.hutMiner.blockID || blockIDat==mod_MineColony.hutWarehouse.blockID
						|| blockIDat == Block.crate.blockID)
						{
							//  convert to chest
					    TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity((int) lastTickPosX + x, (int) lastTickPosY+d,(int)  lastTickPosZ+z);
					    if (chestContains(ch, blockType))
					    {
					    	Vec3D v = Vec3D.createVectorHelper((int) lastTickPosX + x, (int) lastTickPosY+d,(int)  lastTickPosZ+z);
					    	return v;
					    }
					    
						}
				}				
			//  scan along min y	
			for (int x=-d;x<=d; x++)
				for (int z=-d;z<=d; z++)
				{
				  blockIDat=world.getBlockId((int) lastTickPosX + x, (int) lastTickPosY-d,(int)  lastTickPosZ+z);
				  if (blockIDat==mod_MineColony.hutBuilder.blockID || blockIDat==mod_MineColony.hutLumberjack.blockID || blockIDat==mod_MineColony.hutMiner.blockID || blockIDat==mod_MineColony.hutWarehouse.blockID
						|| blockIDat == Block.crate.blockID)
						{
							//  convert to chest
					    TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity((int) lastTickPosX + x, (int) lastTickPosY-d,(int)  lastTickPosZ+z);
					    if (chestContains(ch, blockType))
					    {
					    	Vec3D v = Vec3D.createVectorHelper((int) lastTickPosX + x, (int) lastTickPosY-d,(int)  lastTickPosZ+z);
					    	return v;
					    }
					    
						}
				}	
				
		}
		
		return null;
	}

	
	//  convert planks to wood if need be.  
	void getAllNeededMaterialsFromChest(int chestX, int chestY, int chestZ)
	{
		TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity(chestX, chestY, chestZ);
		if (ch==null) return;
		for(int i=0;i<=8; i++)
		{
		ItemStack itmstk = getItemFromChest(ch, myBlockID[i], amtWanted[i]);
		if (itmstk!=null)
			{
			addtoOurInventory(myBlockID[i], itmstk.stackSize);
			defaultHoldItem=new ItemStack(Item.itemsList[myBlockID[i]],1);			
			}
		}
	}
	
	
	
	boolean getFirstNeededMaterialsFromChest(int chestX, int chestY, int chestZ)
	{
		TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity(chestX, chestY, chestZ);
		if (ch==null) return false;
		
		for (int i=8;i>=0; i--)
			if (amtNeeded[i]>0)
		{
		ItemStack itmstk = getItemFromChest(ch, myBlockID[i], amtWanted[i]);
		if (itmstk!=null)
			{
			addtoOurInventory(myBlockID[i], itmstk.stackSize);
			defaultHoldItem=new ItemStack(Item.itemsList[myBlockID[i]],1);
			return true;			
			}
		}
		


		return false;
	}	
	
	void placeAllMatsInChest()
	{
		//System.out.println("start place all mats back");
		if (myChestPos==null) 
			{
			myChestPos = scanForBlockNearEntity(mod_MineColony.hutBuilder.blockID, 20,5,20);
			
			if (myChestPos==null) return;
			}
		int chestX=(int) myChestPos.xCoord;
		int chestY=(int) myChestPos.yCoord;
		int chestZ=(int) myChestPos.zCoord;
		
		TileEntityChest ch = (TileEntityChest) world.getBlockTileEntity(chestX, chestY, chestZ);
		//System.out.println("putting back wood " + woodInInventory);
		
		for (int i=0;i<=8; i++)
		{
			if (amtInInventory[i]>0) putItemIntoChest(ch,myBlockID[i], amtInInventory[i]);
			amtInInventory[i]=0;
		}
		
	}
	
	
	
	private int getSignRotation(int x, int y, int z)
	{
	int answer=-1;
	if(worldObj.getBlockId(x, y, z)==Block.signPost.blockID)
	{
		TileEntitySign sign = (TileEntitySign)worldObj.getBlockTileEntity(x,y,z);
		if(sign==null)
			return answer;

		int rot = sign.getBlockMetadata();
		// 0 means sign faces to the west, so building goes to the east
		// 4 means sign faces north, so building goes to the south
		// 8 ... faces west, building goes west
		// 12 means sign faces south, so building goes north
		
	//	System.out.println("Sign metadata = " + rot);
		answer=rot/4;
	}
	return answer;
	}
	
	// northi is positive x
	// west is positive z
	
	
	private int getBlockValues(String fpSymbol)
	{
		int blockType;
		
		//String s = floorPlan.get(fpY)
		// fpSymbol
		// get string
		blockType=-1;
		// convert string to block values
		if (fpSymbol.equals("ww"))
			blockType=Block.planks.blockID;
		if (fpSymbol.startsWith("_"))
			blockType=Block.stairSingle.blockID;		
		if (fpSymbol.equals("uu"))
			blockType=Block.wood.blockID;		
		if (fpSymbol.equals("gg"))
			blockType=Block.glass.blockID;	
		if (fpSymbol.equals("cc"))
			blockType=Block.cobblestone.blockID;	
		if (fpSymbol.equals("CC"))
			blockType=Block.stone.blockID;
		if (fpSymbol.equals("sa"))
			blockType=Block.sand.blockID;		
		if (fpSymbol.equals("SS"))
			blockType=Block.sandStone.blockID;		
		if (fpSymbol.equals("bb"))
			blockType=Block.brick.blockID;
		if (fpSymbol.equals("oo"))
			blockType = Block.cloth.blockID;
		if (fpSymbol.startsWith("|"))
			blockType=Block.doorWood.blockID;
		if (fpSymbol.startsWith("^"))
			blockType=Block.stairCompactCobblestone.blockID;
		if (fpSymbol.startsWith("v"))
			blockType=Block.stairCompactPlanks.blockID;		
		if (fpSymbol.equals("fp"))
			blockType=Block.fence.blockID;		

		if (fpSymbol.equals(emptyBlock))
			blockType=0;  // remove it


		return blockType;
	}
	
	
	
	private int getMyBaseResourceIdx(String fpSymbol)
	{
		int blockType;
		
		//String s = floorPlan.get(fpY)
		// fpSymbol
		// get string
		blockType=-1;
		// convert string to block values
		if (fpSymbol.equals("ww"))
			blockType=1;
		if (fpSymbol.equals("_w"))
			blockType=1;
		if (fpSymbol.startsWith("v"))
			blockType=1	;
		if (fpSymbol.equals("fp"))
			blockType=1;
		if (fpSymbol.startsWith("|"))
			blockType=0;
		if (fpSymbol.startsWith("["))
			blockType=0;		
		if (fpSymbol.equals("uu"))
			blockType=0;		
		if (fpSymbol.equals("gg"))
			blockType=6;	
		if (fpSymbol.equals("cc"))
			blockType=2;	
		if (fpSymbol.startsWith("^"))
			blockType=2;
		if (fpSymbol.equals("_c"))
			blockType=2;			
		if (fpSymbol.equals("CC"))
			blockType=3;
		if (fpSymbol.equals("_C"))
			blockType=3;		
		if (fpSymbol.equals("sa"))
			blockType=4;		
		if (fpSymbol.equals("SS"))
			blockType=5;		
		if (fpSymbol.equals("_S"))
			blockType=5	;		
		if (fpSymbol.equals("bb"))
			blockType=7;
		if (fpSymbol.equals("oo"))
			blockType = 8;
		if (fpSymbol.equals(dontCare))
			blockType=-10;  // remove it

		return blockType;
	}	
	private int getBaseResourceID(String fpSymbol)
	{
		int blockType;
		
		//String s = floorPlan.get(fpY)
		// fpSymbol
		// get string
		blockType=-1;
		// convert string to block values
		if (fpSymbol.equals("ww"))
			blockType=Block.planks.blockID;
		if (fpSymbol.equals("_w"))
			blockType=Block.planks.blockID;		
		if (fpSymbol.startsWith("v"))
			blockType=Block.planks.blockID;;		
		if (fpSymbol.equals("fp"))
			blockType=Block.planks.blockID;
		if (fpSymbol.startsWith("|"))
			blockType=Block.wood.blockID;
		if (fpSymbol.startsWith("["))
			blockType=Block.wood.blockID;		
		if (fpSymbol.equals("uu"))
			blockType=Block.wood.blockID;		
		if (fpSymbol.equals("gg"))
			blockType=Block.glass.blockID;	
		if (fpSymbol.equals("cc"))
			blockType=Block.cobblestone.blockID;	
		if (fpSymbol.startsWith("^"))
			blockType=Block.cobblestone.blockID;
		if (fpSymbol.equals("_c"))
			blockType=Block.cobblestone.blockID;			
		if (fpSymbol.equals("CC"))
			blockType=Block.stone.blockID;
		if (fpSymbol.equals("_C"))
			blockType=Block.stone.blockID;		
		if (fpSymbol.equals("sa"))
			blockType=Block.sand.blockID;		
		if (fpSymbol.equals("SS"))
			blockType=Block.sandStone.blockID;		
		if (fpSymbol.equals("_S"))
			blockType=Block.sandStone.blockID;			
		if (fpSymbol.equals("bb"))
			blockType=Block.brick.blockID;
		if (fpSymbol.equals("oo"))
			blockType = Block.cloth.blockID;
		if (fpSymbol.equals(dontCare))
			blockType=0;  // remove it

		return blockType;
	}

	int getBaseBlockTypeFromBlockType(int blockType)
	{
		if ( blockType == Block.planks.blockID )
			return Block.wood.blockID;
		if ( blockType == Block.planks.blockID ||  blockType == Block.stairCompactPlanks.blockID || blockType == Block.signPost.blockID
				||  blockType == Block.doorWood.blockID)
			return Block.planks.blockID;
		if ((   blockType == Block.stairCompactCobblestone.blockID))
			return Block.cobblestone.blockID;
		if (  blockType == Block.sandStone.blockID)
			return Block.sand.blockID;

		return blockType;
	}

	
	
	// n = 0, e = 1, s= 2, w=3
	private int yawToOrdinalDirection(double yaw)
	{
	        int i1 = MathHelper.floor_double((double)(((yaw + 180F) * 4F) / 360F) - 0.5D) & 3;
            i1 = i1 - 1 & 3;
            
            // the above is from notch, it gives n=5, e = 6, w = 4, s =7
            // here we convert to our scheme
            
            return i1;
	}


		//          n 5
		//       w4     e6
		//          s 7i
		
		// facing south =7
		// facing North =5
		// facing West =4		
		// facing East =6			

	
	// facing: n = 0, e = 1, s= 2, w=3
	private void placeDoor(int x, int y, int z, int blockType, int facing)
	{
		    facing+=fpRotation+3;
		    facing = facing % 4;
		    if (facing==0) facing=7;
		    if (facing==1) facing=4;
		    if (facing==2) facing=5;
		    if (facing==3) facing=6;
		  	placeBlockAt(x,y,z, blockType);
  		  	world.setBlockMetadataWithNotify(x,y,z, facing);
  		  	
		  	placeBlockAt(x,y+1,z, blockType);
  		  	world.setBlockMetadataWithNotify(x,y+1,z, facing+8);  		  	
	}

	
	private void placeStairs(int x, int y, int z, int blockType, int facing)
	{
	    facing+=fpRotation+3;
	    facing = facing % 4;
	    
		if (facing==0) facing=0;
		    else
		    if (facing==1) facing=2;
		    else
		    if (facing==2) facing=1;
		    else
		    if (facing==3) facing=3;  
		  	placeBlockAt(x,y,z, blockType);
  		  	world.setBlockMetadataWithNotify(x,y,z, facing);
	}
	private void placeHalfblock(int x, int y, int z, int blockType, String fpSymbol)
	{
		placeBlockAt(x,y,z,blockType);
		if (fpSymbol.endsWith("C"))
		  world.setBlockMetadataWithNotify(x,y,z,0);
		if (fpSymbol.endsWith("w"))
			  world.setBlockMetadataWithNotify(x,y,z,2);
		if (fpSymbol.endsWith("c"))
			  world.setBlockMetadataWithNotify(x,y,z,3);
		if (fpSymbol.endsWith("t"))
			  world.setBlockMetadataWithNotify(x,y,z,1);
	}
	
	private void placeBlock()
	{
		if (blockType<0) return;

		if (fpSymbol.length()>0)
		{
	//		if (!(fpSymbol.equals(dontCare)))
	//			System.out.println("placing block " + fpSymbol + " at x=" + destX + " y=" + destY + " z=" + destZ);

			if (blockType>0)
				defaultHoldItem=new ItemStack(Item.itemsList[blockType],1);
			replaceWorldBlock(world.getBlockId((int) destX,(int) destY,(int) destZ), blockType);
			placeBlockAt((int) destX,(int) destY,(int) destZ, blockType);

			if (blockType == Block.doorWood.blockID)
			{
				try 
				{
					placeDoor((int) destX,(int) destY,(int) destZ, blockType, Integer.parseInt(fpSymbol.substring(1))-1);
				} 
				catch(Exception e) {}
			}
			else
				if (blockType == Block.stairCompactCobblestone.blockID)
				{
					try 
					{
						placeStairs((int) destX,(int) destY,(int) destZ, blockType, Integer.parseInt(fpSymbol.substring(1))-1);
					} 
					catch(Exception e) {}
				}				
				else
					if (blockType == Block.stairCompactPlanks.blockID)
					{
						try 
						{
							placeStairs((int) destX,(int) destY,(int) destZ, blockType, Integer.parseInt(fpSymbol.substring(1))-1);
						} 
						catch(Exception e) {}
					}
					else
						if (blockType == Block.stairSingle.blockID)
						{
							try 
							{
								placeHalfblock((int) destX,(int) destY,(int) destZ, blockType, fpSymbol);
							} 
							catch(Exception e) {}
						}				
		}
	}		

	
	private void getNextBlock()
	{
		incrFPBlock();
		if (floorPlan==null) return;
		
		while (fpSymbol.equals(dontCare) && fpIdx<floorPlan.size())
			incrFPBlock();
		if (fpIdx>=floorPlan.size())
		{
		//  we are done!
			floorPlan=null;
			fpSymbol="";
		}
	}
	
private void incrFPBlock()
{
    String fpStr;
	
	fpSymbol="";
	
    while (fpSymbol.equals("")  &&  floorPlan!=null &&  fpIdx<floorPlan.size()) 
    {

    	fpStr = floorPlan.get(fpIdx);
    	//System.out.println(fpIdx + " " + fpStr + " " + fpStr.length());
    	
    	if (isLevelSeperator(fpStr))  	
    	{
    		fpIdx++;
    		fpY++;
    		fpZ=0;
			if (fpIdx>=floorPlan.size())
				return;    		
    	}
    	else
    	{
    		fpX +=2;
    		if (fpX+1>=fpStr.length())
    		{
    			fpX=-2;
    			fpZ++;
    			fpIdx++;
    			if (fpIdx>=floorPlan.size())
    			{
    				return;
    			}
    		}
    		else
    		{
    			  fpSymbol = fpStr.substring(fpX,fpX+2);
    			  return;
    		}
    	}
    }
   

}
	

int getCntSymbolNeeded(String fps)
{
	int oldfpX=fpX, oldfpY=fpY, oldfpZ=fpZ, oldfpidx = fpIdx;
	String oldSymb= fpSymbol;
	int cnt=0;
	
	while (fpIdx<floorPlan.size())
	    {
		if (fpSymbol.startsWith(fps)) cnt++;
		
		incrFPBlock();
	    }
	
	fpX= oldfpX;
	fpY = oldfpY;
	fpZ = oldfpZ;
	fpIdx = oldfpidx;
	fpSymbol=oldSymb;
	return cnt;
}


ArrayList<String> readFloorPlanFromFile(String signText1, String signText2, String signText3)
	{
		ArrayList<String> fp;
		fp =new ArrayList<String>();
		String s="";
		
		try
		{
			mc_path = (new StringBuilder()).append(Minecraft.getMinecraftDir()).toString();
			mc_path = (new StringBuilder()).append(Minecraft.getMinecraftDir().getCanonicalPath()).toString();
			mc_path = mc_path + "\\resources\\floorplan\\" + signText2 + ".floorplan";
			//mc_path = (new StringBuilder()).append(Minecraft.getMinecraftDir().getCanonicalPath()).append("/1.floorplan").toString();
			//System.out.println(mc_path);
			FileReader fr = new FileReader(mc_path);
			BufferedReader br = new BufferedReader(fr);
			
			s=br.readLine();  // first line should be version of the file
			s=br.readLine();  // second line should be coordinates of the sign
			String[] tokens = s.split(" ");
			fpSignX = Integer.parseInt(tokens[0]);
			fpSignY = Integer.parseInt(tokens[1]);
			fpSignZ = Integer.parseInt(tokens[2]);
			
			while (s!=null)
			{
				s=br.readLine();
				
				if (s!= null)
					{ // strip comments
					s=stripComments(s);

					fp.add(s);
					}
			}
		}
		catch (Exception err)
		{
			signText1="error with floorplan";
		}
		finally
		{

		}
		return fp;
	}
	
	private boolean isLevelSeperator(String s)
	{
		return s.toLowerCase().startsWith("level");	
	}
	private String stripComments(String s)
	{
		if (s.contains("'"))
		s=s.substring(0, s.indexOf("'"));

	if (s.contains("//"))
		s=s.substring(0, s.indexOf("//"));
	return s;
	}
	

	boolean fpSignExists(int fpSignX, int fpSignY, int fpSignZ)
	{
		return (worldObj.getBlockId(fpSignX,fpSignY, fpSignZ)==Block.signPost.blockID);
	}
	
	
//////////////////////////////  npc message
	public boolean haveMessage()
	{
		return (signText1 != "" || signText2 != ""|| signText3 != "");
	}
	
	public boolean haveMessageSign()
	{
	return haveSign;	
	}
	
	public void displayMessage(String s1, String s2, String s3)
	{
	if (haveSign && s1.equals(signText1) && s2.equals(signText2) && s3.equals(signText3))
		return;
	if (haveSign)
		undisplayMessage();
	haveSign=true;
	
	signText1=s1;
	signText2=s2;
	signText3=s3;
	placeSign(mod_MineColony.hutBuilder.blockID, signText1, signText2, signText3);	
	}
	
	public void undisplayMessage()
	{
		haveSign=false;
		destroySign();
		signText1 = "";
		signText2 = "";
		signText3 = "";
	}
////////////////////////////
	
	
	
	protected int getDropItemId() {
		return Block.crate.blockID;
	}

	public void onDeath(Entity entity) {
		if(scoreValue > 0 && entity != null)
		{
			entity.addToPlayerScore(this, scoreValue);
		}
		undisplayMessage();
	//	field_9327_S = true;
		if(!worldObj.multiplayerWorld)
		{
		}

		for (int i=0; i<=8; i++)
			if (amtInInventory[i]>0)
				dropItem(myBlockID[i], amtInInventory[i]);
		
		worldObj.func_9425_a(this, (byte)3);
	}


	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		//nbttagcompound.setInteger("currentCheckPoint", currentCheckPoint);
		//nbttagcompound.setBoolean("goBack", goBack);

		for (int i=0; i<=8; i++)
			nbttagcompound.setInteger(inventoryResourceName[i], amtInInventory[i]);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		//currentCheckPoint = nbttagcompound.getInteger("currentCheckPoint");
		//goBack = nbttagcompound.getBoolean("goBack");

		for (int i=0; i<=8; i++)
			amtInInventory[i]=nbttagcompound.getInteger(inventoryResourceName[i]);
	}

}
