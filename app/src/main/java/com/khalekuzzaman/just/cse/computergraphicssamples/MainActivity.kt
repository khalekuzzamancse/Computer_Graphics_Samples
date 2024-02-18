package com.khalekuzzaman.just.cse.computergraphicssamples
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class MainActivity : ComponentActivity() {
    private lateinit var gLView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            gLView = MyGLSurfaceView(this)
            setContentView(gLView)

    }
}


class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = MyGLRenderer()
        setRenderer(renderer)
       // renderMode = RENDERMODE_WHEN_DIRTY
    }
}


class MyGLRenderer : GLSurfaceView.Renderer {
    private lateinit var mTriangle: Triangle


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.5f, 0.5f, 1.0f)
        mTriangle = Triangle()

    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        mTriangle.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }



}
// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.62200844f, 0.0f,      // top
    -0.5f, -0.31100425f, 0.0f,    // bottom left
    0.5f, -0.31100425f, 0.0f      // bottom right
)

class Triangle {
    private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
    private var mProgram: Int
    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"


    init {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }
    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4
    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(triangleCoords)
                position(0)
            }
        }


    fun draw() {
        GLES20.glUseProgram(mProgram)
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}