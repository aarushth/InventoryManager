package com.leopardseal.inventorymanagerapp.ui
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _largeCardIcon: ImageVector? = null
val largeCardIcon: ImageVector
    get() {
        if (_largeCardIcon != null) {
            return _largeCardIcon!!
        }
        _largeCardIcon = ImageVector.Builder(
            name = "Grid_view",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(120f, 440f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                close()
                moveToRelative(0f, 400f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                close()
                moveToRelative(400f, -400f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                close()
                moveToRelative(0f, 400f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                close()
                moveTo(200f, 360f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                horizontalLineTo(200f)
                close()
                moveToRelative(400f, 0f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                horizontalLineTo(600f)
                close()
                moveToRelative(0f, 400f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                horizontalLineTo(600f)
                close()
                moveToRelative(-400f, 0f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                horizontalLineTo(200f)
                close()
                moveToRelative(160f, -400f)
            }
        }.build()
        return _largeCardIcon!!
    }



private var _smallCardIcon: ImageVector? = null
val smallCardIcon: ImageVector
    get() {
        if (_smallCardIcon != null) {
            return _smallCardIcon!!
        }
        _smallCardIcon = ImageVector.Builder(
            name = "Lists",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(80f, 800f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(160f)
                close()
                moveToRelative(240f, 0f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(160f)
                close()
                moveTo(80f, 560f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(160f)
                close()
                moveToRelative(240f, 0f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(160f)
                close()
                moveTo(80f, 320f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(160f)
                close()
                moveToRelative(240f, 0f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(160f)
                close()
            }
        }.build()
        return _smallCardIcon!!
    }


private var _camera: ImageVector? = null
val cameraIcon: ImageVector
    get() {
        if (_camera != null) {
            return _camera!!
        }
        _camera = ImageVector.Builder(
            name = "Photo_camera",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(480f, 700f)
                quadToRelative(75f, 0f, 127.5f, -52.5f)
                reflectiveQuadTo(660f, 520f)
                reflectiveQuadToRelative(-52.5f, -127.5f)
                reflectiveQuadTo(480f, 340f)
                reflectiveQuadToRelative(-127.5f, 52.5f)
                reflectiveQuadTo(300f, 520f)
                reflectiveQuadToRelative(52.5f, 127.5f)
                reflectiveQuadTo(480f, 700f)
                moveToRelative(0f, -80f)
                quadToRelative(-42f, 0f, -71f, -29f)
                reflectiveQuadToRelative(-29f, -71f)
                reflectiveQuadToRelative(29f, -71f)
                reflectiveQuadToRelative(71f, -29f)
                reflectiveQuadToRelative(71f, 29f)
                reflectiveQuadToRelative(29f, 71f)
                reflectiveQuadToRelative(-29f, 71f)
                reflectiveQuadToRelative(-71f, 29f)
                moveTo(160f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(80f, 760f)
                verticalLineToRelative(-480f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(160f, 200f)
                horizontalLineToRelative(126f)
                lineToRelative(74f, -80f)
                horizontalLineToRelative(240f)
                lineToRelative(74f, 80f)
                horizontalLineToRelative(126f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(880f, 280f)
                verticalLineToRelative(480f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(800f, 840f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(640f)
                verticalLineToRelative(-480f)
                horizontalLineTo(638f)
                lineToRelative(-73f, -80f)
                horizontalLineTo(395f)
                lineToRelative(-73f, 80f)
                horizontalLineTo(160f)
                close()
                moveToRelative(320f, -240f)
            }
        }.build()
        return _camera!!
    }



private var _barcode: ImageVector? = null
val barcodeIcon: ImageVector
    get() {
        if (_barcode != null) {
            return _barcode!!
        }
        _barcode = ImageVector.Builder(
            name = "Barcode_scanner",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(40f, 840f)
                verticalLineToRelative(-200f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(120f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(680f, 0f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-120f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(200f)
                close()
                moveTo(160f, 720f)
                verticalLineToRelative(-480f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(480f)
                close()
                moveToRelative(120f, 0f)
                verticalLineToRelative(-480f)
                horizontalLineToRelative(40f)
                verticalLineToRelative(480f)
                close()
                moveToRelative(120f, 0f)
                verticalLineToRelative(-480f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(480f)
                close()
                moveToRelative(120f, 0f)
                verticalLineToRelative(-480f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(480f)
                close()
                moveToRelative(160f, 0f)
                verticalLineToRelative(-480f)
                horizontalLineToRelative(40f)
                verticalLineToRelative(480f)
                close()
                moveToRelative(80f, 0f)
                verticalLineToRelative(-480f)
                horizontalLineToRelative(40f)
                verticalLineToRelative(480f)
                close()
                moveTo(40f, 320f)
                verticalLineToRelative(-200f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(80f)
                horizontalLineTo(120f)
                verticalLineToRelative(120f)
                close()
                moveToRelative(800f, 0f)
                verticalLineToRelative(-120f)
                horizontalLineTo(720f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(200f)
                close()
            }
        }.build()
        return _barcode!!
    }


private var _box: ImageVector? = null
val boxIcon: ImageVector
    get() {
        if (_box!= null) {
            return _box!!
        }
        _box = ImageVector.Builder(
            name = "BoxSeam",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.2f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(8.186f, 1.113f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.372f, 0f)
                lineTo(1.846f, 3.5f)
                lineToRelative(2.404f, 0.961f)
                lineTo(10.404f, 2f)
                close()
                moveToRelative(3.564f, 1.426f)
                lineTo(5.596f, 5f)
                lineTo(8f, 5.961f)
                lineTo(14.154f, 3.5f)
                close()
                moveToRelative(3.25f, 1.7f)
                lineToRelative(-6.5f, 2.6f)
                verticalLineToRelative(7.922f)
                lineToRelative(6.5f, -2.6f)
                verticalLineTo(4.24f)
                close()
                moveTo(7.5f, 14.762f)
                verticalLineTo(6.838f)
                lineTo(1f, 4.239f)
                verticalLineToRelative(7.923f)
                close()
                moveTo(7.443f, 0.184f)
                arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.114f, 0f)
                lineToRelative(7.129f, 2.852f)
                arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16f, 3.5f)
                verticalLineToRelative(8.662f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.629f, 0.928f)
                lineToRelative(-7.185f, 2.874f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.372f, 0f)
                lineTo(0.63f, 13.09f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.63f, -0.928f)
                verticalLineTo(3.5f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.314f, -0.464f)
                close()
            }
        }.build()
        return _box!!
    }


private var _item: ImageVector? = null
val itemIcon: ImageVector
    get() {
        if (_item != null) {
            return _item!!
        }
        _item = ImageVector.Builder(
            name = "Shape_line",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(240f, 440f)
                quadToRelative(-83f, 0f, -141.5f, -58.5f)
                reflectiveQuadTo(40f, 240f)
                quadToRelative(0f, -84f, 58.5f, -142f)
                reflectiveQuadTo(240f, 40f)
                quadToRelative(84f, 0f, 142f, 58f)
                reflectiveQuadToRelative(58f, 142f)
                quadToRelative(0f, 83f, -58f, 141.5f)
                reflectiveQuadTo(240f, 440f)
                moveToRelative(0f, -80f)
                quadToRelative(51f, 0f, 85.5f, -35f)
                reflectiveQuadToRelative(34.5f, -85f)
                quadToRelative(0f, -51f, -34.5f, -85.5f)
                reflectiveQuadTo(240f, 120f)
                quadToRelative(-50f, 0f, -85f, 34.5f)
                reflectiveQuadTo(120f, 240f)
                quadToRelative(0f, 50f, 35f, 85f)
                reflectiveQuadToRelative(85f, 35f)
                moveTo(640f, 920f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(560f, 840f)
                verticalLineToRelative(-200f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(640f, 560f)
                horizontalLineToRelative(200f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(920f, 640f)
                verticalLineToRelative(200f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(840f, 920f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(-200f)
                horizontalLineTo(640f)
                close()
                moveToRelative(69f, -532f)
                lineTo(308f, 708f)
                quadToRelative(5f, 12f, 8.5f, 25f)
                reflectiveQuadToRelative(3.5f, 27f)
                quadToRelative(0f, 50f, -34.5f, 85f)
                reflectiveQuadTo(200f, 880f)
                quadToRelative(-50f, 0f, -85f, -35f)
                reflectiveQuadToRelative(-35f, -85f)
                quadToRelative(0f, -51f, 35f, -85.5f)
                reflectiveQuadToRelative(85f, -34.5f)
                quadToRelative(14f, 0f, 27f, 3.5f)
                reflectiveQuadToRelative(25f, 8.5f)
                lineToRelative(400f, -401f)
                quadToRelative(-5f, -12f, -8.5f, -24.5f)
                reflectiveQuadTo(640f, 200f)
                quadToRelative(0f, -51f, 35f, -85.5f)
                reflectiveQuadToRelative(85f, -34.5f)
                quadToRelative(51f, 0f, 85.5f, 34.5f)
                reflectiveQuadTo(880f, 200f)
                quadToRelative(0f, 50f, -34.5f, 85f)
                reflectiveQuadTo(760f, 320f)
                quadToRelative(-14f, 0f, -26.5f, -3.5f)
                reflectiveQuadTo(709f, 308f)
            }
        }.build()
        return _item!!
    }


private var _org: ImageVector? = null
val orgIcon: ImageVector
    get() {
        if (_org != null) {
            return _org!!
        }
        _org = ImageVector.Builder(
            name = "Organization",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(9.111f, 4.663f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 6.89f, 1.337f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.222f, 3.326f)
                close()
                moveToRelative(-0.555f, -2.494f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 7.444f, 3.83f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.112f, -1.66f)
                close()
                moveToRelative(2.61f, 0.03f)
                arcToRelative(1.494f, 1.494f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.895f, 0.188f)
                arcToRelative(1.513f, 1.513f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.487f, 2.46f)
                arcToRelative(1.492f, 1.492f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.635f, -0.326f)
                arcToRelative(1.512f, 1.512f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.228f, -2.321f)
                close()
                moveToRelative(0.48f, 1.61f)
                arcToRelative(0.499f, 0.499f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0.705f, -0.708f)
                arcToRelative(0.509f, 0.509f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.351f, -0.15f)
                arcToRelative(0.499f, 0.499f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.5f, 0.503f)
                arcToRelative(0.51f, 0.51f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.146f, 0.356f)
                close()
                moveTo(3.19f, 12.487f)
                horizontalLineTo(5f)
                verticalLineToRelative(1.005f)
                horizontalLineTo(3.19f)
                arcToRelative(1.197f, 1.197f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.842f, -0.357f)
                arcToRelative(1.21f, 1.21f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.348f, -0.85f)
                verticalLineToRelative(-1.81f)
                arcToRelative(0.997f, 0.997f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.71f, -0.332f)
                arcTo(1.007f, 1.007f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, 9.408f)
                verticalLineTo(7.226f)
                curveToRelative(0.003f, -0.472f, 0.19f, -0.923f, 0.52f, -1.258f)
                curveToRelative(0.329f, -0.331f, 0.774f, -0.52f, 1.24f, -0.523f)
                horizontalLineTo(4.6f)
                arcToRelative(2.912f, 2.912f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.55f, 1.006f)
                horizontalLineTo(2.76f)
                arcToRelative(0.798f, 0.798f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.54f, 0.232f)
                arcToRelative(0.777f, 0.777f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.22f, 0.543f)
                verticalLineToRelative(2.232f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(2.826f)
                arcToRelative(0.202f, 0.202f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.05f, 0.151f)
                arcToRelative(0.24f, 0.24f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.14f, 0.05f)
                close()
                moveToRelative(7.3f, -6.518f)
                arcToRelative(1.765f, 1.765f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.25f, -0.523f)
                horizontalLineTo(6.76f)
                arcToRelative(1.765f, 1.765f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.24f, 0.523f)
                curveToRelative(-0.33f, 0.335f, -0.517f, 0.786f, -0.52f, 1.258f)
                verticalLineToRelative(3.178f)
                arcToRelative(1.06f, 1.06f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.29f, 0.734f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.71f, 0.332f)
                verticalLineToRelative(2.323f)
                arcToRelative(1.202f, 1.202f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.35f, 0.855f)
                curveToRelative(0.18f, 0.168f, 0.407f, 0.277f, 0.65f, 0.312f)
                horizontalLineToRelative(2f)
                arcToRelative(1.15f, 1.15f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, -1.167f)
                verticalLineTo(11.47f)
                arcToRelative(0.997f, 0.997f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.71f, -0.332f)
                arcToRelative(1.006f, 1.006f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.29f, -0.734f)
                verticalLineTo(7.226f)
                arcToRelative(1.8f, 1.8f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.51f, -1.258f)
                close()
                moveTo(10f, 10.454f)
                horizontalLineTo(9f)
                verticalLineToRelative(3.34f)
                arcToRelative(0.202f, 0.202f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.06f, 0.14f)
                arcToRelative(0.17f, 0.17f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.14f, 0.06f)
                horizontalLineTo(7.19f)
                arcToRelative(0.21f, 0.21f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.2f, -0.2f)
                verticalLineToRelative(-3.34f)
                horizontalLineTo(6f)
                verticalLineTo(7.226f)
                curveToRelative(0f, -0.203f, 0.079f, -0.398f, 0.22f, -0.543f)
                arcToRelative(0.798f, 0.798f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.54f, -0.232f)
                horizontalLineToRelative(2.48f)
                arcToRelative(0.778f, 0.778f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.705f, 0.48f)
                arcToRelative(0.748f, 0.748f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.055f, 0.295f)
                verticalLineToRelative(3.228f)
                close()
                moveToRelative(2.81f, 3.037f)
                horizontalLineTo(11f)
                verticalLineToRelative(-1.005f)
                horizontalLineToRelative(1.8f)
                arcToRelative(0.24f, 0.24f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.14f, -0.05f)
                arcToRelative(0.2f, 0.2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.06f, -0.152f)
                verticalLineTo(9.458f)
                horizontalLineToRelative(1f)
                verticalLineTo(7.226f)
                arcToRelative(0.777f, 0.777f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.22f, -0.543f)
                arcToRelative(0.798f, 0.798f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.54f, -0.232f)
                horizontalLineToRelative(-1.29f)
                arcToRelative(2.91f, 2.91f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.55f, -1.006f)
                horizontalLineToRelative(1.84f)
                arcToRelative(1.77f, 1.77f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.24f, 0.523f)
                curveToRelative(0.33f, 0.335f, 0.517f, 0.786f, 0.52f, 1.258f)
                verticalLineToRelative(2.182f)
                curveToRelative(0f, 0.273f, -0.103f, 0.535f, -0.289f, 0.733f)
                curveToRelative(-0.186f, 0.199f, -0.44f, 0.318f, -0.711f, 0.333f)
                verticalLineToRelative(1.81f)
                curveToRelative(0f, 0.319f, -0.125f, 0.624f, -0.348f, 0.85f)
                arcToRelative(1.197f, 1.197f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.842f, 0.357f)
                close()
                moveTo(4f, 1.945f)
                arcToRelative(1.494f, 1.494f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.386f, 0.932f)
                arcTo(1.517f, 1.517f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.94f, 4.52f)
                arcTo(1.497f, 1.497f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5.5f, 3.454f)
                curveToRelative(0f, -0.4f, -0.158f, -0.784f, -0.44f, -1.067f)
                arcTo(1.496f, 1.496f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4f, 1.945f)
                close()
                moveToRelative(0f, 2.012f)
                arcToRelative(0.499f, 0.499f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.5f, -0.503f)
                arcToRelative(0.504f, 0.504f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.5f, -0.503f)
                arcToRelative(0.509f, 0.509f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.5f, 0.503f)
                arcToRelative(0.504f, 0.504f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.5f, 0.503f)
                close()
            }
        }.build()
        return _org!!
    }


private var _subtract: ImageVector? = null
val subtractIcon: ImageVector
    get() {
        if (_subtract != null) {
            return _subtract!!
        }
        _subtract = ImageVector.Builder(
            name = "Check_indeterminate_small",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(240f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(480f)
                verticalLineToRelative(80f)
                close()
            }
        }.build()
        return _subtract!!
    }

val Filter_alt: ImageVector
    get() {
        if (_Filter_alt != null) return _Filter_alt!!

        _Filter_alt = ImageVector.Builder(
            name = "Filter_alt",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(440f, 800f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(400f, 760f)
                verticalLineToRelative(-240f)
                lineTo(168f, 224f)
                quadToRelative(-15f, -20f, -4.5f, -42f)
                reflectiveQuadToRelative(36.5f, -22f)
                horizontalLineToRelative(560f)
                quadToRelative(26f, 0f, 36.5f, 22f)
                reflectiveQuadToRelative(-4.5f, 42f)
                lineTo(560f, 520f)
                verticalLineToRelative(240f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(520f, 800f)
                close()
                moveToRelative(40f, -308f)
                lineToRelative(198f, -252f)
                horizontalLineTo(282f)
                close()
                moveToRelative(0f, 0f)
            }
        }.build()

        return _Filter_alt!!
    }

private var _Filter_alt: ImageVector? = null






