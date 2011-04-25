package net.minecraft.src;

import java.lang.Math;
import java.sql.Time;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.client.Minecraft;


/**
 *
 * @author heho
 */
public class EntityCitizen extends EntityWorker
{
	protected static final int maxNumberOfNeedings = 20;
	/*private static final int actionSeekForTree = 10;
	private static final int actionChop = 11;
	private static final int actionPlantTree = 12;*/

	protected static final int actionGoToBed = 6;
	protected static final int actionSleep = 7;
	protected static final int actionGetNeeding = 8;

	private int currentNeeding = -1;

	private int age;
	private byte sex;
	private byte level;
	private boolean asleep = false;

	private Item[][] needingPerLevel = new Item[4][maxNumberOfNeedings];
	private int[][] consumptionTimePerLevel = new int[4][maxNumberOfNeedings];
	private int[][] needingImportancePerLevel = new int[4][maxNumberOfNeedings];

	private long[] needNewItemTime = new long[maxNumberOfNeedings];

	private int[] baseHappinessPerLevel = {10, 100, 500, 3000};

	private float personalHappinessMultiplier;

	private Vec3D homePosition;
	private Vec3D bedPosition;
	//private Vec3D workPosition;

	public EntityCitizen(World world)
	{
		super(world);
		texture = "/mob/civilian1.png";
		setSize(0.9F, 1.3F);
		currentAction = actionIdle;
		defaultHoldItem = null;
		pathToEntity = null;
		destPoint = Vec3D.createVector(iPosX, iPosY, iPosZ);
		age = 30;
		sex = 1;
		level = -1;

		homePosition = null;
		bedPosition = null;
		personalHappinessMultiplier = 1F;

		for(int i = 0; i < maxNumberOfNeedings; i++)
		{
			needNewItemTime[i] = -1L;
		}

		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < maxNumberOfNeedings; j++)
			{
				needingPerLevel[i][j] = null;
				consumptionTimePerLevel[i][j] = 0;
				needingImportancePerLevel[i][j] = 0;
			}
		}

		needingPerLevel[0][0] = Item.bread;
		consumptionTimePerLevel[0][0] = 100;
		needingImportancePerLevel[0][0] = 20;

		
		changeLevel(0);
	}

	public void changeLevel(int newLevel)
	{
		this.level = (byte)newLevel;
		for(int i = 0; i < maxNumberOfNeedings; i++)
		{
			if(consumptionTimePerLevel[this.level][i] != 0 && needNewItemTime[i] == -1L)
			{
				needNewItemTime[i] = 0L;
			}
		}
	}

	public void setHomePosition(double x, double y, double z) {
		this.homePosition = Vec3D.createVectorHelper(x, y, z);
	}

	public void setBedPosition(double x, double y, double z) {
		this.bedPosition = Vec3D.createVectorHelper(x, y, z);
	}

	protected void workerUpdate()
	{
		TileEntityChest tileentitychest;
		//nextBed= scanForBlockNearEntity(Block.blockBed.blockID, 20, 20, 20);
		//destPoint = nextBed;

		Vec3D entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);

		if(!asleep && currentNeeding != -1)
		{
			long currentTime = world.worldInfo.getWorldTime();
			for(int i = 0; i < maxNumberOfNeedings; i++)
			{
				if(needNewItemTime[i] == -1L)
				{
					continue;
				}
				
				if((needNewItemTime[i] - currentTime) < (consumptionTimePerLevel[this.level][i] / 3))
				{
					currentAction = actionGetNeeding;
					currentNeeding = i;
				}
			}
		}
		
		
		switch(currentAction)
		{
			case actionIdle:
				if(!worldObj.isDaytime())
				{
					currentAction = actionGoToBed;
				}
			break;
			
			case actionGoToBed:
				if(bedPosition != null)
				{
					speed = (float) 1.0;
					destPoint = bedPosition;

					if(entVec.distanceTo(destPoint) < 2)
					{
						currentAction = actionSleep;
					}
				}
				if(worldObj.isDaytime())
				{
					currentAction = actionIdle;
				}
			break;

			case actionSleep:
				asleep = true;
				speed = (float) 0.0F;
				motionX = motionZ = motionY = 0.0D;
				destPoint = null;
				if(worldObj.isDaytime())
				{
					asleep = false;
					currentAction = actionIdle;
				}
			break;

			case actionGetNeeding:
				tileentitychest = (TileEntityChest) worldObj
						.getBlockTileEntity(
								MathHelper.floor_double(homePosition.xCoord),
								MathHelper.floor_double(homePosition.yCoord),
								MathHelper.floor_double(homePosition.zCoord));
				for(int i = 0; i < tileentitychest.getSizeInventory(); i++)
				{
					if(tileentitychest.getStackInSlot(i).getItem() == needingPerLevel[this.level][currentNeeding])
					{
						destPoint = homePosition;
						currentAction = actionGetEquipment;
					}
				}
			break;

			case actionGetEquipment:
				speed = (float) 1.0;
				destPoint = bedPosition;

				if(entVec.distanceTo(destPoint) < 2)
				{
					tileentitychest = (TileEntityChest) worldObj
						.getBlockTileEntity(
								MathHelper.floor_double(homePosition.xCoord),
								MathHelper.floor_double(homePosition.yCoord),
								MathHelper.floor_double(homePosition.zCoord));

					if(getItemFromChest(tileentitychest, needingPerLevel[this.level][currentNeeding].shiftedIndex, 1) != null)
					{
						fulfillNeeding(currentNeeding);
						currentNeeding = -1;
					}

					currentAction = actionIdle;
				}
			break;

			default:
				currentAction = actionIdle;
			break;
		}
	}

	/*public void setPosition(double d, double d1, double d2)
    {
        posX = d;
        posY = d1;
        posZ = d2;
        float f = width / 2.0F;
        float f1 = height;
        boundingBox.setBounds(d - (double)f, (d1 - (double)yOffset) + (double)ySize, d2 - (double)f, d + (double)f, (d1 - (double)yOffset) + (double)ySize + (double)f1, d2 + (double)f);
    }*/

	protected void fulfillNeeding(int i)
	{
		needNewItemTime[i] = world.worldInfo.getWorldTime() + consumptionTimePerLevel[this.level][i];
	}

	public void onDeath(Entity entity)
	{
		destroySign();
		if(scoreValue > 0 && entity != null)
        {
            entity.addToPlayerScore(this, scoreValue);
        }
        unused_flag = true;
        if(!worldObj.multiplayerWorld)
        {
            if(toolsList[0]!=null)
            	dropItem(toolsList[0].itemID, 1);
        }
        worldObj.func_9425_a(this, (byte)3);
	}

	protected int getHappiness()
	{
		int needingBasedHappiness = 0;

		int i = 0;
		while(needingPerLevel[this.level][i] != null)
		{
			needingBasedHappiness += (int)(needingImportancePerLevel[this.level][i] * getSatisfactionOf(i));
		}

		return (int)(personalHappinessMultiplier * (baseHappinessPerLevel[this.level] + needingBasedHappiness));
	}

	protected float getSatisfactionOf(int i)
	{
		return java.lang.Math.min(((needNewItemTime[i] - (consumptionTimePerLevel[this.level][i] / 5)) / consumptionTimePerLevel[this.level][i] * 5), 1F);

	}

	protected int getTaxes(int tax)
	{
		if(tax <= 0)
		{
			return 0;
		}
		//dont pay taxes if the tax is higher than the doubled happiness and decrease the happiness multiplier
		if(tax > (getHappiness() * 2))
		{
			tax = 0;
			personalHappinessMultiplier = java.lang.Math.max(0F, (personalHappinessMultiplier - 0.2F));
		}
		//pay taxes if tax is between 1.5 and 2 but decrease the multiplier
		else if(tax > (getHappiness() * 1.5))
		{
			personalHappinessMultiplier = java.lang.Math.max(0F, (personalHappinessMultiplier - 0.1F));
		}
		//pay taxes if tax is between 1.5 and 2 but decrease the multiplier
		else if(tax > (getHappiness()))
		{
			personalHappinessMultiplier = java.lang.Math.max(0F, (personalHappinessMultiplier - 0.05F));
		}
		//just pay taxes if the tax is between 0.5 and 1
		else if(tax > (getHappiness() * 0.5))
		{
			
		}
		//pay taxes if tax is between 0 and 0.5 and increase the multiplier
		else
		{
			personalHappinessMultiplier = java.lang.Math.min(1.2F, (personalHappinessMultiplier + 0.1F));
		}

		return tax;
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);

		StringBuilder name;
		for(int i = 0; i < 20; i++)
		{
			name = new StringBuilder("needNewItemTime_").append(i);
			nbttagcompound.setLong(name.toString(), this.needNewItemTime[i]);
		}

		nbttagcompound.setInteger("currentAction", this.currentAction);
		nbttagcompound.setInteger("age", this.age);
		nbttagcompound.setByte("sex", this.sex);
		nbttagcompound.setByte("level", this.level);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);

		StringBuilder name;
		for(int i = 0; i < 20; i++)
		{
			name = new StringBuilder("needNewItemTime_").append(i);
			this.needNewItemTime[i] = nbttagcompound.getLong(name.toString());
		}

		this.currentAction = nbttagcompound.getInteger("currentAction");
		this.age = nbttagcompound.getInteger("age");
		this.sex = nbttagcompound.getByte("sex");
		this.level = nbttagcompound.getByte("level");
	}
}
