package lee.aspect.dev.discordrpc;


public class Updates {

	private long wait = 3000;
	
	private String image, imagetext, smallimage, smalltext,fl,sl;
	
	
	public Updates(long time, String image, String imagetext, String smallimage, String smalltext, String f1, String sl) {
		this.wait = time;
		this.fl = f1;
		this.sl = sl;
		this.image = image;
		this.imagetext = imagetext;
		this.smallimage = smallimage;
		this.smalltext = smalltext;
		
	}
	
	public Updates(long time, String f1, String sl) {
		this.wait = time;
		this.fl = f1;
		this.sl = sl;
		this.image = null;
		
	} 
	
	public long getWait() {
		return wait;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getImagetext() {
		return imagetext;
	}
	
	public String getSmallimage() {
		return smallimage;
	}
	
	public String getSmalltext() {
		return smalltext;
	}
	
	public String getFl() {
		return fl;
	}
	
	public String getSl() {
		return sl;
	}
	
	public static Updates fromUpdates(long time, String fline, String sline) {
		return new Updates(time,fline,sline);
	}
	
	public static Updates fromUpdates(long time, String image, String imagetext, String smallimage, String smalltext, String f1, String sl) {
		return new Updates(time,image,imagetext,smallimage,smalltext,f1,sl);
	}
	
	@Override
	public String toString() {
		return wait + ", " + image +", " + imagetext + ", " + smallimage + ", " +smalltext + ", " + fl + ", " + sl;
	}
	
}
