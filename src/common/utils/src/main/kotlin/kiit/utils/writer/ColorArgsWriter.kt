package kiit.utils.writer

import kiit.common.args.Arg
import kiit.common.args.ArgsWriter

class ColorArgsWriter : ArgsWriter {
    val writer = ConsoleWriter()

    /**
     * prints the arg for command line display
     *
     * -env     :  the environment to run in
     *             ! required  [String]  e.g. dev
     * -log     :  the log level for logging
     *             ? optional  [String]  e.g. info
     * -enc     :  whether encryption is on
     *             ? optional  [String]  e.g. false
     * -region  :  the region linked to app
     *             ? optional  [String]  e.g. us
     *
     * @param tab
     * @param prefix
     * @param separator
     * @param maxWidth
     */
    override fun write(arg: Arg, prefix: String?, separator: String?, maxLength: Int) {
        val nameLen = maxLength?: arg.name.length
        val nameFill = arg.name.padEnd(nameLen)
        val namePart = (prefix ?: "-") + nameFill

        val logs = mutableListOf(
                TextOutput(TextType.Highlight, namePart, false),
                TextOutput(TextType.Text, separator
                        ?: "=", false),
                TextOutput(TextType.Text, arg.desc, true),
                TextOutput(TextType.Text, " ".repeat(nameLen + 6), false))

        if (arg.isRequired) {
            logs.add(TextOutput(TextType.Important, "!", false))
            logs.add(TextOutput(TextType.Text, "required ", false))
        } else {
            logs.add(TextOutput(TextType.Success, "?", false))
            logs.add(TextOutput(TextType.Text, "optional ", false))
        }

        logs.add(TextOutput(TextType.Subtitle, "[${arg.dataType}] ", false))
        logs.add(TextOutput(TextType.Text, "e.g. ${arg.example}", true))
        logs.forEach {entry ->
            writer.write(entry.textType, entry.msg, entry.endLine)
        }
    }
}
