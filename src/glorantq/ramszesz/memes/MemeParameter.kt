package glorantq.ramszesz.memes

/**
 * Created by glora on 2017. 07. 26..
 */
class MemeParameter(type: Type, required: Boolean = true) {
    companion object {
        enum class Type {
            STRING, INT, USER
        }
    }

    var type: Type = type
        get() = field
        set(value) {
            field = value
        }

    var value: Any = "Invalid"
        get() = field
        set(value) {
            field = value
            println("Field value has been set to $field ($value)")
        }

    var required: Boolean = required
        get() = field
        set(value) {
            field = value
        }
}