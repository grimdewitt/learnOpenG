import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import javafx.geometry.Point2D;

public class DrawGraphic {
    public static GL2 gl2=null;

    //Создание одного угла гекса
    public static Point2D hexCorner(Point2D center, float size, int i) {
        float angleDeg = 60 * i + 30;
        float angleRad = (float) (Math.PI / 180 * angleDeg);
        return new Point2D(
                (float) (center.getX() + size * Math.cos(angleRad)),
                (float) (center.getY() + size * Math.sin(angleRad))
        );
    }

    //Создание одной линии для гексагона в центе с точкой
    private static void createOneLine(GL2 gl2, float size, Point2D center, int i) {
        gl2.glBegin(GL2.GL_LINES);
        Point2D point2D = hexCorner(center, size, i);
        gl2.glVertex2f((float) point2D.getX(), (float) point2D.getY());
        point2D = hexCorner(center, size, (i + 1) % 6);
        gl2.glVertex2f((float) point2D.getX(), (float) point2D.getY());
        gl2.glEnd();
    }

    private static void createHighlight4Hex(GL2 gl2,float size,Point2D center){
        gl2.glLineWidth(2f);
        gl2.glColor3f(1f,0f,0f);
        for (int i = 0; i < 6; i++) {
            createOneLine(gl2, size, center, i);
        }
        gl2.glLineWidth(1f);
        gl2.glColor3f(1f,1f,1f);
    }

    private static void highlightOneHex(float size,Point2D center,float dx){
        createHighlight4Hex(gl2,size-0.004f,center);
        createHighlight4Hex(gl2,size+0.004f,center);
    }

    //Создание одного гексагона
    private static void createOneHex(GL2 gl2, float size, Point2D center) {
        for (int i = 0; i < 6; i++) {
            createOneLine(gl2, size, center, i);
        }
    }

    public static void createHexTiles(float size){
        //gl2 = GameEventListener.gl2;

        //Расчет высоты гекса и отступов по вертикали и коризонтали(от соседних)
        float height = size * 2;
        float ver = height * 3 / 4;

        float width = (float) Math.sqrt(3) / 2 * height;
        float horiz = width;

        //Отступы от границ
        float indentHoriz = width;
        float indentVer = ver;

        //Координаты центра первого
        float baseX = -1f + indentHoriz;
        float baseY = 1f - indentVer;

        Point2D center = new Point2D(baseX, baseY);


        //Количество гекосв по х и у
        float Xcount = 2f - width;
        float Ycount = 2f - ver * 2;

        boolean shift = true;
        float tempX = baseX;
        float tempY = baseY;

        //Построение сетки
        int count = 0;
        while (Ycount > 0f) {
            Xcount = 2f - indentHoriz * 2;
            if (shift) {
                tempX = baseX;
            } else {
                tempX = baseX + width / 2;
            }
            //rows
            while (Xcount > 0f) {
                center = new Point2D(tempX, tempY);
                createOneHex(gl2, size, center);
                //highlightOneHex(size, center, 0);
                tempX += horiz;
                Xcount -= width;
            }
            tempY = tempY - ver;
            shift = !shift;
            Ycount -= ver;
            count++;
        }
    }

    public static void createCustomHexTiles(float size,int x, int y){
        //gl2 = GameEventListener.gl2;

        //Расчет высоты гекса и отступов по вертикали и коризонтали(от соседних)
        float height = size * 2;
        float ver = height * 3 / 4;

        float width = (float) Math.sqrt(3) / 2 * height;
        float horiz = width;

        //Отступы от границ
        float indentHoriz = width;
        float indentVer = ver;

        //Координаты центра первого
        float baseX = -1f + indentHoriz;
        float baseY = 1f - indentVer;

        Point2D center = new Point2D(baseX, baseY);


        //Количество гекосв по х и у
        int Xcount = x;
        int Ycount = y;

        boolean shift = true;
        float tempX = baseX;
        float tempY = baseY;

        //Построение сетки
        while (Ycount > 0) {
            Xcount = x;
            if (shift) {
                tempX = baseX;
            } else {
                tempX = baseX + width / 2;
            }
            //rows
            while (Xcount > 0) {
                center = new Point2D(tempX, tempY);
                createOneHex(gl2, size, center);
                //highlightOneHex(size, center, 0);
                tempX += horiz;
                Xcount -= 1;
            }
            tempY = tempY - ver;
            shift = !shift;
            Ycount -= 1;
        }
    }

    private static float rotation = 0;

    /*public static void drawImage(ImageResource image,float x, float y,float width,float height){
        gl2 = GameEventListener.gl2;

        Texture texture = image.getTexture();
        if(texture!=null){
            gl2.glBindTexture(GL2.GL_TEXTURE_2D,texture.getTextureObject());
        }

        //gl2.glTranslatef(x, y, 0);
        //gl2.glRotatef(-rotation,0,0,1);

        gl2.glBegin(GL2.GL_QUADS);

        gl2.glTexCoord2f(0,1);
        gl2.glVertex2f(-1f,-1f);

        gl2.glTexCoord2f(1,1);
        gl2.glVertex2f(1f,-1f);

        gl2.glTexCoord2f(1,0);
        gl2.glVertex2f(1f,1f);

        gl2.glTexCoord2f(0,0);
        gl2.glVertex2f(-1f,1f);

        gl2.glEnd();
        gl2.glFlush();

        //gl2.glTranslatef(x, y, 0);
        //gl2.glRotatef(rotation,0,0,1);

        gl2.glBindTexture(GL2.GL_TEXTURE_2D,0);
    }*/

    public static void setRotation(float r){
        rotation= r;
    }
}
