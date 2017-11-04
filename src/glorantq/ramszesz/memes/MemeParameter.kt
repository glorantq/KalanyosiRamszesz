package glorantq.ramszesz.memes

/**
 * Created by glora on 2017. 07. 26..
 */
class MemeParameter(var type: Type, required: Boolean = true) {
    companion object {
        enum class Type {
            STRING, INT, USER
        }
    }

    var value: Any = "Invalid"
        set(value) {
            field = value
            println("Field value has been set to $field ($value)")
        }
}