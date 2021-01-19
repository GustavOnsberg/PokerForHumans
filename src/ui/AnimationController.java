package ui;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class AnimationController {
    ArrayList<Anim> animList = new ArrayList<Anim>();

    public void main(String[] args){
        for (int i = 0; i < animList.size(); i++) {

        }
    }
    public void newAnimation(int startT, int endT, int startX, int startY, int endX, int endY){
        animList.add(new Anim( startT,  endT,  startX ,  startY,  endX,  endY));
    }
    
}
class Anim{
    private final int startT;
    private final int startX;
    private final int endT;
    private final int startY;
    private final int endX;
    private final int endY;

    Anim(int startT, int endT, int startX , int startY, int endX, int endY){
        this.startT = startT;
        this.endT = endT;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
