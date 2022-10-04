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

	public Updates() {}
	
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

	public void setWait(long wait) {
		this.wait = wait;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setImagetext(String imagetext) {
		this.imagetext = imagetext;
	}

	public void setSmallimage(String smallimage) {
		this.smallimage = smallimage;
	}

	public void setSmalltext(String smalltext) {
		this.smalltext = smalltext;
	}

	public void setFl(String fl) {
		this.fl = fl;
	}

	public void setSl(String sl) {
		this.sl = sl;
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
