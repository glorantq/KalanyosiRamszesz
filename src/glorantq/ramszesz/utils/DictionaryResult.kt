package glorantq.ramszesz.utils

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by glora on 2017. 07. 28..
 */
class DictionaryResult {
    @SerializedName("textProns")
    @Expose
    var textProns: List<Any>? = null

    @SerializedName("sourceDictionary")
    @Expose
    var sourceDictionary: String = ""

    @SerializedName("exampleUses")
    @Expose
    var exampleUses: List<Any>? = null

    @SerializedName("relatedWords")
    @Expose
    var relatedWords: List<Any>? = null

    @SerializedName("labels")
    @Expose
    var labels: List<Any>? = null

    @SerializedName("citations")
    @Expose
    var citations: List<Any>? = null

    @SerializedName("word")
    @Expose
    var word: String = ""

    @SerializedName("partOfSpeech")
    @Expose
    var partOfSpeech: String = ""

    @SerializedName("attributionText")
    @Expose
    var attributionText: String = ""

    @SerializedName("sequence")
    @Expose
    var sequence: String = ""

    @SerializedName("text")
    @Expose
    var text: String = ""

    @SerializedName("score")
    @Expose
    var score: Double = 0.toDouble()
}