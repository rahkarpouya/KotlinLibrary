package com.ali74.libkot.patternBuilder

import android.content.Context
import android.graphics.Typeface
import android.os.Build

class ReplaceFontApp(private val context: Context, private val fontAll: String) {

    private var sansSerif = ""
    private var sansSerifSmallCaps = ""
    private var monospace = ""
    private var sansSerifLight = ""
    private var sansSerifMedium = ""
    private var sansSerifBlack = ""
    private var serifMonospace = ""
    private var sansSerifCondensed = ""
    private var sansSerifCondensedLight = ""
    private var sansSerifCondensedMedium = ""
    private var sansSerifThin = ""
    private var cursive = ""
    private var casual = ""
    private var serif = ""

    fun setSansSerif(font: String) = apply { sansSerif = font }

    fun setSansSerifSmallCaps(font: String) = apply { sansSerifSmallCaps = font }

    fun setMonospace(font: String) = apply { monospace = font }

    fun setSansSerifLight(font: String) = apply { sansSerifLight = font }

    fun setSansSerifMedium(font: String) = apply { sansSerifMedium = font }

    fun setSansSerifBlack(font: String) = apply { sansSerifBlack = font }

    fun setSerifMonospace(font: String) = apply { serifMonospace = font }

    fun setSansSerifCondensed(font: String) = apply { sansSerifCondensed = font }

    fun setSansSerifCondensedLight(font: String) = apply { sansSerifCondensedLight = font }

    fun setSansSerifCondensedMedium(font: String) = apply { sansSerifCondensedMedium = font }

    fun setSansSerifThin(font: String) = apply { sansSerifThin = font }

    fun setCursive(font: String) = apply { cursive = font }

    fun setCasual(font: String) = apply { casual = font }

    fun setSerif(font: String) = apply { serif = font }

    fun execute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val newMap = HashMap<String, Typeface>()

            newMap["sans-serif"] = if (sansSerif != "")
                Typeface.createFromAsset(context.assets, sansSerif)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["sans-serif-smallcaps"] = if (sansSerifSmallCaps != "")
                Typeface.createFromAsset(context.assets, sansSerifSmallCaps)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["monospace"] = if (monospace != "")
                Typeface.createFromAsset(context.assets, monospace)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["sans-serif-light"] = if (sansSerifLight != "")
                Typeface.createFromAsset(context.assets, sansSerifLight)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["sans-serif-medium"] = if (sansSerifMedium != "")
                Typeface.createFromAsset(context.assets, sansSerifMedium)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["sans-serif-black"] = if (sansSerifBlack != "")
                Typeface.createFromAsset(context.assets, sansSerifBlack)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["serif-monospace"] = if (serifMonospace != "")
                Typeface.createFromAsset(context.assets, serifMonospace)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["sans-serif-condensed"] = if (sansSerifCondensed != "")
                Typeface.createFromAsset(context.assets, sansSerifCondensed)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["sans-serif-condensed-light"] = if (sansSerifCondensedLight != "")
                Typeface.createFromAsset(context.assets, sansSerifCondensedLight)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["sans-serif-condensed-medium"] = if (sansSerifCondensedMedium != "")
                Typeface.createFromAsset(context.assets, sansSerifCondensedMedium)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["sans-serif-thin"] = if (sansSerifThin != "")
                Typeface.createFromAsset(context.assets, sansSerifThin)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["cursive"] = if (cursive != "")
                Typeface.createFromAsset(context.assets, cursive)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["casual"] = if (casual != "")
                Typeface.createFromAsset(context.assets, casual)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            newMap["serif"] = if (serif != "")
                Typeface.createFromAsset(context.assets, serif)
            else
                Typeface.createFromAsset(context.assets, fontAll)

            try {
                val staticField = Typeface::class.java.getDeclaredField("sSystemFontMap")
                staticField.isAccessible = true
                staticField.set(null, newMap)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } else {
            try {
                if (fontAll != "") {
                    val staticField = Typeface::class.java.getDeclaredField(fontAll)
                    staticField.isAccessible = true
                    staticField.set(null, Typeface.createFromAsset(context.assets, fontAll))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}