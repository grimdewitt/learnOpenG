import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

public class Renderer {
    private static GLWindow glWindow;
    private static GLProfile profile;

    public static int screenHeight = 480;
    public static int screenWidth = 640;

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getWindowWidth(){
        return glWindow.getWidth();
    }

    public static int getWindowHeight(){
        return glWindow.getHeight();
    }

    public static GLProfile getGLProfile(){ return profile;}

    //инициализация профиля и создание glWindow
    public static void init() {
        profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        glWindow = GLWindow.create(capabilities);

        glWindow.setSize(getScreenWidth(), getScreenHeight());

        //Добавляем слушателей
        glWindow.addGLEventListener(new GameEventListener());
        glWindow.addMouseListener(new MouseInput());
        glWindow.addKeyListener(new KeyInput());

        //Аниматор для обновления glWindow
        FPSAnimator animator = new FPSAnimator(glWindow,60);
        animator.start();

        glWindow.setVisible(true);
    }

    public static void main(String[] args){
        init();
    }
}
