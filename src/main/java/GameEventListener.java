import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import javafx.geometry.Point2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;

public class GameEventListener implements GLEventListener {
    public static float sizeOfHex = 0.1f;
    public static GL2 gl2;
    private GLU glu;

    //public static ImageResource image = null;

    //Игровые юниты
    public static float units = 10;
    private int program;


    //Координаты положения камеры
    public static float eyeX = 0;
    public static float eyeY = 0;
    public static float eyeZ = 0;

    int[] elements = {
            0, 1, 2,
            2, 3, 0
    };


    /*float vertices[] = {
            0.0f,  0.5f, // Vertex 1 (X, Y)
            0.5f, -0.5f, // Vertex 2 (X, Y)
            -0.5f, -0.5f  // Vertex 3 (X, Y)
    };*/


    float[] vertices = {
            -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, // Top-left
            0.5f,  0.5f, 0.0f, 1.0f, 0.0f, // Top-right
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // Bottom-right
            -0.5f, -0.5f, 1.0f, 1.0f, 1.0f  // Bottom-left
    };


    int uniColor;


    FloatBuffer vertexFB = FloatBuffer.wrap(vertices);
    IntBuffer elementB = IntBuffer.wrap(elements);

    //vbo
    IntBuffer buffers = IntBuffer.allocate(1);
    //vao
    IntBuffer vertexArray = IntBuffer.allocate(1);
    //ea
    IntBuffer elementArray = IntBuffer.allocate(1);
    //tex data
    IntBuffer texArray = IntBuffer.allocate(1);

    //Инициализация gl2 для рисования и выбор цвета фона
    public void init(GLAutoDrawable glAutoDrawable) {
        glu = new GLU();
        gl2 = glAutoDrawable.getGL().getGL2();
        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnable(GL2.GL_TEXTURE_2D);
        gl2.glEnable(GL2.GL_DEPTH_TEST);
        gl2.glDepthFunc(GL2.GL_LEQUAL);
        gl2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);


// Create program.
        program = gl2.glCreateProgram();

// Create vertexShader.
        int vertexShader = gl2.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
        String[] vertexShaderSource = new String[1];
        vertexShaderSource[0] = "#version 330\n" +
                "in vec2 position;\n" +
                "in vec3 color;" +
                "out vec3 Color;" +
                "void main(void)\n" +
                "{\n" +
                "Color = color;\n" +
                "gl_Position = vec4(position, 0.0, 1.0);\n"+
                "}\n";
        gl2.glShaderSource(vertexShader, 1, vertexShaderSource, null);
        gl2.glCompileShader(vertexShader);

// Create and fragment shader.
        int fragmentShader = gl2.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
        String[] fragmentShaderSource = new String[1];
        fragmentShaderSource[0] = "#version 330\n" +
                "uniform vec3 triangleColor;\n" +
                "in vec3 Color;;\n" +
                "out vec4 fColor;\n" +
                "void main(void)\n" +
                "{\n" +
                "fColor = vec4(Color, 1.0);\n" +
                "}\n";
        gl2.glShaderSource(fragmentShader, 1, fragmentShaderSource, null);
        gl2.glCompileShader(fragmentShader);
        //"fColor = vColor;\n" +


        //check if shaders compile successful
        IntBuffer intBuffer = IntBuffer.allocate(1);

        gl2.glGetShaderiv(vertexShader,GL2.GL_COMPILE_STATUS,intBuffer);
        System.out.println("glCompileShader(vertex)="+intBuffer.get(0));

        if(intBuffer.get(0)==GL.GL_FALSE){
            gl2.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl2.glGetProgramInfoLog(program, size, intBuffer, byteBuffer);
                for (byte b : byteBuffer.array()) {
                    System.err.print((char) b);
                }
            } else {
                System.out.println("Unknown");
            }
        }

        gl2.glGetShaderiv(fragmentShader,GL2.GL_COMPILE_STATUS,intBuffer);
        System.out.println("glCompileShader(fragment)="+intBuffer.get(0));

        if(intBuffer.get(0)==GL.GL_FALSE){
            gl2.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl2.glGetProgramInfoLog(program, size, intBuffer, byteBuffer);
                for (byte b : byteBuffer.array()) {
                    System.err.print((char) b);
                }
            } else {
                System.out.println("Unknown");
            }
        }

       /* char[] buffer =new char[512];

        gl2.glGetShaderInfoLog(vertexShader,512 ,0 , buffer);*/

        // Attach shaders to program.
        gl2.glAttachShader(program, vertexShader);
        gl2.glAttachShader(program, fragmentShader);
        gl2.glLinkProgram(program);

        uniColor = gl2.glGetUniformLocation(program, "triangleColor");

        ///
        gl2.glGenBuffers(1, buffers);

        // Create Vertex Array.
        gl2.glGenVertexArrays(1, vertexArray);
        gl2.glBindVertexArray(vertexArray.get(0));

        // Specify how data should be sent to the Program.

        // VertexAttribArray 0 corresponds with location 0 in the vertex shader.
        /*gl2.glEnableVertexAttribArray(0);
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
        gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertexFB.capacity()* Float.BYTES, vertexFB, GL2.GL_STATIC_DRAW);
        gl2.glVertexAttribPointer(0, 2, GL.GL_FLOAT, false, 0, 0);

        // VertexAttribArray 1 corresponds with location 1 in the vertex shader.
        gl2.glEnableVertexAttribArray(1);
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(1));
        gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertexFB.capacity()*Float.BYTES, vertexFB, GL2.GL_STREAM_DRAW);
        gl2.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0, 0);
        */

        //vbo
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
        gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertexFB.capacity()* Float.BYTES, vertexFB, GL_STATIC_DRAW);

        /*//tex
        gl2.glGenBuffers(1, texArray);
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, texArray.get(0));

        //wrap texture
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        //filtering - linear
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);*/

        //color
        /*float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, color);*/



        //ve
        gl2.glGenBuffers(1, elementArray);
        gl2.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementArray.get(0));
        gl2.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementB.capacity()* Integer.BYTES, elementB, GL_STATIC_DRAW);


        int posAttrib = gl2.glGetAttribLocation(program, "position");
        gl2.glEnableVertexAttribArray(posAttrib);
        gl2.glVertexAttribPointer(posAttrib, 2, GL2.GL_FLOAT, false, 5* Float.BYTES, 0);

        int colAttrib = gl2.glGetAttribLocation(program, "color");
        gl2.glEnableVertexAttribArray(colAttrib);
        gl2.glVertexAttribPointer(colAttrib, 3, GL2.GL_FLOAT, false, 5* Float.BYTES, 2* Float.BYTES);

        //image = new ImageResource("C://Users//korid//OneDrive//Desktop//unknown2.png");
        //System.out.println(image.getTexture());
    }

    //Уничтожения
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }



    //Отображние сетки
    public void display(GLAutoDrawable glAutoDrawable) {
        // А МОЖНО МНЕ ЗАХУЯРИТЬСЯ

        //Очистка экрана
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        //Матричный режим - Проекции
        //Загрузка единичной матрицы("обнуление")
        //gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();

        //Положение камеры по y в заивимости от размера экрана.
        //По умолчанию - в два раза , сверху.
        float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / GameEventListener.units);
        gl2.glOrtho(-GameEventListener.units / 2,
                GameEventListener.units / 2,
                -unitsTall / 2,
                unitsTall / 2,
                -1,
                1);

        //Матричный режим - модели
        //gl2.glMatrixMode(GL2.GL_MODELVIEW);

        //Перемещение по x y
        gl2.glTranslatef(-eyeX, -eyeY, eyeZ);
        //Отрисовка сетки

        //Отрисовка кастомной сетки
        /*DrawGraphic.createCustomHexTiles(sizeOfHex , 25, 25);
        sizeOfHex+=0.00001f;*/
        gl2.glUseProgram(program);
        gl2.glBindVertexArray(vertexArray.get(0));
        //gl2.glDrawElements(GL2.GL_TRIANGLES, 3, GL2.GL_UNSIGNED_INT, 0);
        gl2.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_INT, 0);
        //gl2.glDrawArrays(GL.GL_TRIANGLES, 0, 3);

        //Uniforms
        //gl2.glUniform3f(uniColor, 1.0f, 0.0f, 0.0f);


        //Отрисовка изображения
        //DrawGraphic.drawImage(image,0,0,1,1);
        //DrawGraphic.createHexTiles(sizeOfHex);
        //Обратное перемещение




        gl2.glTranslatef(eyeX, eyeY, eyeZ);



        gl2.glFlush();
    }

    boolean flag = true;

    //Изменение размера окна
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        /*GL2 gl2 = glAutoDrawable.getGL().getGL2();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();

        float unitsTall = test.getCanvasHeight() / (test.getCanvasWidth() / test.units);
        gl2.glOrtho(-test.units / 2,
                test.units / 2,
                -unitsTall / 2,
                unitsTall / 2,
                -1,
                1);

        //gl2.glTranslatef(eyeX, 0.0f, 1.0f);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);*/

        /*float aspect = width/height;
        glu.gluPerspective(50.0,aspect,1.0, 100.0);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        glu.gluLookAt(
                0.0, 0.0, 10.0,
                0.0, 0.0 , 0.0,
                0.0, 1.0, 0.0);*/

    }

    //Сохранение окна
    public void save() {
        //GLProfile profile = glCanvas.getGLProfile();//GLProfile.getDefault();
        /*GLCapabilities glCapabilities = new GLCapabilities(profile);
        glCapabilities.setAlphaBits(8);
        GLJPanel pane = new GLJPanel(glCapabilities);
        pane.setOpaque(false);*/

        //glCanvas.get

        //GLContext glContext = glCanvas.getContext();
        AWTGLReadBufferUtil awtglReadBufferUtil = new AWTGLReadBufferUtil(
                gl2.getGLProfile(),
                false);
        BufferedImage image = awtglReadBufferUtil.readPixelsToBufferedImage(
                gl2.getGL(),
                true);
        File outputFile = new File("C:\\netProject\\1.jpg");
        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


/*
* "#version 330\n" +
                "layout(location=0) in vec2 position;\n" +
                "layout(location=1) in vec3 color;\n" +
                "out vec3 vColor;\n" +
                "void main(void)\n" +
                "{\n" +
                "gl_Position = vec4(position, 0.0, 1.0);\n"+
                "vColor = vec4(color, 1.0);\n"+
                "}\n";
*
*
*
*
* "#version 330\n" +
                "in vec4 vColor;\n" +
                "out vec4 fColor;\n" +
                "void main(void)\n" +
                "{\n" +
                "fColor = vColor;\n" +
                "}\n";
*
*
* */
