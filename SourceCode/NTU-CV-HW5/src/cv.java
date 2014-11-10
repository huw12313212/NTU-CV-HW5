


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class cv {

	public static void main(String[] args) throws IOException {

		 String fileName = "./assets/lena.im";
		 int headerLength = 172;
		 int imageWidth = 512;
		 int imageHeight = 512;
		 int threshold = 128;
		 
		 Kernel octogonKernel = new Kernel(new int[][]{
				  { 0,1,1,1,0},
				  { 1,1,1,1,1},
				  { 1,1,1,1,1},
				  { 1,1,1,1,1},
				  { 0,1,1,1,0}
				},2,2);


		 ArrayList<Integer> bytes = GetByteData(fileName);
		 ArrayList<Integer> dialation = Dilation(bytes,headerLength,imageWidth,imageHeight,octogonKernel);
		 ArrayList<Integer> erosion = Erosion(bytes,headerLength,imageWidth,imageHeight,octogonKernel);
		 ArrayList<Integer> opening = Dilation(erosion,headerLength,imageWidth,imageHeight,octogonKernel);
		 ArrayList<Integer> closing = Erosion(dialation,headerLength,imageWidth,imageHeight,octogonKernel);
		 

		 WriteOut(dialation,"./assets/dialation.im");
		 WriteOut(erosion,"./assets/erosion.im");
		 WriteOut(opening,"./assets/opening.im");
		 WriteOut(closing,"./assets/closing.im");
	}
	
	public static ArrayList<Integer> Erosion(ArrayList<Integer> origin,int headerLength, int width, int height,Kernel kernel)
	{
		ArrayList<Integer> results = InitWhite(origin,headerLength,width,height);
		
		for(int y = 0 ; y < height - (kernel.GetHeight()-1) ; y++)
		{
			for(int x = 0 ; x<width - (kernel.GetWidth()-1); x++)
			{
				boolean validate = true;
				int min = 255;
				
				for(int y2 = 0 ; y2 < kernel.GetHeight() ; y2 ++)
				{
					for(int x2 =0 ; x2 < kernel.GetWidth() ;x2 ++)
					{
						if(kernel.Data[y2][x2]==1)
						{
							int globalX = x + x2;
							int globalY = y + y2;
							int globalIndex = headerLength+globalY * width + globalX;
							
							if(min>origin.get(globalIndex))min = origin.get(globalIndex);
						}
					}
				}
				
				int globalX = x + kernel.OriginX;
				int globalY = y + kernel.OriginY;
				int globalIndex = headerLength+globalY * width + globalX;
				results.set(globalIndex, min);

			}
		}
		
		return results;
	}
	
	
	
	
	public static ArrayList<Integer> Dilation(ArrayList<Integer> origin,int headerLength, int width, int height,Kernel kernel)
	{
		ArrayList<Integer> results = InitWhite(origin,headerLength,width,height);
		
		for(int y = 0 ; y < height ; y++)
		{
			for(int x = 0 ; x<width ; x++)
			{
				int index = headerLength+width*y+x;
				int max = 0;
				
					for(int y2 = 0 ; y2 < kernel.GetHeight() ; y2++)
					{
						for( int x2 = 0 ; x2 < kernel.GetWidth() ; x2++)
						{
							int localX = x2 - kernel.OriginX;
							int localY = y2 - kernel.OriginY;
							
							int globalX = x + localX;
							int globalY = y + localY;
							
							if(globalX<0)continue;
							if(globalX>=width)continue;
							if(globalY<0)continue;
							if(globalY>=height)continue;
							
							int globalIndex = headerLength + globalY*height + globalX;
							
							if(kernel.Data[y2][x2]==1)
							{
								if(max < origin.get(globalIndex))
								{
									max = origin.get(globalIndex);
								}
							}
						}
						
					results.set(index, max);
				}
			}
		}
		
		return results;
	}
	
	public static ArrayList<Integer> InitWhite(ArrayList<Integer> origin,int headerLength, int width, int height)
	{
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		for(int i = 0 ; i < headerLength ; i++)
		{
			results.add(origin.get(i));
		}
		
		for(int i = 0 ; i < width ; i ++)
		{
			for(int j = 0 ; j<height ; j++)
			{
				results.add(0);
			}
		}
		
		return results;
	}
	
	public static ArrayList<Integer> Binarize(ArrayList<Integer> origin,int threshold,int headerLength, int width, int height)
	{
		ArrayList<Integer> results = InitWhite(origin,headerLength,width,height);
		for(int i = 0 ; i < width ; i ++)
		{
			for(int j = 0 ; j<height ; j++)
			{
				int index = headerLength+width*i+j;
				int originData = origin.get(index);
				
				if(originData < threshold)originData = 0;
				else originData = 255;
				results.set(index, originData);
			}
		}
		return results;
		
	}
	
	public static ArrayList<Integer> GetByteData(String fileName) throws IOException
	{
		 File f = new File(fileName);
		 ArrayList<Integer> bytes = new ArrayList<Integer>();
		
		 //System.out.println("file exist:"+f.exists());
		
		 FileInputStream in = null;	
		 in = new FileInputStream(fileName);
		 
		 int c;
		 while ((c = in.read()) != -1) {
			 bytes.add(c);
        }
		 
		 return bytes;
	}
	
	public static void WriteOut(ArrayList<Integer> data,String name) throws IOException
	{
		File f = new File(name);
		if(f.exists())f.delete();
		FileOutputStream out = null;
		out = new FileOutputStream(name);
		
		for(int i : data)
		{
			out.write((byte)i);
		}
		
		out.flush();
		out.close();
		
	}
	

}
