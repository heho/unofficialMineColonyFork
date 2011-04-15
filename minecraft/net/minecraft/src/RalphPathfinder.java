package net.minecraft.src;

public class RalphPathfinder {
    int pathSegsToTry;
    public int maxPathSegsToTry=2000;
    public RalphPathFinderBlockWeights getBlockWeights=null; 
    
    public RalphPathfinder(IBlockAccess iblockaccess)
    {
        path = new RalphPath();
        pointMap = new MCHashTable();
        pathOptions = new PathPoint[32];
        worldMap = iblockaccess;
    }

    public RalphPathfinder(IBlockAccess iblockaccess, RalphPathFinderBlockWeights w)
    {
        path = new RalphPath();
        pointMap = new MCHashTable();
        pathOptions = new PathPoint[32];
        worldMap = iblockaccess;
        getBlockWeights=w;
    }    
    
    
    public RalphPathEntity createEntityPathTo(Entity entity, Entity entity1, float f)
    {
        return createEntityPathTo(entity, entity1.posX, entity1.boundingBox.minY, entity1.posZ, f);
    }

    public RalphPathEntity createEntityPathTo(Entity entity, int i, int j, int k, float f)
    {
        return createEntityPathTo(entity, (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, f);
    }

    public RalphPathEntity createEntityPathTo(Entity entity, int x, int y, int z, double d, double d1, double d2, 
            float f)
    {
        path.clearPath();
        pointMap.clearMap();
        PathPoint pathpoint = openPoint(x, y, z);
        
        // since y may be up in the air ... jumping, we find a suitable y
        pathpoint = openPoint(x, findGroundLevel(x, y, z), z);

        PathPoint pathpoint1 = openPoint(MathHelper.floor_double(d - (double)(entity.width / 2.0F)), MathHelper.floor_double(d1), MathHelper.floor_double(d2 - (double)(entity.width / 2.0F)));
        PathPoint pathpoint2 = new PathPoint(MathHelper.floor_float(entity.width + 1.0F), MathHelper.floor_float(entity.height + 1.0F), MathHelper.floor_float(entity.width + 1.0F));
        if (debug) System.out.println("tyring to a* path from " + pathpoint + " to " + pathpoint1);

      //  for (int j=-1; j<2; j++)
        //    for (int i=-2; i<3; i++)
          //      for (int k=-2; k<3; k++)
            //    	System.out.println("neighbor block values of " + (x+i) + "y=" + (y+j) + "z=" + (k+z) + " is " + worldMap.getBlockId(x+i, y+j, z+k) + " " + worldMap.getBlockMaterial(x+i, y+j, z+k));
                
        RalphPathEntity pathentity = addToPath(entity, pathpoint, pathpoint1, pathpoint2, f);
        return pathentity;
    }
    
    
    private RalphPathEntity createEntityPathTo(Entity entity, double d, double d1, double d2, 
            float f)
    {
        path.clearPath();
        pointMap.clearMap();
        PathPoint pathpoint = openPoint(MathHelper.floor_double(entity.boundingBox.minX), MathHelper.floor_double(entity.boundingBox.minY), MathHelper.floor_double(entity.boundingBox.minZ));
        PathPoint pathpoint1 = openPoint(MathHelper.floor_double(d - (double)(entity.width / 2.0F)), MathHelper.floor_double(d1), MathHelper.floor_double(d2 - (double)(entity.width / 2.0F)));
        PathPoint pathpoint2 = new PathPoint(MathHelper.floor_float(entity.width + 1.0F), MathHelper.floor_float(entity.height + 1.0F), MathHelper.floor_float(entity.width + 1.0F));
        if (debug) System.out.println("tyring to a* path from " + pathpoint + " to " + pathpoint1);

        RalphPathEntity pathentity = addToPath(entity, pathpoint, pathpoint1, pathpoint2, f);
        return pathentity;
    }

    private RalphPathEntity addToPath(Entity entity, PathPoint startPoint, PathPoint destPoint, PathPoint pathWidth, float f)
    {
    	startPoint.totalPathDistance = 0.0F;
    	startPoint.distanceToNext = startPoint.distanceTo(destPoint);
    	startPoint.distanceToTarget = startPoint.distanceToNext;
        path.clearPath();
        path.addPoint(startPoint);
        PathPoint furthestPtReached = startPoint;
        pathSegsToTry=0;
        while(!path.isPathEmpty()  &&  pathSegsToTry<maxPathSegsToTry) 
        {
        	pathSegsToTry++;
            PathPoint pathpoint4 = path.dequeue();
            if (debug)
              System.out.println("looking for neighbors of " + pathpoint4 + " and trying to reach " + destPoint);
            if(pathpoint4.equals(destPoint)  ||  pathpoint4.distanceTo(destPoint)<=1)  // was 2
            {
                return createEntityPath(startPoint,  pathpoint4);
            }
            if(pathpoint4.distanceTo(destPoint) < furthestPtReached.distanceTo(destPoint))
            {
                furthestPtReached = pathpoint4;
         
            }

           if (pathpoint4.xCoord==-92  && pathpoint4.zCoord==201)
            {
       	  // debug=true;
           // 	System.out.println("debug");
            //     for (int j=-1; j<2; j++)
            //        for (int i=-1; i<2; i++)
              //          for (int k=-1; k<2; k++)
              //         	System.out.println("neighbor block values of " + (pathpoint4.xCoord+i) + "y=" + (pathpoint4.yCoord+j) + "z=" + (k+pathpoint4.zCoord) + " is " + worldMap.getBlockId(pathpoint4.xCoord+i, pathpoint4.yCoord+j, pathpoint4.zCoord+k) + " " + worldMap.getBlockMaterial(pathpoint4.xCoord+i, pathpoint4.yCoord+j, pathpoint4.zCoord+k));
         
            }
            pathpoint4.isFirst = true;
            int i = findPathOptionsR1(entity, pathpoint4, pathWidth, destPoint, f);
            //i = findPathOptions(entity, pathpoint4, pathWidth, destPoint, f);
            int j = 0;
            while(j < i) 
            {
                PathPoint pathpoint5 = pathOptions[j];
                
                float f1;
                
                if (getBlockWeights==null)
                  f1 = pathpoint4.totalPathDistance + pathpoint4.distanceTo(pathpoint5);
                else
                  f1 = pathpoint4.totalPathDistance + pathpoint4.distanceTo(pathpoint5)*(float) getBlockWeights.getWeight(pathpoint5.xCoord, pathpoint5.yCoord, pathpoint5.zCoord);
                
                if(!pathpoint5.isAssigned() || f1 < pathpoint5.totalPathDistance)
                {
                    pathpoint5.previous = pathpoint4;
                    pathpoint5.totalPathDistance = f1;  // total to get to that point
                    pathpoint5.distanceToNext = pathpoint5.distanceTo(destPoint); //estimate for rest of trip
                    if(pathpoint5.isAssigned())
                    {
                        path.changeDistance(pathpoint5, pathpoint5.totalPathDistance + pathpoint5.distanceToNext);
                    } else
                    {
                        pathpoint5.distanceToTarget = pathpoint5.totalPathDistance + pathpoint5.distanceToNext;  //estimate of distance via this point? (and what we sort on)
                        if (debug)
                        	System.out.println("adding neighbor for consideration " + pathpoint5 + " dist est via this pt(s)=" + pathpoint5.distanceToTarget + " dist to dest " + pathpoint5.distanceToNext + " dist to get to this point=" + pathpoint5.totalPathDistance);
                        path.addPoint(pathpoint5);
                    }
                }
                j++;
            }
           if (debug)
        	   System.out.println(path.toString());
        }
        
        //not reached, so either return null or the best so far.
        if(furthestPtReached == startPoint)
        {
 
            return null;
        } else
        {

            return createEntityPath(startPoint, furthestPtReached);
        }
    }

    
    private int findPathOptionsR1(Entity entity, PathPoint strtPt, PathPoint sizePoint, PathPoint destpoint2, float f)
    {
    	int i=0;
        PathPoint p = getPossibleNeighbor(strtPt,strtPt.xCoord+1, strtPt.yCoord, strtPt.zCoord);
        if (p!=null)
        	{
        	pathOptions[i]=p;
            i++;
            }
        p = getPossibleNeighbor(strtPt,strtPt.xCoord-1, strtPt.yCoord, strtPt.zCoord);
        if (p!=null)
        	{
        	pathOptions[i]=p;
            i++;
            }     
         p = getPossibleNeighbor(strtPt,strtPt.xCoord, strtPt.yCoord, strtPt.zCoord+1);
        if (p!=null)
        	{
        	pathOptions[i]=p;
            i++;
            }
        p = getPossibleNeighbor(strtPt, strtPt.xCoord, strtPt.yCoord, strtPt.zCoord-1);
        if (p!=null)
        	{
        	pathOptions[i]=p;
            i++;
            }         
        return i;
    }
    
    boolean isGoodForMeStandOn(int x, int y, int z)
    {
    	if (debug) System.out.println("is good to stand on? block value of " + x + " " + y + " " + z + " is " + worldMap.getBlockId(x, y ,z));
    	return (worldMap.getBlockId(x, y ,z)!= 0);
    }
    boolean isPassable(int x, int y, int z)
    {
    	if (debug) System.out.println("is passable block value of " + x + " " + y + " " + z + " is " + worldMap.getBlockId(x, y ,z));
    	//return (worldMap.getBlockId(x, y ,z)== 0  ||   worldMap.getBlockId(x, y ,z)==50);
    	Material material = worldMap.getBlockMaterial(x, y, z);
        return !material.getIsSolid();
    }
    
    PathPoint getPossibleNeighbor(PathPoint strtPt, int x, int y, int z)
    {
        PathPoint answer = null;
        int b=worldMap.getBlockId(x, y,z);

    
        if (debug) System.out.println("trying to jump down");
        if (isGoodForMeStandOn(x, y-2,z) &&  isPassable(x, y-1, z) && isPassable(x, y, z) && isPassable(x, y+1, z))
		{  // can jump this neighbor
    		answer= openPoint(x, y-1, z);
    		if (!answer.isFirst)
		    	return answer;
		}
        if (debug) System.out.println("trying to walk straight accross");        
        if (isGoodForMeStandOn(x, y-1, z) &&  isPassable(x, y, z) && isPassable(x, y+1, z) )
		{  // can jump this neighbor
    		answer= openPoint(x, y, z);
  		
    		if (!answer.isFirst)
		    	return answer;
		}
        if (debug) System.out.println("trying to jump up");  
        if (isGoodForMeStandOn(x, y, z) &&  isPassable(x, y+1, z) && isPassable(x, y+2, z) 
        		&&  isPassable(strtPt.xCoord, strtPt.yCoord+2, strtPt.zCoord))
		{  // can jump this neighbor
    		answer= openPoint(x, y+1, z);

    		if (!answer.isFirst)
		    	return answer;
		}

        	return null;
    }
    
    
    private int findPathOptions(Entity entity, PathPoint pathpoint, PathPoint pathpoint1, PathPoint pathpoint2, float f)
    {
        int i = 0;
        int j = 0;
        if(getVerticalOffset(entity, pathpoint.xCoord, pathpoint.yCoord + 1, pathpoint.zCoord, pathpoint1) > 0)
        {
            j = 1;
        }
        PathPoint pathpoint3 = getSafePoint(entity, pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord + 1, pathpoint1, j);
        PathPoint pathpoint4 = getSafePoint(entity, pathpoint.xCoord - 1, pathpoint.yCoord, pathpoint.zCoord, pathpoint1, j);
        PathPoint pathpoint5 = getSafePoint(entity, pathpoint.xCoord + 1, pathpoint.yCoord, pathpoint.zCoord, pathpoint1, j);
        PathPoint pathpoint6 = getSafePoint(entity, pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord - 1, pathpoint1, j);
        if(pathpoint3 != null && !pathpoint3.isFirst && pathpoint3.distanceTo(pathpoint2) < f)
        {
            pathOptions[i++] = pathpoint3;
        }
        if(pathpoint4 != null && !pathpoint4.isFirst && pathpoint4.distanceTo(pathpoint2) < f)
        {
            pathOptions[i++] = pathpoint4;
        }
        if(pathpoint5 != null && !pathpoint5.isFirst && pathpoint5.distanceTo(pathpoint2) < f)
        {
            pathOptions[i++] = pathpoint5;
        }
        if(pathpoint6 != null && !pathpoint6.isFirst && pathpoint6.distanceTo(pathpoint2) < f)
        {
            pathOptions[i++] = pathpoint6;
        }
        return i;
    }

    private PathPoint getSafePoint(Entity entity, int i, int j, int k, PathPoint pathpoint, int l)
    {
        PathPoint pathpoint1 = null;
        if(getVerticalOffset(entity, i, j, k, pathpoint) > 0)
        {
            pathpoint1 = openPoint(i, j, k);
        }
        if(pathpoint1 == null && l > 0 && getVerticalOffset(entity, i, j + l, k, pathpoint) > 0)
        {
            pathpoint1 = openPoint(i, j + l, k);
            j += l;
        }
        if(pathpoint1 != null)
        {
            int i1 = 0;
            for(int j1 = 0; j > 0 && (j1 = getVerticalOffset(entity, i, j - 1, k, pathpoint)) > 0; j--)
            {
                if(j1 < 0)
                {
                    return null;
                }
                if(++i1 >= 4)
                {
                    return null;
                }
            }

            if(j > 0)
            {
                pathpoint1 = openPoint(i, j, k);
            }
        }
        return pathpoint1;
    }

    private final PathPoint openPoint(int i, int j, int k)
    {
        int l = PathPoint.func_22329_a(i, j, k);
        PathPoint pathpoint = (PathPoint)pointMap.lookup(l);
        if(pathpoint == null)
        {
            pathpoint = new PathPoint(i, j, k);
            pointMap.addKey(l, pathpoint);
        }
        return pathpoint;
    }

    private int getVerticalOffset(Entity entity, int i, int j, int k, PathPoint pathpoint)
    {
        for(int l = i; l < i + pathpoint.xCoord; l++)
        {
            for(int i1 = j; i1 < j + pathpoint.yCoord; i1++)
            {
                for(int j1 = k; j1 < k + pathpoint.zCoord; j1++)
                {
                    Material material = worldMap.getBlockMaterial(l, i1, j1);
                    if(material.getIsSolid())
                    {
                        return 0;
                    }
                    if(material == Material.water || material == Material.lava)
                    {
                        return -1;
                    }
                }

            }

        }

        return 1;
    }

    private RalphPathEntity createEntityPath(PathPoint pathpoint, PathPoint pathpoint1)
    {
        int i = 1;
        if (debug) 
        	System.out.println("");
        
        for(PathPoint pathpoint2 = pathpoint1; pathpoint2.previous != null; pathpoint2 = pathpoint2.previous)
        {
            i++;
        }

        PathPoint apathpoint[] = new PathPoint[i];
        PathPoint pathpoint3 = pathpoint1;
        for(apathpoint[--i] = pathpoint3; pathpoint3.previous != null; apathpoint[--i] = pathpoint3)
        {
            pathpoint3 = pathpoint3.previous;
        }

        return new RalphPathEntity(apathpoint);
    }

    public int findGroundLevel(int x, int y, int z)
    {
        for (int j=0; j<100; j++)
            if  (isGoodForMeStandOn(x, y-j-1, z))
          {
          	return y-j;
          }
        return y;
    }
    
    
    public boolean debug=false;
    private IBlockAccess worldMap;
    private RalphPath path;
    private MCHashTable pointMap;
    private PathPoint pathOptions[];
}
