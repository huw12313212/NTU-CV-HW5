

public class Kernel {
	
	public final int Data[][];
	public final int OriginX;
	public final int OriginY;
	
	public Kernel(int[][] data,int originX,int originY)
	{
		Data = data;
		OriginX = originX;
		OriginY = originY;
	}
	
	public int GetHeight()
	{
		return Data.length;
	}
	
	public int GetWidth()
	{
		return Data[0].length;
	}
	
	

}
