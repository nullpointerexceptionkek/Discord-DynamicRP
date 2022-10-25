package lee.aspect.dev.discordrpc;


public class Updates {

    private long wait = 3000;

    private String image, imagetext, smallimage, smalltext, fl, sl, button1Text, button1Url, button2Text, button2Url;


    public Updates(long time, String image, String imagetext, String smallimage, String smalltext, String f1, String sl, String button1Text, String button1Url, String button2Text, String button2Url) {
        this.wait = time;
        this.fl = f1;
        this.sl = sl;
        this.image = image;
        this.imagetext = imagetext;
        this.smallimage = smallimage;
        this.smalltext = smalltext;
        this.button1Text = button1Text;
        this.button1Url = button1Url;
        this.button2Url = button2Url;
        this.button2Text = button2Text;

    }

    public Updates(long time, String image, String imagetext, String smallimage, String smalltext, String f1, String sl) {
        this.wait = time;
        this.fl = f1;
        this.sl = sl;
        this.image = image;
        this.imagetext = imagetext;
        this.smallimage = smallimage;
        this.smalltext = smalltext;
    }

    public Updates() {
    }

    public long getWait() {
        return wait;
    }

    public void setWait(long wait) {
        this.wait = wait;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagetext() {
        return imagetext;
    }

    public void setImagetext(String imagetext) {
        this.imagetext = imagetext;
    }

    public String getSmallimage() {
        return smallimage;
    }

    public void setSmallimage(String smallimage) {
        this.smallimage = smallimage;
    }

    public String getSmalltext() {
        return smalltext;
    }

    public void setSmalltext(String smalltext) {
        this.smalltext = smalltext;
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getButton1Text() {
        return button1Text;
    }

    public void setButton1Text(String button1Text) {
        this.button1Text = button1Text;
    }

    public String getButton1Url() {
        return button1Url;
    }

    public void setButton1Url(String url) {
        this.button1Url = url;
    }

    public String getButton2Text() {
        return button2Text;
    }

    public void setButton2Text(String button2Text) {
        this.button2Text = button2Text;
    }

    public String getButton2Url() {
        return button2Url;
    }

    public void setButton2Url(String url) {
        this.button2Url = url;
    }

    @Override
    public String toString() {
        return wait + ", " + image + ", " + imagetext + ", " + smallimage + ", " + smalltext + ", " + fl + ", " + sl;
    }

}
