package ru.skillbranch.gameofthrones

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import java.lang.IllegalArgumentException

enum class HouseType(
    val title: String,
    @DrawableRes
    val icon: Int,
    @DrawableRes
    val coastOfArms: Int,
    @ColorRes
    val primaryColor: Int,
    @ColorRes
    val accentColor: Int,
    @ColorRes
    val darkColor: Int
) {
    STARK("Stark", R.drawable.stark_icon, R.drawable.stark_coast_of_arms, R.color.stark_primary, R.color.stark_accent, R.color.stark_dark),
    LANNISTER("Lannister", R.drawable.lannister_icon, R.drawable.lannister__coast_of_arms, R.color.lannister_primary, R.color.lannister_accent, R.color.lannister_dark),
    TARGARYEN("Targaryen", R.drawable.targaryen_icon, R.drawable.targaryen_coast_of_arms, R.color.targaryen_primary, R.color.targaryen_accent, R.color.targaryen_dark),
    BARATHEON("Baratheon", R.drawable.baratheon_icon, R.drawable.baratheon_coast_of_arms, R.color.baratheon_primary, R.color.baratheon_accent, R.color.baratheon_dark),
    GREYJOY("Greyjoy", R.drawable.greyjoy_icon, R.drawable.greyjoy_coast_of_arms, R.color.greyjoy_primary, R.color.greyjoy_accent, R.color.greyjoy_dark),
    MARTELL("Martell", R.drawable.martel_icon, R.drawable.martel_coast_of_arms, R.color.martel_primary, R.color.martel_accent, R.color.martel_dark),
    TYRELL("Tyrell", R.drawable.tyrel_icon, R.drawable.tyrel_coast_of_arms, R.color.tyrel_primary, R.color.tyrel_accent, R.color.tyrel_dark);
    companion object {
        fun fromString(title:String): HouseType =
            when (title) {
                "Stark" -> STARK
                "Lannister" -> LANNISTER
                "Targaryen" -> TARGARYEN
                "Baratheon" -> BARATHEON
                "Greyjoy" -> GREYJOY
                "Martell" -> MARTELL
                "Tyrell" -> TYRELL
                else -> throw IllegalArgumentException("Failed house $title")
            }

    }
}