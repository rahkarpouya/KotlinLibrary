package com.ali74.libkot.utils

object ValidateMelliCode {

    private const val COD_SIZE = 10

    fun isCode(number: String): Boolean {
        if (number.length != 10) return false
        val x = tokenizeCodeToArray(number)
        val n = calculateN(x)
        val r = n % 11
        val resultC: Int
        resultC = when (r) {
            0 -> 0
            1 -> 1
            else -> 11 - r
        }
        return resultC == x[COD_SIZE - 1]
    }

    private fun tokenizeCodeToArray(code: String): IntArray {
        val charList = code.toCharArray()
        val result = IntArray(10)
        for (i in charList.indices) {
            result[i] = charList[i].toString().toInt()
        }
        return result
    }

    private fun calculateN(l: IntArray): Int {
        var n = 0
        for (i in 0 until COD_SIZE - 1)
            n += l[i] * (COD_SIZE - i)
        return n
    }
}