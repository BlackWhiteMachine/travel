package com.example.travel.data.database.elevations

import java.util.*

data class LinearCoordinate<A, B>(var x: A, var y: B)

// Этот код "рисует" все 9 видов отрезков. Наклонные (из начала в конец и из конца в начало каждый), вертикальный и горизонтальный - тоже из начала в конец и из конца в начало, и точку.
private fun sign(x: Int): Int {
    return if (x > 0) 1 else if (x < 0) -1 else 0
    //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
}

/**
 * startCoordinate - начало;
 * finishCoordinate - конец;
 * "g.drawLine (x, y, x, y);" используем в качестве "setPixel (x, y);"
 * Можно писать что-нибудь вроде g.fillRect (x, y, 1, 1);
 */
fun drawBresenhamLine(startCoordinate: LinearCoordinate<Int, Int>, finishCoordinate: LinearCoordinate<Int, Int>): List<LinearCoordinate<Int, Int>> {
    var x: Int
    var y: Int
    var dx: Int
    var dy: Int
    val incx: Int
    val incy: Int
    val pdx: Int
    val pdy: Int
    val es: Int
    val el: Int
    var err: Int

    dx = finishCoordinate.x - startCoordinate.x//проекция на ось икс
    dy = finishCoordinate.y - startCoordinate.y//проекция на ось игрек

    incx = sign(dx)
    /*
     * Определяем, в какую сторону нужно будет сдвигаться. Если dx < 0, т.е. отрезок идёт
     * справа налево по иксу, то incx будет равен -1.
     * Это будет использоваться в цикле постороения.
     */
    incy = sign(dy)
    /*
     * Аналогично. Если рисуем отрезок снизу вверх -
     * это будет отрицательный сдвиг для y (иначе - положительный).
     */

    if (dx < 0) dx = -dx//далее мы будем сравнивать: "if (dx < dy)"
    if (dy < 0) dy = -dy//поэтому необходимо сделать dx = |dx|; dy = |dy|
    //эти две строчки можно записать и так: dx = Math.abs(dx); dy = Math.abs(dy);

    if (dx > dy)
    //определяем наклон отрезка:
    {
        /*
         * Если dx > dy, то значит отрезок "вытянут" вдоль оси икс, т.е. он скорее длинный, чем высокий.
         * Значит в цикле нужно будет идти по икс (строчка el = dx;), значит "протягивать" прямую по иксу
         * надо в соответствии с тем, слева направо и справа налево она идёт (pdx = incx;), при этом
         * по y сдвиг такой отсутствует.
         */
        pdx = incx
        pdy = 0
        es = dy
        el = dx
    } else
    //случай, когда прямая скорее "высокая", чем длинная, т.е. вытянута по оси y
    {
        pdx = 0
        pdy = incy
        es = dx
        el = dy//тогда в цикле будем двигаться по y
    }

    x = startCoordinate.x
    y = startCoordinate.y
    err = el / 2

    //ставим первую точку
    //все последующие точки возможно надо сдвигать, поэтому первую ставим вне цикла

    val result = ArrayList<LinearCoordinate<Int, Int>>()

    result.add(LinearCoordinate(x, y))

    for (t in 0 until el)
    //идём по всем точкам, начиная со второй и до последней
    {
        err -= es
        if (err < 0) {
            err += el
            x += incx//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
            y += incy//или сместить влево-вправо, если цикл проходит по y
        } else {
            x += pdx//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
            y += pdy//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
        }

        result.add(LinearCoordinate(x, y))
    }

    return result
}
