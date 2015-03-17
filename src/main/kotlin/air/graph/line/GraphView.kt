package air.graph.line

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.DashPathEffect
import android.graphics.Paint.Align
import android.graphics.Paint.Style

public class GraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    val a = context.obtainStyledAttributes(attributeSet, R.styleable.GraphView)

    val title = a.getString(R.styleable.GraphView_graph_title)
    val lineColor = a.getColor(R.styleable.GraphView_line_color, getResources().getColor(R.color.graph_line))
    val areaColor = a.getColor(R.styleable.GraphView_area_color, getResources().getColor(R.color.graph_area))
    val gridColor = a.getColor(R.styleable.GraphView_grid_color, getResources().getColor(R.color.graph_grid))
    val textColor = a.getColor(R.styleable.GraphView_text_color, getResources().getColor(R.color.graph_text))
    val markColor = a.getColor(R.styleable.GraphView_mark_color, getResources().getColor(R.color.graph_mark))
    val verticalOffset = a.getDimension(R.styleable.GraphView_vertical_offset, 0f)
    val horizontalOffset = a.getDimension(R.styleable.GraphView_horizontal_offset, 0f)

    val titleTextSize = a.getDimension(R.styleable.GraphView_title_text_size, 40f)
    val titleXOffset = a.getDimension(R.styleable.GraphView_title_x_offset, 30f)
    val titleYOffset = a.getDimension(R.styleable.GraphView_title_y_offset, 30f)
    val labelTextSize = a.getDimension(R.styleable.GraphView_label_text_size, 25f)
    val lineStrokeWidth = a.getDimension(R.styleable.GraphView_line_width, 3f)
    val gridStrokeWidth = a.getDimension(R.styleable.GraphView_grid_line_width, 2f)
    val endPointMarkerRadius = a.getDimension(R.styleable.GraphView_end_point_marker_radius, 10f)
    val endPointLabelTextSize = a.getDimension(R.styleable.GraphView_end_point_label_text_size, 60f)
    val endPointLabelXOffset = a.getDimension(R.styleable.GraphView_end_point_label_x_offset, 20f)
    val endPointLabelYOffset = a.getDimension(R.styleable.GraphView_end_point_label_y_offset, 70f)

    var values = listOf<Float>()
    var labels = listOf<String>()
    var endPointLabel: String? = null

    var canvas: Canvas? = null
    var graphWidth: Float = 0f
    var graphHeight: Float = 0f
    var width: Float = 0f
    var height: Float = 0f

    override fun onDraw(canvas: Canvas) {
        this.canvas = canvas
        this.height = getHeight().toFloat()
        this.width = (getWidth() - 1).toFloat()
        this.graphHeight = height - (2 * verticalOffset)
        this.graphWidth = width - horizontalOffset

        drawGrid()

        drawLabels()

        if (!values.isEmpty()) {
            var endPoint = drawArea()

            markLineEnd(endPoint)
        }

        drawTitle()
    }

    private fun drawArea(): Pair<Float, Float> {
        var endPoint = Pair(0f, 0f)

        val max = values.reduce {(memo, element) -> Math.max(memo, element) }
        val min = values.reduce {(memo, element) -> Math.min(memo, element) }
        val diff = max - min
        val columnWidth = (width - horizontalOffset) / values.size()
        val halfColumn = columnWidth / 2
        var prevHeight = 0f

        val linePaint = getBrushPaint(color = lineColor, width = lineStrokeWidth)

        var path = Path()
        path.moveTo(0f, height)

        values.forEachIndexed {(i, value) ->
            val ratio = (value - min) / diff
            val currentHeight = graphHeight * ratio

            val xOffset = (horizontalOffset + 1) + halfColumn
            val startX = ((i - 1) * columnWidth) + xOffset
            val startY = (verticalOffset - prevHeight) + graphHeight
            val stopX = (i * columnWidth) + xOffset
            val stopY = (verticalOffset - currentHeight) + graphHeight

            if (i == 0) {
                path.lineTo(startX, startY)
            }
            path.lineTo(stopX, stopY)

            canvas?.drawLine(startX, startY, stopX, stopY, linePaint)

            prevHeight = currentHeight
            endPoint = Pair(stopX, stopY)
        }
        path.lineTo(width, height)
        path.close()

        val areaPaint = getBrushPaint(color = areaColor, width = lineStrokeWidth, style = Style.FILL)
        canvas?.drawPath(path, areaPaint)

        return endPoint
    }

    private fun markLineEnd(endPoint: Pair<Float, Float>) {
        if (endPointLabel != null) {
            val textPaint = getTextPaint(color = markColor, align = Align.RIGHT, size = endPointLabelTextSize)
            canvas?.drawText(endPointLabel, graphWidth - endPointLabelXOffset, endPointLabelYOffset, textPaint)
        }

        val linePaint = getBrushPaint(color = markColor, width = lineStrokeWidth, style = Style.FILL_AND_STROKE)
        canvas?.drawCircle(endPoint.first, endPoint.second, endPointMarkerRadius, linePaint)

        linePaint.setPathEffect(DashPathEffect(floatArray(10f, 10f), 0f))

        val linePath = Path()
        linePath.moveTo(endPoint.first, endPoint.second)
        linePath.lineTo(endPoint.first, endPointLabelYOffset + 5f)
        canvas?.drawPath(linePath, linePaint)
    }

    private fun drawGrid() {
        val paint = getBrushPaint(color = gridColor, width = gridStrokeWidth)
        (1..3).forEach {
            var y = height / 4 * (it)
            canvas?.drawLine(0f, y, width, y, paint)
        }
    }

    private fun drawLabels() {
        val paint = getTextPaint(color = textColor, size = labelTextSize)
        labels.forEachIndexed {(i, label) ->
            val x = ((graphWidth / labels.size() - 1) * i) + horizontalOffset
            when (i) {
                0 -> {
                    paint.setTextAlign(Align.LEFT)
                }
                labels.size() - 1 -> {
                    paint.setTextAlign(Align.RIGHT)
                }
                else -> {
                    paint.setTextAlign(Align.CENTER)
                }
            }
            canvas?.drawText(label, x, height - labelTextSize, paint)
        }
    }

    private fun drawTitle() {
        val paint = getTextPaint(color = textColor, align = Align.LEFT, size = titleTextSize)
        canvas?.drawText(title, horizontalOffset + titleXOffset, titleTextSize + titleYOffset, paint)
    }

    private fun getTextPaint(color: Int, align: Align = Align.LEFT, size: Float): Paint {
        val paint = Paint()
        paint.setColor(color)
        paint.setTextAlign(align)
        paint.setTextSize(size)
        return paint
    }

    private fun getBrushPaint(color: Int, width: Float, style: Style = Style.STROKE): Paint {
        val paint = Paint()
        paint.setColor(color)
        paint.setStrokeWidth(width)
        paint.setStyle(style)
        return paint
    }
}