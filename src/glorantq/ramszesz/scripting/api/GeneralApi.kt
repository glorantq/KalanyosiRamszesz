package glorantq.ramszesz.scripting.api

import glorantq.ramszesz.Ramszesz
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.ZeroArgFunction

class GeneralApi : TwoArgFunction() {
    override fun call(modname: LuaValue, env: LuaValue): LuaValue {
        env.set("valueOf", ValueOf())
        env.set("consoleLog", ConsoleLog())
        env.set("resetContext", ResetContext())
        env.set("sleep", Sleep())
        return LuaValue.tableOf()
    }

    private class ConsoleLog : VarArgFunction() {
        override fun onInvoke(p0: Varargs): Varargs {
            val string = StringBuilder()
            for(i: Int in 1..p0.narg()) {
                string.append(p0.arg(i).tojstring())
            }

            println(string.toString())
            return LuaValue.NIL
        }
    }

    private class ResetContext : ZeroArgFunction() {
        override fun call(): LuaValue {
            Ramszesz.instance.scriptingContext.resetContext()

            return LuaValue.TRUE
        }
    }

    private class ValueOf : OneArgFunction() {
        override fun call(p0: LuaValue): LuaValue = p0
    }

    private class Sleep : OneArgFunction() {
        override fun call(p0: LuaValue): LuaValue {
            val millis: Long = p0.checklong()
            Thread.sleep(millis)

            return LuaValue.TRUE
        }
    }
}