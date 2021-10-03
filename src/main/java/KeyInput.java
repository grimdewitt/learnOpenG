import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class KeyInput implements KeyListener {
    private static char keyPressed;

    public static char getKeyPressed() {
        return keyPressed;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPressed = e.getKeyChar();
        //System.out.println(keyPressed);
        switch (keyPressed){
            case ('w'):
                GameEventListener.eyeY-=0.1f;
                break;
            case ('s'):
                GameEventListener.eyeY+=0.1;
                break;
            case ('a'):
                GameEventListener.eyeX+=0.1;
                break;
            case ('d'):
                GameEventListener.eyeX-=0.1;
                break;
            case ('q'):
                GameEventListener.units+=1;
                break;
            case ('e'):
                GameEventListener.units-=1;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
