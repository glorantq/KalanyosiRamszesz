package glorantq.ramszesz.utils

/**
 * Created by glora on 2017. 07. 28..
 */

fun String.tryParseInt(default: Int = 0): Int {
    try {
        return toInt()
    } catch (e: NumberFormatException) {
        return default
    }
}