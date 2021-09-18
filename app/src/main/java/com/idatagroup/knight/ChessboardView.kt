package com.idatagroup.knight

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*


class ChessboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private var gridSize = 6
    private var tilesize:Float = 0f
    private var textPadding:Float = 40f
    private var offset:Float = 0f
    private var knightPos:Point? = null
    private var kingPos:Point? = null
    private var shownMove:ArrayList<Point>? = null

    private val blackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 20.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val redFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
        strokeWidth =5f
    }

    private val knightBitmap = BitmapFactory.decodeResource(context.resources,R.drawable.knight)
    private val kingBitmap = BitmapFactory.decodeResource(context.resources,R.drawable.king)

    init {
        isClickable = true
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        CalculateMetrics()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw Labels
        var x:Float = offset-textPadding/2
        var y:Float = offset+tilesize/2 - (textPaint.descent() + textPaint.ascent()) / 2
        for(i in gridSize downTo 1){
            canvas.drawText(i.toString(), x, y, textPaint)
            y+=tilesize
        }
        y = height-offset+textPadding/2- (textPaint.descent() + textPaint.ascent()) / 2
        x = offset+tilesize/2
        for(i in 1 .. gridSize){
            canvas.drawText(   (64+i).toChar().toString(), x, y, textPaint)
            x+=tilesize
        }
        //draw grid
        x = offset
        y = offset
        var pstart = whitePaint
        for (i in 1..gridSize){
            var p = pstart
            for(j in 1..gridSize){
                canvas.drawRect(x, y, x + tilesize, y + tilesize, p)
                x+=tilesize
                p = if(p==whitePaint) blackPaint else whitePaint
            }
            y+=tilesize
            x=offset
            pstart = if(pstart==whitePaint) blackPaint else whitePaint
        }

        //draw powns
        drawBitmap(canvas,knightBitmap,knightPos)
        drawBitmap(canvas,kingBitmap,kingPos)

        //draw rersult
        if(shownMove != null){
            var p0 = centerForTile(shownMove!![0])
            canvas.drawCircle(p0.x,p0.y,tilesize/4,redFillPaint)
            for(index in 1 .. shownMove!!.size-1){
                var pnext = centerForTile(shownMove!![index])
                canvas.drawCircle(pnext.x,pnext.y,tilesize/4,redFillPaint)
                canvas.drawLine(p0.x,p0.y,pnext.x,p0.y,redFillPaint)
                canvas.drawLine(pnext.x,p0.y,pnext.x,pnext.y,redFillPaint)

                p0 = pnext
            }
        }
    }

    private fun centerForTile(p:Point):PointF{
        val x = offset+p.x*tilesize+tilesize/2
        val y = height-(offset+p.y*tilesize+tilesize/2)
        return PointF(x,y)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int){
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val sz = if (w<h)  w else h
        setMeasuredDimension(sz, sz)
    }

    private fun drawBitmap(canvas:Canvas,b:Bitmap,p:Point?){
        if(p != null) {
            val x = (p!!.x*tilesize+offset+2).toInt()
            val y = height-((p!!.y+1)*tilesize+offset).toInt()+2
            canvas.drawBitmap(
                b,
                Rect(0, 0, b.width, b.height),
                Rect(x,y,(x+tilesize-4).toInt(),(y+tilesize-4).toInt()),
                blackPaint
            )
        }
    }

    private fun CalculateMetrics(){
        if(gridSize != 0){
            tilesize = ((width-2*textPadding)/gridSize).toFloat()
            offset = (width-2*textPadding - tilesize*gridSize)/2+textPadding
        }
    }

    public fun setGridSize(s: Int){
        gridSize = s
        CalculateMetrics()
        invalidate()
    }

    public fun setKnightPos(p:Point?){
        knightPos = p
        invalidate()
    }

    public fun setKingPos(p:Point?){
        kingPos = p
        invalidate()
    }

    public fun setShownMove(m:ArrayList<Point>?){
        shownMove = m
        invalidate()
    }

    public fun chessCoordinatesFromViewCoordinates(p:Point):Point?{
        val y = (height-p.y).toFloat()-offset
        val x = p.x.toFloat()-offset
        if(x<0 || y<0)
            return null
        val X = Math.floor((x/tilesize).toDouble()).toInt()
        val Y = Math.floor((y/tilesize).toDouble()).toInt()
        if(X>=gridSize || Y>=gridSize)
            return null
        return Point(X,Y)
    }


}